package com.douglaasph.clinic_api.adapter.inbound.controller.appointment;

import com.douglaasph.clinic_api.adapter.inbound.controller.appointment.request.CreateAppointmentRequest;
import com.douglaasph.clinic_api.adapter.inbound.controller.appointment.response.*;
import com.douglaasph.clinic_api.domain.domain.*;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentStatus;
import com.douglaasph.clinic_api.domain.domain.enums.AppointmentType;
import com.douglaasph.clinic_api.domain.ports.inbound.*;
import com.douglaasph.clinic_api.domain.results.*;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/appointment")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Appointment", description = "Endpoints for scheduling, managing and tracking clinic appointments")
public class AppointmentController {
    private final CreateAppointmentUseCasePort createAppointmentUseCasePort;
    private final GetAllAvailabilitiesAppointmentsUseCasePort getAllAvailabilitiesAppointmentsUseCasePort;
    private final GetAppointmentLinkedToThePatientUseCasePort getAppointmentLinkedToThePatientUseCasePort;
    private final BookExamCaptureUseCasePort bookExamCaptureUseCasePort;
    private final BookReportReviewUseCasePort bookReportReviewUseCasePort;
    private final CancelAppointmentUseCasePort cancelAppointmentUseCasePort;
    private final StartExamCaptureUseCasePort startExamCaptureUseCasePort;
    private final GetAppointmentsManagementWithPaginationUseCasePort getAppointmentsManagementWithPaginationUseCasePort;
    private final AppointmentMetricsForEmployeesUseCasePort appointmentMetricsForEmployeesUseCasePort;
    private final GetAppointmentByIdUseCasePort getAppointmentByIdUseCasePort;

    // AUTHORIZATION: ADMIN
    @PostMapping
    @Operation(
            summary = "Create a new appointment",
            description = "Creates a new appointment slot for an employee at a given date/time and type. Restricted to admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in the request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not an admin", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "The employee already has an appointment at that time", content = @Content)
    })
    public ResponseEntity<Appointment> insert(@RequestBody @Valid CreateAppointmentRequest createAppointmentRequest) {
        Appointment appointmentResponse = createAppointmentUseCasePort.execute(
                createAppointmentRequest.getEmployee_id(),
                createAppointmentRequest.getDateHour(),
                createAppointmentRequest.getType()
        );
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(appointmentResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(appointmentResponse);
    }

    // AUTHORIZATION: PATIENT AND ADMIN
    @GetMapping("/available")
    @Operation(
            summary = "List available appointment slots",
            description = "Returns a paginated list of open appointment slots, optionally filtered by date and appointment type. Accessible by patients and admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of available slots retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is neither a patient nor an admin", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Page<AvailabilitiesAppointmentsResponse>> findAvailabilities(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDate date,
            @RequestParam(required = false) AppointmentType appointmentType,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        Page<AvailabilitiesAppointmentsResult> result = getAllAvailabilitiesAppointmentsUseCasePort.execute(date, appointmentType, page);
        return ResponseEntity.ok().body(result.map(AvailabilitiesAppointmentsResponse::fromDomain));
    }

    // AUTHORIZATION: PATIENT
    @Operation(
            summary = "List appointments linked to the authenticated patient",
            description = "Returns all appointments (past and upcoming) associated with the currently authenticated patient."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a patient", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<AppointmentsLinkedToThePatientResponse>> findByPatientEmail(Authentication authentication) {
        List<AppointmentsLinkedToThePatientResult> result = getAppointmentLinkedToThePatientUseCasePort.execute(authentication.getName());
        return ResponseEntity.ok().body(
                result
                        .stream()
                        .map(AppointmentsLinkedToThePatientResponse::fromDomain)
                        .toList()
        );
    }

    // AUTHORIZATION: PATIENT
    @PutMapping("/exam-capture/{appointmentId}/book")
    @Operation(
            summary = "Book an exam capture slot",
            description = "Books the given appointment slot as an exam capture appointment for the authenticated patient."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment successfully scheduled"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a patient", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or Appointment not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Appointment time slot already taken or unavailable", content = @Content)
    })
    public ResponseEntity<Appointment> bookExamCapture(@PathVariable Long appointmentId, Authentication authentication) throws AppointmentConflictException {
        Appointment response = bookExamCaptureUseCasePort.execute(appointmentId, authentication.getName());
        return ResponseEntity.ok(response);
    }

    // AUTHORIZATION: PATIENT
    @PutMapping("/report-review/{appointmentId}/{xRayReportId}/book")
    @Operation(
            summary = "Book a report review slot",
            description = "Books the given appointment slot as a report review appointment for the authenticated patient, linked to a specific X-ray report."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Slot booked successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a patient", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or Appointment or X-ray report not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "The slot is no longer available", content = @Content)
    })
    public ResponseEntity<Appointment> bookReportReview(
            @PathVariable Long appointmentId,
            @PathVariable Long xRayReportId,
            Authentication authentication
    ) throws AppointmentConflictException {
        Appointment response = bookReportReviewUseCasePort.execute(appointmentId, xRayReportId, authentication.getName());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // AUTHORIZATION: ADMIN or PATIENT
    @PutMapping("/{appointmentId}/cancel")
    @Operation(
            summary = "Cancel an appointment",
            description = "Cancels the given appointment. Can be performed by an admin or by the patient the appointment belongs to."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment canceled successfully"),
            @ApiResponse(responseCode = "400", description = "Business rule violation (e.g. less than 24 hours' notice or already canceled)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have permission to cancel this appointment", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or Appointment not found", content = @Content)
    })
    public ResponseEntity<Appointment> cancel(@PathVariable Long appointmentId, Authentication authentication) {
        return ResponseEntity.ok().body(cancelAppointmentUseCasePort.execute(appointmentId, authentication.getName()));
    }

    // AUTHORIZATION: EMPLOYEE (ONLY TECHNICAL)
    @PostMapping("/{appointmentId}/request-upload")
    @PreAuthorize("@securityUtils.isEmployeeTechnical(authentication)")
    @Operation(
            summary = "Request a presigned upload URL for an exam capture",
            description = "Generates a time-limited presigned URL that a technical employee can use to upload the captured X-ray image directly to storage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or appointment type is not EXAM_CAPTURE", content = @Content), // <--- Adicionado
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated employee is not a technical or not assigned to this appointment", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or Appointment not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Appointment is not in SCHEDULED status", content = @Content)
    })
    public ResponseEntity<UrlPressignedS3Response> startExam(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) throws AppointmentConflictException {
        UrlPressignedS3Result result = startExamCaptureUseCasePort.execute(appointmentId, authentication.getName());
        return ResponseEntity.ok(UrlPressignedS3Response.fromDomain(result));
    }

    // AUTHORIZATION: ONLY ADMIN AND EMPLOYEES
    @GetMapping("management")
    @Operation(
            summary = "List appointments for management, with pagination and filters",
            description = "Returns a paginated list of appointments across all patients, optionally filtered by status and/or employee name. Restricted to admins and employees."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of appointments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is neither an admin nor an employee", content = @Content)
    })
    public ResponseEntity<Page<AppointmentsManagementResponse>> appointmentsManagementWithPagination(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String employeeName,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        Page<AppointmentsManagementResult> result = getAppointmentsManagementWithPaginationUseCasePort.execute(status, employeeName, page);
        return ResponseEntity.ok(
                result.map(AppointmentsManagementResponse::fromDomain)
        );
    }

    // AUTHORIZATION: ONLY EMPLOYEES
    @GetMapping("metrics")
    @Operation(
            summary = "Get appointment metrics for the authenticated employee",
            description = "Returns aggregated appointment metrics scoped to the currently authenticated employee. Restricted to employees."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not an employee", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
    })
    public ResponseEntity<EmployeesMetricsResponse> getMetricsForEmployeees(Authentication authentication) {
        EmployeesMetricsResult result = appointmentMetricsForEmployeesUseCasePort.execute(authentication.getName());
        return ResponseEntity.ok(EmployeesMetricsResponse.fromDomain(result));
    }

    // AUTHORIZATION: EMPLOYEE
    @GetMapping("{appointmentId}")
    @Operation(
            summary = "Get appointment by id",
            description = "Retrieves the full details of a single appointment by its id. Restricted to employees."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not an employee", content = @Content),
            @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content)
    })
    public ResponseEntity<Appointment> findByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok().body(getAppointmentByIdUseCasePort.execute(appointmentId));
    }
}
