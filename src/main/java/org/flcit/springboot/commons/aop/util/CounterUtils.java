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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.flcit.springboot.commons.aop.domain.Seuil;
import org.flcit.springboot.commons.aop.domain.TypeSeuil;
import org.flcit.commons.core.util.NumberUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class CounterUtils {

    private CounterUtils() { }

    /**
     * @param <T>
     * @param counters
     * @param seuil
     * @param key
     * @return
     */
    public static <T> boolean isRefused(final Map<T, AtomicInteger> counters, final Seuil seuil, final T key) {
        return !isAccepted(counters, seuil, key);
    }

    /**
     * @param <T>
     * @param counters
     * @param seuil
     * @param key
     * @return
     */
    public static <T> boolean isAccepted(final Map<T, AtomicInteger> counters, final Seuil seuil, final T key) {
        if (seuil == null
                || seuil.getValue() == null) {
            return true;
        }
        if (seuil.getValue().equals(NumberUtils.INTEGER_0)) {
            return false;
        }
        if (seuil.getType() == TypeSeuil.POURCENTAGE
                && seuil.getValue() >= NumberUtils.INTEGER_100) {
            return true;
        }
        final AtomicInteger atomic = counters.computeIfAbsent(key, k -> new AtomicInteger(NumberUtils.INTEGER_0));
        if (seuil.getType() == TypeSeuil.NOMBRE) {
            return isAcceptedByNombre(atomic, seuil.getValue());
        } else {
            return isAcceptedByPourcentage(atomic, seuil.getValue());
        }
    }

    private static boolean isAcceptedByNombre(final AtomicInteger atomic, final Integer seuil) {
        if (atomic.incrementAndGet() > seuil) {
            atomic.set(seuil);
            return false;
        } else {
            return true;
        }
    }

    private static boolean isAcceptedByPourcentage(final AtomicInteger atomic, final Integer seuil) {
        final int current = atomic.incrementAndGet();
        boolean res = current % (100 / seuil) == 0;
        if (current == 100) {
            atomic.addAndGet(-100);
        }
        return res;
    }

}
