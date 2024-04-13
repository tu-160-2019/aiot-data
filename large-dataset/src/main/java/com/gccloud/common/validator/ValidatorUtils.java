/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.common.validator;


import com.gccloud.common.exception.GlobalException;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * hibernate-validator校验工具类
 * <p>
 * 参考文档：http://docs.jboss.org/hibernate/validator/5.4/reference/en-US/html_single/
 */
@Slf4j
public class ValidatorUtils {

    private static Validator validator;

    public ValidatorUtils() {
        throw new IllegalStateException("不允许创建");
    }

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     * @throws GlobalException 校验不通过，则报RRException异常
     */
    public static void validateEntity(Object object, Class<?>... groups) throws GlobalException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            List<String> errorList = Lists.newArrayList();
            for (ConstraintViolation<Object> constraint : constraintViolations) {
                errorList.add(constraint.getMessage());
            }
            String message = Joiner.on("<br/>").join(errorList);
            log.error(message);
            throw new GlobalException(message);
        }
    }
}