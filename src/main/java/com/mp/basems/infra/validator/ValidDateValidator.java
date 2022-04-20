package com.mp.basems.infra.validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class ValidDateValidator implements ConstraintValidator<ValidDate, String> {

    private Boolean isOptional;

    @Override
    public void initialize(ValidDate validDate) {
        this.isOptional = validDate.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        boolean validDate = isValidFormat2(value);
        
        return isOptional? (validDate || StringUtils.isEmpty(value)) : validDate;
    }

    
    private static boolean isValidFormat2(String value) {
        boolean isValid = false;
        
        try {
            if (!StringUtils.isEmpty(value)) {
               LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
               isValid = true;
           }
        } 
        catch (DateTimeParseException  e) { }
        
        return isValid;
    }
}
