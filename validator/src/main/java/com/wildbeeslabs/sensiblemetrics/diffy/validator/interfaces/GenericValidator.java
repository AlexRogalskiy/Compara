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
package com.wildbeeslabs.sensiblemetrics.diffy.validator.interfaces;

import java.io.Serializable;
import java.util.Locale;

/**
 * Generic {@link Validator} implementation
 *
 * @param <T> type of validated item
 */
public interface GenericValidator<T> extends Validator<T>, Serializable {

    /**
     * Returns validation flag by input parameters
     *
     * @param value   - initial input value {@code T}
     * @param pattern - initial input pattern {@link String}
     * @param locale  - initial input locale {@link Locale}
     * @return true - if input value {@code T} is valid, false - otherwise
     */
    boolean validate(final T value, final String pattern, final Locale locale);

    /**
     * Returns validation flag by input parameters
     *
     * @param value  - initial input value {@code T}
     * @param locale - initial input locale {@link Locale}
     * @return true - if input value {@code T} is valid, false - otherwise
     */
    boolean validate(final T value, final Locale locale);

    /**
     * Returns validation flag by input parameters
     *
     * @param value   - initial input value {@code T}
     * @param pattern - initial input pattern {@link String}
     * @return true - if input value {@code T} is valid, false - otherwise
     */
    boolean validate(final T value, final String pattern);
}
