package com.mawus.raspApi.exceptions;

public class FieldHolder {

    private final String errorField;
    private final String fieldValue;

    public FieldHolder(String errorField, String fieldValue) {
        this.errorField = errorField;
        this.fieldValue = fieldValue;
    }

    public String getErrorField() {
        return errorField;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
