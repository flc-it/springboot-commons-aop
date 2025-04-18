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

package org.flcit.springboot.commons.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.flcit.springboot.commons.core.exception.ServiceUnavailableException;
import org.flcit.commons.core.util.StringUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PartialActivationOnProperty {

    /**
     * @return the name of the property
     */
    String value();

    /**
     * @return the default value (default is null)
     */
    String defaultValue() default StringUtils.EMPTY;

    /**
     * @return false if must throws an Exception or not if disable
     */
    boolean silent() default false;

    /**
     * @return property sources list to search more faster
     */
    String[] propertySources() default {};

    /**
     * @return Throwable to throws if the property is disable
     * The Throwable must have a constructor with string parameter if a throwableMessage is not empty
     * The Throwable must have a no arg constructor if throwableMessage is empty
     */
    Class<? extends Throwable> throwable() default ServiceUnavailableException.class;

    /**
     * @return Message to associate with the Throwable to throws when the property is disable.
     * The Throwable must have a constructor with string parameter
     */
    String throwableMessage() default StringUtils.EMPTY;

    /**
     * @return Cache Manager name to retrieve the cache
     */
    String cacheManagerName() default StringUtils.EMPTY;

    /**
     * @return Cache Manager class to retrieve the cache
     */
    Class<? extends CacheManager> cacheManagerClass() default CacheManager.class;

    /**
     * @return Cache name to store the value
     */
    String cacheName() default StringUtils.EMPTY;

    /**
     * @return Cache class to store the value
     */
    Class<? extends Cache> cacheClass() default Cache.class;

}
