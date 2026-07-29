package com.douglaasph.clinic_api.exceptions;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String msg) {
        super(msg);
    }
}
