package com.douglaasph.clinic_api.controllers;

import com.douglaasph.clinic_api.controllers.dto.auth.LoginResponseDto;
import com.douglaasph.clinic_api.controllers.dto.patient.RegisterPatientDto;
import com.douglaasph.clinic_api.services.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/patient")
@Tag(name = "Patient", description = "Endpoints for managing patients of the clinic")
public class PatientController {
    @Autowired
    private PatientService patientService;

    // AUTHORIZATION: ANYONE WITHOUT REGISTER
    @Operation(summary = "Register patient", description = "Register patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registered patient with success"),
            @ApiResponse(responseCode = "400", description = "Invalid data (validation failure)")
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register (@RequestBody @Valid RegisterPatientDto dto) {
        LoginResponseDto response = patientService.register(dto);
        return ResponseEntity.ok(response);
    }
}
