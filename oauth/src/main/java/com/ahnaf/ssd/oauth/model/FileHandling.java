package com.ahnaf.ssd.oauth.model;

import org.springframework.web.multipart.MultipartFile;

public class FileHandling {

    private static final long oaSerialVersionUID = 1L;
    private MultipartFile oaMultipartFile;

    public MultipartFile getOaMultipartFile() {
        return oaMultipartFile;
    }

    public void setOaMultipartFile(MultipartFile oaMultipartFile) {
        this.oaMultipartFile = oaMultipartFile;
    }

    public static long getOASerialversionuid() {
        return oaSerialVersionUID;
    }
}
