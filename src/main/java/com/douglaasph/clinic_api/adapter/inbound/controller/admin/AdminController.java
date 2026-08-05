package com.douglaasph.clinic_api.adapter.inbound.controller.admin;

import com.douglaasph.clinic_api.adapter.inbound.controller.admin.response.DashboardAdminMetricsResponse;
import com.douglaasph.clinic_api.domain.results.DashboardAdminMetricsResult;
import com.douglaasph.clinic_api.domain.ports.inbound.GetDashboardMetricsForAdminUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints for clinic-wide administrative dashboards and metrics")
public class AdminController {
    private final GetDashboardMetricsForAdminUseCasePort getDashboardMetricsForAdminUseCasePort;


    @GetMapping("metrics/dashboard")
    @Operation(
            summary = "Get admin dashboard metrics",
            description = "Returns aggregated metrics (appointments, patients, employees) for the admin dashboard. Restricted to admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not an admin", content = @Content)
    })
    public ResponseEntity<DashboardAdminMetricsResponse> dashboardMetrics() {
        DashboardAdminMetricsResult result = this.getDashboardMetricsForAdminUseCasePort.execute();
        return ResponseEntity.ok(DashboardAdminMetricsResponse.fromDomain(result));
    }
}
