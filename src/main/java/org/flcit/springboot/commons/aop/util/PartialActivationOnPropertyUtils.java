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

package org.flcit.springboot.commons.aop.util;

import java.util.function.Supplier;

import org.springframework.cache.Cache;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySources;

import org.flcit.springboot.commons.aop.domain.Seuil;
import org.flcit.springboot.commons.aop.service.SimpleCounterService;
import org.flcit.springboot.commons.core.util.PropertyUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class PartialActivationOnPropertyUtils {

    private static final SimpleCounterService<String> COUNTER_SERVICE = new SimpleCounterService<>();

    private PartialActivationOnPropertyUtils() { }

    /**
     * @param <T>
     * @param propertyName
     * @param propertyResolver
     * @param activeSupplier
     * @param notActiveSupplier
     * @return
     */
    public static <T> T get(String propertyName, PropertyResolver propertyResolver, Supplier<T> activeSupplier, Supplier<T> notActiveSupplier) {
        if (COUNTER_SERVICE.isAccepted(getSeuil(propertyName, propertyResolver), propertyName)) {
            return activeSupplier.get();
        }
        return notActiveSupplier != null ? notActiveSupplier.get() : null;
    }

    /**
     * @param propertyName
     * @param propertyResolver
     * @param activeRunnable
     * @param notActiveRunnable
     */
    public static void run(String propertyName, PropertyResolver propertyResolver, Runnable activeRunnable, Runnable notActiveRunnable) {
        if (COUNTER_SERVICE.isAccepted(getSeuil(propertyName, propertyResolver), propertyName)) {
            activeRunnable.run();
        } else if (notActiveRunnable != null) {
            notActiveRunnable.run();
        }
    }

    private static Seuil getSeuil(String name, PropertyResolver propertyResolver) {
        return getSeuil(null, name, null, propertyResolver, null, null);
    }

    /**
     * @param cache
     * @param name
     * @param defaultValue
     * @param propertyResolver
     * @param propertySources
     * @param propertySourcesNames
     * @return
     */
    public static Seuil getSeuil(Cache cache, String name, Supplier<Seuil> defaultValue, PropertyResolver propertyResolver, PropertySources propertySources, String[] propertySourcesNames) {
        return PropertyUtils.getValue(
                cache,
                name,
                propertyResolver,
                propertySources,
                propertySourcesNames,
                Seuil::getInstance,
                defaultValue);
    }

}
