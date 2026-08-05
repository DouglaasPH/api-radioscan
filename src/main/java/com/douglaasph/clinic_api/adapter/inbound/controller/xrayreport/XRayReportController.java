package com.douglaasph.clinic_api.adapter.inbound.controller.xrayreport;

import com.douglaasph.clinic_api.adapter.inbound.controller.xrayreport.request.DoctorReviewExamRequest;
import com.douglaasph.clinic_api.adapter.inbound.controller.xrayreport.response.ExamDownloadUrlResponse;
import com.douglaasph.clinic_api.domain.domain.XRayReport;
import com.douglaasph.clinic_api.domain.ports.inbound.DoctorReviewExamUseCasePort;
import com.douglaasph.clinic_api.domain.ports.inbound.GetExamDownloadUrlUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/x-ray")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "X-Ray Report", description = "Endpoints for doctor review of exams and downloading X-ray reports")
public class XRayReportController {
    private final DoctorReviewExamUseCasePort doctorReviewExamUseCasePort;
    private final GetExamDownloadUrlUseCasePort getExamDownloadUrlUseCasePort;

    // AUTHORIZATION: EMPLOYEE (ONLY DOCTOR)
    @PutMapping("/{appointmentId}/review")
    @PreAuthorize("@securityUtils.isEmployeeDoctor(authentication)")
    @Operation(
            summary = "Submit doctor's review of an exam",
            description = "Records the final diagnosis given by a doctor for the X-ray exam linked to an appointment. Restricted to employees with the doctor position."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review submitted successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated employee is not a doctor", content = @Content),
            @ApiResponse(responseCode = "404", description = "Appointment or X-ray report not found", content = @Content)
    })
    public ResponseEntity<XRayReport> doctorReviewExam(
            @PathVariable Long appointmentId,
            @RequestBody DoctorReviewExamRequest doctorReviewExamRequest
    ) {
        return ResponseEntity.ok().body(
                doctorReviewExamUseCasePort.execute(
                        appointmentId,
                        doctorReviewExamRequest.getFinalDoctorDiagnosis()
                )
        );
    }

    // AUTHORIZATION: ANYONE
    @GetMapping("/{reportId}/download")
    @Operation(
            summary = "Get a presigned download URL for an X-ray report",
            description = "Generates a time-limited presigned URL to download the X-ray report file. Accessible by the patient the report belongs to, and by clinic staff."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL generated successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have access to this report", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or X-ray report not found", content = @Content)
    })
    public ResponseEntity<ExamDownloadUrlResponse> getDownloadUrl(
            @PathVariable Long reportId,
            Authentication authentication) {
        return ResponseEntity.ok(new ExamDownloadUrlResponse(getExamDownloadUrlUseCasePort.execute(reportId, authentication.getName())));
    }
}
