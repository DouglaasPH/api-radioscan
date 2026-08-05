package com.douglaasph.clinic_api.domain.ports.inbound;

public interface GetExamDownloadUrlUseCasePort {
    String execute(Long reportId, String email);
}
