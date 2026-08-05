package com.douglaasph.clinic_api.adapter.inbound.controller.auth;

import com.douglaasph.clinic_api.adapter.inbound.controller.auth.request.LoginGoogleRequest;
import com.douglaasph.clinic_api.adapter.inbound.controller.auth.request.LoginRequest;
import com.douglaasph.clinic_api.adapter.inbound.controller.auth.response.LoginResponse;
import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.ports.inbound.AuthenticateGoogleUserUseCasePort;
import com.douglaasph.clinic_api.domain.ports.inbound.AuthenticateUserUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints for authenticating users (email/password or Google) and issuing tokens")
public class AuthController {
    private final AuthenticateUserUseCasePort authenticateUserUseCasePort;
    private final AuthenticateGoogleUserUseCasePort authenticateGoogleUserUseCasePort;

    // AUTHORIZATION: ANYONE
    @PostMapping("/login")
    @Operation(
            summary = "Login with email and password",
            description = "Authenticates a user with email and password and returns an access token and a refresh token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Validation error in the request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Email or password invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found user with email sent", content = @Content)
    })
    public ResponseEntity<LoginResponse> login (@RequestBody @Valid LoginRequest loginRequest) {
        LoginResult result = authenticateUserUseCasePort.execute(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(LoginResponse.fromDomain(result));
    }

    // AUTHORIZATION: ANYONE
    @PostMapping("/login/google")
    @Operation(
            summary = "Login with Google",
            description = "Authenticates a user via a Google ID token. The user must already have an account with the same email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Validation error in the request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Google token is invalid or has expired", content = @Content),
            @ApiResponse(responseCode = "404", description = "No user found for the email associated with the Google account", content = @Content)
    })
    public ResponseEntity<LoginResponse> loginGoogle(@RequestBody @Valid LoginGoogleRequest loginGoogleRequest) {
        LoginResult result = authenticateGoogleUserUseCasePort.execute(loginGoogleRequest.getGoogleToken());
        return ResponseEntity.ok(LoginResponse.fromDomain(result));
    }
}
