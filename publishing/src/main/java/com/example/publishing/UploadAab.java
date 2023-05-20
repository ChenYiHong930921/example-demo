package com.example.publishing;

import com.example.publishing.constant.Channel;
import com.example.publishing.constant.FileType;
import com.example.publishing.constant.PublishStatus;
import com.example.publishing.timeout.TimeoutCredentialsAdapter;
import com.example.publishing.timeout.TimeoutNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.Bundle;
import com.google.api.services.androidpublisher.model.LocalizedText;
import com.google.api.services.androidpublisher.model.Track;
import com.google.api.services.androidpublisher.model.TrackRelease;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class UploadAab {

    /**
     * 应用名
     */
    public static final String APPLICATION_NAME = "PublishTest";

    /**
     * 应用包名
     */
    public static final String PACKAGE_NAME = "com.chenyihong.exampledemo";

    /**
     * aab存放路径
     */
    public static final String AAB_FILE_PATH = "ExampleDemo.aab";

    /**
     * 服务账号配置文件存放路径
     */
    public static final String SERVER_CLIENT_PATH = "server_client.json";

    /**
     * 发布的渠道
     */
    public static final String CHOSEN_CHANNEL = Channel.TEST_INTERNAL;

    /**
     * 应用内更新优先级0-5
     */
    public static final int IN_APP_UPDATE_PRIORITY = 0;

    /**
     * 发布状态
     */
    public static final String PUBLISH_STATUS = PublishStatus.DRAFT;

    /**
     * 分阶段发布比例
     */
    public static final double USER_FRACTION = 0.05;

    /**
     * 更新版本名
     */
    public static final String RELEASE_NAME = "test upload from java";

    /**
     * 更新内容文本
     */
    public static final String RELEASE_NOTE = "this is test for upload aab";

    /**
     * 超时时间
     */
    private static final int TIMEOUT = 2 * 60 * 1000;

    public static void main(String[] args) {
        InputStream serverClientStream = null;
        try {
            ClassLoader classLoader = UploadAab.class.getClassLoader();
            if (classLoader == null) {
                System.out.println("classLoader is null");
                return;
            }
            NetHttpTransport httpTransport = TimeoutNetHttpTransport.newTimeoutTransport(TIMEOUT);
            serverClientStream = classLoader.getResourceAsStream(SERVER_CLIENT_PATH);
            URL aabResource = classLoader.getResource(AAB_FILE_PATH);
            System.out.println("start upload");
            if (serverClientStream != null && aabResource != null) {
                // 获取凭据
                GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serverClientStream).createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));
                if (googleCredentials != null) {
                    if (googleCredentials.getAccessToken() == null) {
                        googleCredentials.refresh();
                    }
                    TimeoutCredentialsAdapter httpCredentialsAdapter = new TimeoutCredentialsAdapter(googleCredentials, TIMEOUT);
                    AndroidPublisher publisher = new AndroidPublisher.Builder(httpTransport, GsonFactory.getDefaultInstance(), httpCredentialsAdapter)
                            .setApplicationName(APPLICATION_NAME)
                            .build();
                    System.out.println("start publishing");
                    // 创建一个新的更改
                    AndroidPublisher.Edits edits = publisher.edits();
                    AndroidPublisher.Edits.Insert editRequest = edits.insert(PACKAGE_NAME, null);
                    String editId = editRequest.execute().getId();
                    System.out.println("editId:" + editId);
                    // 上传aab
                    Bundle uploadBundle = edits.bundles().upload(PACKAGE_NAME, editId, new FileContent(FileType.AAB, new File(aabResource.toURI().getPath()))).execute();
                    Long uploadBundleVersionCode = (long) uploadBundle.getVersionCode();
                    // 设置发布渠道
                    Track track = new Track();
                    track.setTrack(CHOSEN_CHANNEL);
                    // 设置更新相关信息
                    TrackRelease trackRelease = new TrackRelease();
                    // 设置更新版本号
                    trackRelease.setVersionCodes(Collections.singletonList(uploadBundleVersionCode));
                    // 设置更新版本名
                    trackRelease.setName(RELEASE_NAME);
                    // 设置更新内容
                    trackRelease.setReleaseNotes(Collections.singletonList(new LocalizedText().setText(RELEASE_NOTE)));
                    // 设置应用内更新优先级（0-5，等级4、5为强制更新）
                    trackRelease.setInAppUpdatePriority(IN_APP_UPDATE_PRIORITY);
                    // 设置发布状态
                    trackRelease.setStatus(PUBLISH_STATUS);
                    if (PublishStatus.IN_PROGRESS.equals(trackRelease.getStatus()) || PublishStatus.HALTED.equals(trackRelease.getStatus())) {
                        // 分阶段发布时，设置发布比例
                        trackRelease.setUserFraction(USER_FRACTION);
                    }
                    track.setReleases(Collections.singletonList(trackRelease));
                    edits.tracks().update(PACKAGE_NAME, editId, track.getTrack(), track).execute();
                    // 提交此次更改
                    edits.commit(PACKAGE_NAME, editId).execute();
                    System.out.println("------end upload--------");
                }
            }
        } catch (GeneralSecurityException | IOException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("upload failed message:" + e.getMessage());
        } finally {
            try {
                if (serverClientStream != null) {
                    serverClientStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("upload failed message:" + e.getMessage());
            }
        }
    }
}
