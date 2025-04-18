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

package org.flcit.springboot.commons.aop.aspect;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.flcit.commons.core.util.ThrowableUtils;
import org.flcit.springboot.commons.aop.annotation.ActivationOnProperty;
import org.flcit.springboot.commons.core.util.CacheUtils;
import org.flcit.springboot.commons.core.util.PropertyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.cache.Cache;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@Aspect
public class AspectActivationOnProperty implements BeanFactoryAware {

    private final Map<Method, Cache> caches = new ConcurrentHashMap<>();

    @Nullable
    private BeanFactory beanFactory;

    private final ConfigurableEnvironment env;

    protected AspectActivationOnProperty(ConfigurableEnvironment env) {
        this.env = env;
    }

    /**
     * @param joinPoint
     * @param activationOnProperty
     * @return
     * @throws Throwable
     */
    @Around("@annotation(activationOnProperty)")
    public Object activate(final ProceedingJoinPoint joinPoint, final ActivationOnProperty activationOnProperty) throws Throwable {
        if (activate(((MethodSignature) joinPoint.getSignature()).getMethod(), activationOnProperty)) {
            return joinPoint.proceed();
        } else {
            ThrowableUtils.throwable(!activationOnProperty.silent(), activationOnProperty.throwable(), activationOnProperty.throwableMessage());
        }
        return null;
    }

    private boolean activate(final Method method, final ActivationOnProperty activationOnProperty) {
        return PropertyUtils.getValue(
                CacheUtils.getCache(caches, method, beanFactory, activationOnProperty.cacheManagerName(), activationOnProperty.cacheManagerClass(), activationOnProperty.cacheName(), activationOnProperty.cacheClass()),
                activationOnProperty.value(),
                env,
                env.getPropertySources(),
                activationOnProperty.propertySources(),
                PropertyUtils::toBoolean,
                activationOnProperty::defaultValue);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}
