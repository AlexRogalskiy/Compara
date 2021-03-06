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
package com.wildbeeslabs.sensiblemetrics.diffy.matcher.interfaces;

import com.wildbeeslabs.sensiblemetrics.diffy.matcher.exception.MatchOperationException;

/**
 * Type safe {@link Matcher} interface declaration
 *
 * @param <T> type of input element to be matched by operation
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@FunctionalInterface
public interface TypeSafeMatcher<T> extends Matcher<T> {

    /**
     * Returns binary flag depending on initial argument value by comparison
     *
     * @param value - initial input argument value {@code T}
     * @return true - if input value {@code T} matches, false - otherwise
     */
    default boolean matches(final T value) {
        try {
            return this.matchesSafe(value);
        } catch (RuntimeException e) {
            MatchOperationException.throwIncorrectMatch(value, e);
        }
        return false;
    }

    /**
     * Returns binary flag depending on initial argument value by type safe comparison
     *
     * @param value - initial input argument value {@code T}
     * @return true - if input value {@code T} matches safely, false - otherwise
     */
    boolean matchesSafe(final T value);
}
