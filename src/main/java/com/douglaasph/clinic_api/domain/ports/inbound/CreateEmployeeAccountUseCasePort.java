package com.douglaasph.clinic_api.domain.ports.inbound;

import com.douglaasph.clinic_api.domain.domain.Employee;
import com.douglaasph.clinic_api.domain.domain.enums.Position;

public interface CreateEmployeeAccountUseCasePort {
    Employee execute(String name, String email, String password, String licenseNumber, Position position);
}
