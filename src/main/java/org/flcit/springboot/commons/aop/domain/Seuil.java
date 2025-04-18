/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flcit.springboot.commons.aop.domain;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import org.flcit.commons.core.util.NumberUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class Seuil {

    private final Integer value;
    private final TypeSeuil type;

    private Seuil(Integer value, TypeSeuil type) {
        Assert.notNull(value, "value must not be null");
        if (value < 0) {
            value = 0;
        }
        if (type == TypeSeuil.POURCENTAGE
                && value > 100) {
            value = 100;
        }
        this.value = value;
        this.type = type != null ? type : TypeSeuil.NOMBRE;
    }

    public static final Seuil getInstance(Object value) {
        return getInstance(org.flcit.commons.core.util.StringUtils.convert(value));
    }

    public static final Seuil getInstance(String value) {
        if (!StringUtils.hasLength(value)) {
            return null;
        }
        if (value.endsWith("%")) {
            return new Seuil(
                    NumberUtils.convertSafe(value.substring(0, value.length() - 1).trim(), Integer.class),
                    TypeSeuil.POURCENTAGE);
        } else {
            return new Seuil(
                    NumberUtils.convertSafe(value.trim(), Integer.class),
                    TypeSeuil.NOMBRE);
        }
    }

    /**
     * @return
     */
    public Integer getValue() {
        return value;
    }
    /**
     * @return
     */
    public TypeSeuil getType() {
        return type;
    }

}
