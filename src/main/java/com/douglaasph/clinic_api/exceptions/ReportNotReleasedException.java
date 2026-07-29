package com.douglaasph.clinic_api.exceptions;

public class ReportNotReleasedException extends RuntimeException {
    public ReportNotReleasedException(String msg) {
        super(msg);
    }
}
