package com.andy.spring.converter;

import com.andy.spring.exception.ParamException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 基础转化器
 *
 * @author 庞先海 2018-10-27
 */
public abstract class AbstractConverter {

    private static Validator validator;

    public AbstractConverter() {
        if (validator == null) {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }
    }

    protected void checkParam(Object object) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        if (CollectionUtils.isNotEmpty(constraintViolations)) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<?> constraintViolation : constraintViolations) {
                errorMessage.append(constraintViolation.getPropertyPath()).append(":");
                errorMessage.append(constraintViolation.getMessage()).append(",");
            }
            throw new ParamException(errorMessage.toString());
        }
    }
}
