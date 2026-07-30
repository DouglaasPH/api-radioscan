package com.douglaasph.clinic_api.services;

import com.douglaasph.clinic_api.controllers.dto.appointment.*;
import com.douglaasph.clinic_api.exceptions.AppointmentConflictException;
import com.douglaasph.clinic_api.exceptions.BusinessRuleException;
import com.douglaasph.clinic_api.models.entities.*;
import com.douglaasph.clinic_api.models.entities.enums.AppointmentStatus;
import com.douglaasph.clinic_api.models.entities.enums.AppointmentType;
import com.douglaasph.clinic_api.repositories.*;
import com.douglaasph.clinic_api.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.mapping.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private XRayReportService xRayReportService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XRayReportRepository xRayReportRepository;

    public List<PatientEmployeeAppointmentResponseDto> findByPatientEmail(String loggedEmail, boolean isAdmin) {
            return appointmentRepository.findByPatientEmail(loggedEmail);
    }

    public Page<AllAvailablesAppointmentsResponseDto> findAllAvailable(LocalDate date, AppointmentType appointmentType, Integer page) {
        PageRequest pageable = PageRequest.of(page, 4);
        return appointmentRepository.findAllAvailablesAppointments(appointmentType.getCode(), date.atStartOfDay(), date.atTime(LocalTime.MAX), pageable);
    }

    @Transactional
    public Appointment insert(CreateAppointmentDto dto) throws IllegalArgumentException {
        Employee employee = employeeRepository.findById(dto.employee_id()).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", dto.employee_id()));

        // TECHNICAL CODE IS 1 === EXAM_CAPTURE CODE IS 1
        // DOCTOR CODE IS 2 === REPORT_REVIEW CODE IS 2
        if (dto.type().getCode() != employee.getPosition().getCode()) {
            throw new BusinessRuleException("Employee's position incompatible with the type of inquiry.");
        }

        Appointment appointment = new Appointment(null, employee, null, dto.dateHour(), AppointmentStatus.AVAILABLE, dto.type());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment bookExamCapture(Long appointmentId, Authentication authentication) throws AppointmentConflictException {
        Patient patient = getPatientByEmail(authentication.getName());
        Appointment appointment = getAndValidateAvailableAppointment(appointmentId);

        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment bookReportReview(Long appointmentId, Long xRayReportId, Authentication authentication) throws AppointmentConflictException {
        Patient patient = getPatientByEmail(authentication.getName());
        Appointment appointment = getAndValidateAvailableAppointment(appointmentId);

        XRayReport xRayReport = xRayReportRepository.findById(xRayReportId)
                        .orElseThrow(() -> new ResourceNotFoundException("XRayReport", "id", xRayReportId));
        appointment.setXRayReport(xRayReport);

        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    private Patient getPatientByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                .getPatient();
    }

    private Appointment getAndValidateAvailableAppointment(Long appointmentId) throws AppointmentConflictException {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (appointment.getPatient() != null) {
            throw new AppointmentConflictException("This time slot is already reserved by another patient.");
        }
        if (appointment.getStatus() != AppointmentStatus.AVAILABLE) {
            throw new AppointmentConflictException("This time slot is not available for scheduling.");
        }

        return appointment;
    }

    // Business rule: you can only cancel up to 24 hours before your scheduled appointment
    @Transactional
    public Appointment cancel(Long appointmentId, String email) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));


        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(appointment.getDateHour().minusHours(24))) {
            throw new BusinessRuleException("The appointment can only be cancelled with 24 hours' notice.");
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public RequestUploadResponseDto startExamCapture(Long appointmentId, String email) throws AppointmentConflictException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (user.getEmployee() == null) {
            throw new AccessDeniedException("Logged user is not registered as an employee.");
        }

        Long loggedEmployeeId = user.getEmployee().getId();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (appointment.getEmployee() == null || !appointment.getEmployee().getId().equals(loggedEmployeeId)) {
            throw new AccessDeniedException("This appointment is not assigned to you.");
        }

        if (appointment.getType() != AppointmentType.EXAM_CAPTURE) {
            throw new BusinessRuleException("This appointment scheduling is not intended for the collection of test samples.");
        }

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new AppointmentConflictException("The scheduled task must have the status SCHEDULED in order to be started.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return new RequestUploadResponseDto(
                xRayReportService.createReportAndGenerateUploadUrl(appointment)
        );
    }

    public Page<AppointmentManagementAdminDto> findAllAppointmentsForManagementWithPagination(AppointmentStatus status, String employeeName, Integer page) {
        PageRequest pageable = PageRequest.of(page, 4);
        Integer statusCode = (status == null) ? null : status.getCode();
        return appointmentRepository.findAllAppointmentsManagement(statusCode, employeeName, pageable);
    }

    public DashboardMetricsForEmployeeResponseDto metricsForEmployeeDashboard(Authentication authentication) {
        Long userId = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName())).getId();

        return appointmentRepository.getDailyMetrics(
                userId,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );
    }
}
