package com.ahnaf.ssd.oauth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties
public class ApplicationConfig {

    @Value("${google.oauth.callback.url}")
    private String OA_CALLBACK_URI;

    @Value("${google.secret.key.path}")
    private Resource oaGDSecretKeys;

    @Value("${google.credentials.folder.path}")
    private Resource oaCredentialsFolder;

    @Value("${uploadapp.temp.path}")
    private String oaTempFolder;

    public String getOA_CALLBACK_URI() {
        return OA_CALLBACK_URI;
    }

    public void setOA_CALLBACK_URI(String cALLBACK_URI) {
        OA_CALLBACK_URI = cALLBACK_URI;
    }

    public Resource getOaGDSecretKeys() {
        return this.oaGDSecretKeys;
    }

    public void setOaGDSecretKeys(Resource oaGDSecretKeys) {
        this.oaGDSecretKeys = oaGDSecretKeys;
    }

    public Resource getOaCredentialsFolder() {
        return oaCredentialsFolder;
    }

    public void setOaCredentialsFolder(Resource oaCredentialsFolder) {
        this.oaCredentialsFolder = oaCredentialsFolder;
    }

    public String getOaTempFolder() {
        return oaTempFolder;
    }

    public void setOaTempFolder(String oaTempFolder) {
        this.oaTempFolder = oaTempFolder;
    }



}