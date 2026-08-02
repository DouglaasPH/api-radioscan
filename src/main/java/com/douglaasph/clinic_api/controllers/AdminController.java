package com.douglaasph.clinic_api.controllers;

import com.douglaasph.clinic_api.controllers.dto.admin.DashboardAdminMetricsDto;
import com.douglaasph.clinic_api.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin")
@Tag(name = "Admin", description = "Endpoints for managing the administrative portal")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(
            summary = "Get admin dashboard metrics",
            description = "Retrieves general indicators, statistics, and summary data for the admin dashboard."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard metrics retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (missing or invalid token)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (requires ADMIN role)"
            )
    })
    @GetMapping("metrics/dashboard")
    public ResponseEntity<DashboardAdminMetricsDto> dashboardMetrics() {
        return ResponseEntity.ok(this.adminService.dashboardMetrics());
    }
}