package com.ahnaf.ssd.oauth.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ahnaf.ssd.oauth.model.FileHandling;
import com.ahnaf.ssd.oauth.service.GDriveService;

@Controller
public class MainController {

    @Autowired
    GDriveService gDriveService;

    private static Logger log = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String showIndex() throws IOException {
        return gDriveService.isOAUserAuthenticated() ? "main.html" : "home.html";
    }

    @GetMapping("/signingoogle")
    public void goGoogleSignIn(HttpServletResponse response) throws IOException {
        gDriveService.oaGoogleSignIn(response);
    }

    @GetMapping("/oauth")
    public String storeCredentialsFromGoogle(HttpServletRequest request) throws IOException {
        return gDriveService.isStoredAuthorizationCode(request) ? "main.html" : "home.html";
    }

    @PostMapping("/upload")
    public String uploadFile(HttpServletRequest servletRequest, @ModelAttribute FileHandling file)
            throws IllegalStateException, IOException {
        gDriveService.uploadFileToGDrive(file.getOaMultipartFile());
        return "main.html";
    }

    @GetMapping("/applogout")
    public String getLogoutPage(HttpServletRequest request) throws IOException {
        gDriveService.appLogout(request);
        return "home.html/";
    }
}
