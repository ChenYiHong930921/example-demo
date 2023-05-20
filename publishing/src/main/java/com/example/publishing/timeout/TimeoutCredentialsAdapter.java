package com.example.publishing.timeout;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.util.Preconditions;
import com.google.auth.Credentials;
import com.google.auth.http.AuthHttpConstants;
import com.google.auth.http.HttpCredentialsAdapter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class TimeoutCredentialsAdapter implements HttpRequestInitializer, HttpUnsuccessfulResponseHandler {

    private static final Logger LOGGER = Logger.getLogger(HttpCredentialsAdapter.class.getName());

    private static final Pattern INVALID_TOKEN_ERROR = Pattern.compile("\\s*error\\s*=\\s*\"?invalid_token\"?");

    static final String BEARER_PREFIX = AuthHttpConstants.BEARER + " ";

    private final Credentials credentials;

    private final int timeout;

    public TimeoutCredentialsAdapter(Credentials credentials, int timeout) {
        Preconditions.checkNotNull(credentials);
        this.credentials = credentials;
        this.timeout = timeout;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        request.setUnsuccessfulResponseHandler(this);
        if (timeout != 0) {
            request.setConnectTimeout(timeout);
            request.setReadTimeout(timeout);
            request.setWriteTimeout(timeout);
        }
        if (!credentials.hasRequestMetadata()) {
            return;
        }
        HttpHeaders requestHeaders = request.getHeaders();
        URI uri = null;
        if (request.getUrl() != null) {
            uri = request.getUrl().toURI();
        }
        Map<String, List<String>> credentialHeaders = credentials.getRequestMetadata(uri);
        if (credentialHeaders == null) {
            return;
        }
        for (Map.Entry<String, List<String>> entry : credentialHeaders.entrySet()) {
            String headerName = entry.getKey();
            List<String> requestValues = new ArrayList<>(entry.getValue());
            requestHeaders.put(headerName, requestValues);
        }
    }

    @Override
    public boolean handleResponse(HttpRequest request, HttpResponse response, boolean supportsRetry) throws IOException {
        boolean refreshToken = false;
        boolean bearer = false;

        List<String> authenticateList = response.getHeaders().getAuthenticateAsList();

        // if authenticate list is not null we will check if one of the entries contains "Bearer"
        if (authenticateList != null) {
            for (String authenticate : authenticateList) {
                if (authenticate.startsWith(BEARER_PREFIX)) {
                    // mark that we found a "Bearer" value, and check if there is a invalid_token error
                    bearer = true;
                    refreshToken = INVALID_TOKEN_ERROR.matcher(authenticate).find();
                    break;
                }
            }
        }

        // if "Bearer" wasn't found, we will refresh the token, if we got 401
        if (!bearer) {
            refreshToken = response.getStatusCode() == HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
        }

        if (refreshToken) {
            try {
                credentials.refresh();
                initialize(request);
                return true;
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "unable to refresh token", exception);
            }
        }
        return false;
    }
}
