package com.douglaasph.clinic_api.domain.ports.outbound;

public interface FileStorageAdapterPort {
    public String generateUploadUrl(String key);
    public String generateDownloadUrl(String key);
}
