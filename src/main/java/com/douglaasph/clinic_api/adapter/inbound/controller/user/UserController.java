package com.douglaasph.clinic_api.adapter.inbound.controller.user;

import com.douglaasph.clinic_api.adapter.inbound.controller.user.request.UpdateUserDataRequest;
import com.douglaasph.clinic_api.adapter.inbound.controller.user.request.UpdateUserPasswordRequest;
import com.douglaasph.clinic_api.adapter.inbound.controller.user.response.UserResponse;
import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.inbound.GetMyUserDataByEmailUseCasePort;
import com.douglaasph.clinic_api.domain.ports.inbound.UpdateUserDataUseCasePort;
import com.douglaasph.clinic_api.domain.ports.inbound.UpdateUserPasswordUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for managing the authenticated user's own profile and account details")
public class UserController {
    private final GetMyUserDataByEmailUseCasePort getMyUserDataByEmailUseCasePort;
    private final UpdateUserDataUseCasePort updateUserDataUseCasePort;
    private final UpdateUserPasswordUseCasePort updateUserPasswordUseCasePort;

    // AUTHORIZATION: ANYONE
    @GetMapping
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves profile details for the currently authenticated user, based on the JWT Bearer token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponse> me (Authentication authentication) {
        User user = getMyUserDataByEmailUseCasePort.execute(authentication.getName());
        return ResponseEntity.ok(
                UserResponse.fromDomain(user)
        );
    }

    // AUTHORIZATION: ANYONE
    @PutMapping("/data")
    @Operation(
            summary = "Update personal data",
            description = "Updates personal details (CPF, phone) of the currently authenticated user. Only applies to patients."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in the request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponse> updateData (@RequestBody @Valid UpdateUserDataRequest updateUserDataRequest, Authentication authentication) {
        User user = updateUserDataUseCasePort.execute(
                updateUserDataRequest.getCpf(),
                updateUserDataRequest.getPhone(),
                authentication.getName()
        );
        return ResponseEntity.ok(
                UserResponse.fromDomain(user)
        );
    }

    // AUTHORIZATION: ANYONE
    @PutMapping("/password")
    @Operation(
            summary = "Update password",
            description = "Changes the password of the currently authenticated user, after verifying the current password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error, or the new password matches the current one", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing/invalid Bearer token, or current password is incorrect", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserResponse> updatePassword (@RequestBody @Valid UpdateUserPasswordRequest updateUserPasswordRequest, Authentication authentication) {
        User user = updateUserPasswordUseCasePort.execute(
                authentication.getName(),
                updateUserPasswordRequest.getCurrentPassword(),
                updateUserPasswordRequest.getNewPassword()
        );
        return ResponseEntity.ok(
                UserResponse.fromDomain(user)
        );
    }
}
