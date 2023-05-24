package com.example.ec.resources;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.auth.AuthenticationRequest;
import com.example.ec.auth.AuthenticationResponse;
import com.example.ec.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthenticationResource {

    private final AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }

}
