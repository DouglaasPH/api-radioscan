package com.douglaasph.clinic_api.config.security;

import com.douglaasph.clinic_api.domain.domain.User;
import com.douglaasph.clinic_api.domain.ports.outbound.GetPatientByIdAdapterPort;
import com.douglaasph.clinic_api.domain.ports.outbound.GetUserByEmailAdapterPort;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import com.douglaasph.clinic_api.domain.domain.enums.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {
    private final GetUserByEmailAdapterPort getUserByEmailAdapterPort;
    private final GetPatientByIdAdapterPort getPatientByIdAdapterPort;

    public boolean isEmployeeDoctor(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        User user = getUserByEmailAdapterPort.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(("user not found")));

        return user.getEmployee().getPosition() == Position.DOCTOR;
    }

    public boolean isEmployeeTechnical(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        User user = getUserByEmailAdapterPort.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(("user not found")));

        return user.getEmployee().getPosition() == Position.TECHNICAL;
    }

    public boolean isAdminOrPatientThemSelves(Authentication authentication, Long patientId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        User user = getUserByEmailAdapterPort.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(("user not found")));

        return user.getRole().getCode() == 1 ||
                (user.getRole().getCode() == 3 &&
                        Objects.equals(
                                getPatientByIdAdapterPort.findById(patientId).orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId))
                                        .getId(), user.getPatient().getId()));
    }
}
