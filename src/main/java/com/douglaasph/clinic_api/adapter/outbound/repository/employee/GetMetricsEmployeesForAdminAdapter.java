package com.douglaasph.clinic_api.adapter.outbound.repository.employee;

import com.douglaasph.clinic_api.domain.results.MetricsEmployeesForAdminResult;
import com.douglaasph.clinic_api.domain.ports.outbound.GetMetricsEmployeesForAdminAdapterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetMetricsEmployeesForAdminAdapter implements GetMetricsEmployeesForAdminAdapterPort {
    private final EmployeeRepository employeeRepository;

    @Override
    public MetricsEmployeesForAdminResult get() {
        Integer numberOfEmployees = this.employeeRepository.findAll().size();
        Integer numberOfDoctors = this.employeeRepository.findByOptionalFilters(null, 2).size();
        Integer numberOfTechnicians = this.employeeRepository.findByOptionalFilters(null, 1).size();

        return new MetricsEmployeesForAdminResult(numberOfEmployees, numberOfDoctors, numberOfTechnicians);
    }
}
