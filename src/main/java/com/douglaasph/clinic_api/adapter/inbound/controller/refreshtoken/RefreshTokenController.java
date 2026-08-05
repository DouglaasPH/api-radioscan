package com.douglaasph.clinic_api.adapter.inbound.controller.refreshtoken;

import com.douglaasph.clinic_api.adapter.inbound.controller.auth.response.LoginResponse;
import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.ports.inbound.RefreshTokenUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh-token")
@RequiredArgsConstructor
@Tag(name = "Refresh Token", description = "Endpoint for exchanging a valid refresh token for a new access token")
public class RefreshTokenController {
    private final RefreshTokenUseCasePort refreshTokenUseCasePort;

    @PostMapping("/{refreshToken}")
    @Operation(
            summary = "Refresh access token",
            description = "Exchanges a valid, non-expired refresh token for a new access token. If the refresh token itself is expired, a new refresh token is also issued."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "404", description = "Refresh token not found or User not found during refresh token creation", content = @Content),
    })
    public ResponseEntity<LoginResponse> refresh(@PathVariable String refreshToken) {
        LoginResult result = refreshTokenUseCasePort.execute(refreshToken);
        return ResponseEntity.ok().body(
                new LoginResponse(result.getAccessToken(), result.getRefreshToken())
        );
    }
}
