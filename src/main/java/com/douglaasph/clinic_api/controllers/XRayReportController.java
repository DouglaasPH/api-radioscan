package com.douglaasph.clinic_api.controllers;

import com.douglaasph.clinic_api.controllers.dto.xRayReport.RequestDownloadResponseDto;
import com.douglaasph.clinic_api.controllers.dto.appointment.ReviewDto;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import com.douglaasph.clinic_api.models.entities.XRayReport;
import com.douglaasph.clinic_api.services.XRayReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/x-ray")
@Tag(name = "X-Ray", description = "Endpoints for managing and retrieving X-Ray exam reports")
@SecurityRequirement(name = "bearerAuth")
public class XRayReportController {
    @Autowired
    private XRayReportService xRayReportService;

    // AUTHORIZATION: EMPLOYEE (ONLY DOCTOR)
    @Operation(
            summary = "Update diagnosis report",
            description = "Allows the assigned doctor to provide or update the final medical diagnosis for an X-Ray exam linked to a specific appointment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagnosis updated and saved successfully."),
            @ApiResponse(responseCode = "404", description = "Appointment or X-Ray report not found."),
            @ApiResponse(responseCode = "409", description = "Business rule violation: The appointment is either not assigned to you, or its current status does not allow editing.")
    })
    @PutMapping("/{appointmentId}/review")
    @PreAuthorize("@securityUtils.isEmployeeDoctor(authentication)")
    public ResponseEntity<XRayReport> review(
            @PathVariable Long appointmentId,
            @RequestBody ReviewDto dto
    ) {
        return ResponseEntity.ok().body(xRayReportService.reviewDoctor(appointmentId, dto.finalDoctorDiagnosis()));
    }

    // AUTHORIZATION: ANYONE
    @GetMapping("/{reportId}/download")
    public ResponseEntity<RequestDownloadResponseDto> getDownloadUrl(
            @PathVariable Long reportId,
            Authentication authentication) throws AppointmentConflictException {
        RequestDownloadResponseDto response = xRayReportService.getExamDownloadUrl(reportId, authentication);
        return ResponseEntity.ok(response);
    }
}
