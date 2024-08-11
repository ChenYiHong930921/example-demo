package com.chenyihong.plugin.entity;

public class KeystoreParamsEntity {

    private final String saveFolder;

    private final String keystoreFileName;

    private final String keystorePassword;

    private final String keyAliasName;

    private final String keyAliasPassword;

    public KeystoreParamsEntity(String saveFolder, String keystoreFileName, String keystorePassword, String keyAliasName, String keyAliasPassword) {
        this.saveFolder = saveFolder;
        this.keystoreFileName = keystoreFileName;
        this.keystorePassword = keystorePassword;
        this.keyAliasName = keyAliasName;
        this.keyAliasPassword = keyAliasPassword;
    }

    public String getSaveFolder() {
        return saveFolder;
    }

    public String getKeystoreFileName() {
        return keystoreFileName;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeyAliasName() {
        return keyAliasName;
    }

    public String getKeyAliasPassword() {
        return keyAliasPassword;
    }

    @Override
    public String toString() {
        return "KeystoreParamsEntity{" +
                "saveFolder='" + saveFolder + '\'' +
                ", keystoreFileName='" + keystoreFileName + '\'' +
                ", keystorePassword='" + keystorePassword + '\'' +
                ", keyAliasName='" + keyAliasName + '\'' +
                ", keyAliasPassword='" + keyAliasPassword + '\'' +
                '}';
    }
}
