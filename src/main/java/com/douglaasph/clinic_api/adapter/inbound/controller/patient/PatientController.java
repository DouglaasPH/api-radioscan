package com.douglaasph.clinic_api.adapter.inbound.controller.patient;

import com.douglaasph.clinic_api.adapter.inbound.controller.auth.response.LoginResponse;
import com.douglaasph.clinic_api.adapter.inbound.controller.patient.request.CreatePatientRequest;
import com.douglaasph.clinic_api.domain.results.LoginResult;
import com.douglaasph.clinic_api.domain.ports.inbound.CreatePatientAccountUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/patient")
@Tag(name = "Patient", description = "Public endpoint for patient self-registration")
public class PatientController {
    private final CreatePatientAccountUseCasePort createPatientAccountUseCasePort;

    public PatientController(CreatePatientAccountUseCasePort createPatientAccountUseCasePort) {
        this.createPatientAccountUseCasePort = createPatientAccountUseCasePort;
    }

    // AUTHORIZATION: ANYONE WITHOUT REGISTER
    @PostMapping("/register")
    @Operation(
            summary = "Register a new patient",
            description = "Creates a new patient account and immediately logs them in, returning an access token and a refresh token. Open to anyone."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient registered and authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in the request body", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email or CPF already registered", content = @Content)
    })
    public ResponseEntity<LoginResponse> register (@RequestBody @Valid CreatePatientRequest request) {
        LoginResult result = createPatientAccountUseCasePort.execute(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getCpf(),
                request.getPhone()
        );
        return ResponseEntity.ok(LoginResponse.fromDomain(result));
    }
}
