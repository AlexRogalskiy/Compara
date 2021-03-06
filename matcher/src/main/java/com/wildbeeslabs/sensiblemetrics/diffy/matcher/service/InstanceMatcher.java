/*
 * The MIT License
 *
 * Copyright 2019 WildBees Labs, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.wildbeeslabs.sensiblemetrics.diffy.matcher.service;

import com.wildbeeslabs.sensiblemetrics.diffy.common.annotation.Factory;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.description.iface.MatchDescription;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.interfaces.Matcher;
import com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ReflectionUtils;
import com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static java.lang.String.join;

/**
 * Instance {@link AbstractMatcher} implementation
 *
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuppressWarnings("unchecked")
public class InstanceMatcher extends AbstractMatcher<Object> {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 296097385512552875L;

    /**
     * Default matchable {@link Class}
     */
    private final Class<?> matchableClazz;

    /**
     * Default instance matcher constructor with class instance {@link Class}
     *
     * @param clazz - initial input class argument instance {@link Class}
     */
    public InstanceMatcher(final Class<?> clazz) {
        this.matchableClazz = ReflectionUtils.getMatchableClass(clazz);
    }

    /**
     * Returns binary flag by initial argument match comparison
     *
     * @param value - initial input argument value to be matched {@link Object}
     * @return true - if initial value matches input argument {@link Object}, false - otherwise
     */
    @Override
    public boolean matches(final Object value) {
        return this.getMatchableClazz().isInstance(value);
    }

    /**
     * Appends input {@link MatchDescription} by input parameters
     *
     * @param value       - initial input {@link Object}
     * @param description - initial input {@link MatchDescription}
     */
    @Override
    public void describeBy(final Object value, final MatchDescription description) {
        description.append(value).append(String.join("matches", StringUtils.wrapInQuotes.apply(this.matchableClazz.getName())));
    }

    /**
     * Returns new {@link Matcher} by input {@link Class}
     *
     * @param <T>   type of matcher value
     * @param clazz - initial input class argument {@link Class}
     * @return {@link Matcher}
     */
    @Factory
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> getMatcher(final Class<? extends T> clazz) {
        return (Matcher<T>) new InstanceMatcher(clazz);
    }
}
