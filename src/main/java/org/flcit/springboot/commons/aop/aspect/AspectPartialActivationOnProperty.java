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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.cache.Cache;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;

import org.flcit.springboot.commons.aop.annotation.PartialActivationOnProperty;
import org.flcit.springboot.commons.aop.domain.Seuil;
import org.flcit.springboot.commons.aop.service.SimpleCounterService;
import org.flcit.springboot.commons.aop.util.PartialActivationOnPropertyUtils;
import org.flcit.springboot.commons.core.util.CacheUtils;
import org.flcit.commons.core.util.ThrowableUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@Aspect
public class AspectPartialActivationOnProperty implements BeanFactoryAware {

    private final Map<Method, Cache> caches = new ConcurrentHashMap<>();
    private final SimpleCounterService<Method> counterService = new SimpleCounterService<>();

    @Nullable
    private BeanFactory beanFactory;

    private final ConfigurableEnvironment env;

    protected AspectPartialActivationOnProperty(ConfigurableEnvironment env) {
        this.env = env;
    }

    /**
     * @param joinPoint
     * @param partialActivationOnProperty
     * @return
     * @throws Throwable
     */
    @Around("@annotation(partialActivationOnProperty)")
    public Object accept(final ProceedingJoinPoint joinPoint, final PartialActivationOnProperty partialActivationOnProperty) throws Throwable {
        if (accept(((MethodSignature) joinPoint.getSignature()).getMethod(), partialActivationOnProperty)) {
            return joinPoint.proceed();
        } else {
            ThrowableUtils.throwable(!partialActivationOnProperty.silent(), partialActivationOnProperty.throwable(), partialActivationOnProperty.throwableMessage());
        }
        return null;
    }

    private boolean accept(final Method method, final PartialActivationOnProperty partialActivationOnProperty) {
        return counterService.isAccepted(value(method, partialActivationOnProperty), method);
    }

    private Seuil value(final Method method, final PartialActivationOnProperty partialActivationOnProperty) {
        return PartialActivationOnPropertyUtils.getSeuil(
                CacheUtils.getCache(caches, method, beanFactory, partialActivationOnProperty.cacheManagerName(), partialActivationOnProperty.cacheManagerClass(), partialActivationOnProperty.cacheName(), partialActivationOnProperty.cacheClass()),
                partialActivationOnProperty.value(),
                () -> Seuil.getInstance(partialActivationOnProperty.defaultValue()),
                env,
                env.getPropertySources(),
                partialActivationOnProperty.propertySources());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}
