package com.douglaasph.clinic_api.domain.ports.outbound;

import com.douglaasph.clinic_api.domain.domain.Employee;

public interface SaveEmployeeAdapterPort {
    Employee save(Employee employee);
}
