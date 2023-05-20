package com.example.publishing.timeout;

import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.mtls.MtlsProvider;
import com.google.api.client.googleapis.mtls.MtlsUtils;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class TimeoutNetHttpTransport {

    public static NetHttpTransport newTimeoutTransport(int timeout) throws GeneralSecurityException, IOException {
        MtlsProvider mtlsProvider = MtlsUtils.getDefaultMtlsProvider();
        KeyStore mtlsKeyStore = null;
        String mtlsKeyStorePassword = null;
        if (mtlsProvider.useMtlsClientCertificate()) {
            mtlsKeyStore = mtlsProvider.getKeyStore();
            mtlsKeyStorePassword = mtlsProvider.getKeyStorePassword();
        }
        if (mtlsKeyStore != null && mtlsKeyStorePassword != null) {
            return new NetHttpTransport.Builder()
                    .trustCertificates(GoogleUtils.getCertificateTrustStore(), mtlsKeyStore, mtlsKeyStorePassword)
                    .setConnectionFactory(new TimeoutConnectionFactory(timeout))
                    .build();
        }
        return new NetHttpTransport.Builder()
                .trustCertificates(GoogleUtils.getCertificateTrustStore())
                .setConnectionFactory(new TimeoutConnectionFactory(timeout))
                .build();
    }
}
