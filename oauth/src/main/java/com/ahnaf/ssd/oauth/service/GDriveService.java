package com.ahnaf.ssd.oauth.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.ahnaf.ssd.oauth.util.ApplicationConfig;

@Service
public class GDriveService {

    @Autowired
    private ApplicationConfig applicationConfig;

    private static Logger log = LoggerFactory.getLogger(GDriveService.class);

    // Used for google APIs REST  calls
    private static HttpTransport OA_HTTP_TRANSPORT = new NetHttpTransport();

    // serialize/de-serialize responses
    private static JsonFactory OA_JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // scopes we access in the Google Drive
    private static final List<String> OA_SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    // identifier of  user
    private static final String OA_USER_IDENTIFIER_KEY = "MY_APP_USER";

    private static final String OA_APPLICATION_NAME = "SSD OAuth App";

    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;

    private Drive gdrive;

    @PostConstruct
    public void init() throws IOException {
        log.info("Init Started ...");
        GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(OA_JSON_FACTORY,
                new InputStreamReader(applicationConfig.getOaGDSecretKeys().getInputStream()));
        log.info("Fetched Secret...");
        googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(OA_HTTP_TRANSPORT, OA_JSON_FACTORY, googleClientSecrets, OA_SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(applicationConfig.getOaCredentialsFolder().getFile())).build();
        gdrive = new Drive.Builder(OA_HTTP_TRANSPORT, OA_JSON_FACTORY, getOACredential()).setApplicationName(OA_APPLICATION_NAME)
                .build();
    }

    public Credential getOACredential() throws IOException {
        return googleAuthorizationCodeFlow.loadCredential(OA_USER_IDENTIFIER_KEY);
    }

    public boolean isOAUserAuthenticated() throws IOException {

        Credential oaCredential = getOACredential();
        boolean isTokenValid = false;
        if (oaCredential != null) {
            isTokenValid = oaCredential.refreshToken();
            return isTokenValid;
        }
        return isTokenValid;
    }

    public void oaGoogleSignIn(HttpServletResponse response) throws IOException {
        GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl = googleAuthorizationCodeFlow.newAuthorizationUrl();
        String redirectURL = googleAuthorizationCodeRequestUrl.setRedirectUri(applicationConfig.getOA_CALLBACK_URI()).setAccessType("offline").build();
        response.sendRedirect(redirectURL);
    }

    public boolean isStoredAuthorizationCode(HttpServletRequest request) throws IOException {
        String codes = request.getParameter("code");

        if (codes != null) {
            saveOAToken(codes);
            return true;
        }
        return false;
    }

    private void saveOAToken(String code) throws IOException {
        GoogleTokenResponse googleTokenResponse = googleAuthorizationCodeFlow.newTokenRequest(code).setRedirectUri(applicationConfig.getOA_CALLBACK_URI()).execute();
        googleAuthorizationCodeFlow.createAndStoreCredential(googleTokenResponse, OA_USER_IDENTIFIER_KEY);
    }

    public void uploadFileToGDrive(MultipartFile multipartFile) throws IllegalStateException, IOException {
        try {
            String originalFileName = multipartFile.getOriginalFilename();
            String fileContentType = multipartFile.getContentType();

            String tempPath = applicationConfig.getOaTempFolder();

            File copyFile = new File(tempPath, originalFileName);

            multipartFile.transferTo(copyFile);

            com.google.api.services.drive.model.File metaDataFile = new com.google.api.services.drive.model.File();
            metaDataFile.setName(originalFileName);
            FileContent fileContent = new FileContent(fileContentType, copyFile);

            com.google.api.services.drive.model.File verifyFile = gdrive.files().create(metaDataFile, fileContent)
                    .setFields("id").execute();
            log.info("Created File: " + verifyFile.getId());
        }catch (Exception e){

        }
    }

    public void appLogout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        session = request.getSession(true);
        if (session != null) {
            session.invalidate();
            log.info("User Logged OUT ...");
        }
    }
}
