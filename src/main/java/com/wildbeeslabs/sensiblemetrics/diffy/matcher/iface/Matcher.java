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
package com.wildbeeslabs.sensiblemetrics.diffy.matcher.iface;

import com.wildbeeslabs.sensiblemetrics.diffy.entry.description.iface.MatchDescription;

import java.io.Serializable;
import java.util.Objects;

import static com.wildbeeslabs.sensiblemetrics.diffy.entry.description.iface.MatchDescription.DEFAULT_EMPTY_MATCH_DESCRIPTION;

/**
 * Matcher interface declaration by input object instance
 *
 * @param <T> type of input element to be matched by operation
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@FunctionalInterface
public interface Matcher<T> extends Serializable {

    /**
     * Returns binary flag by initial argument match comparison
     *
     * @param value - initial input argument value to be matched {@code T}
     * @return true - if initial value matches input argument, false - otherwise
     */
    boolean matches(final T value);

    /**
     * Returns default matcher description {@link MatchDescription}
     *
     * @return matcher description {@link MatchDescription}
     */
    default MatchDescription getDescription() {
        return DEFAULT_EMPTY_MATCH_DESCRIPTION;
    }

    /**
     * Returns composed {@code Matcher} instance
     *
     * @param after - initial input {@link Matcher} instance to perform operation by
     * @return composed {@code Matcher} instance
     * @throws NullPointerException if {@code after} is null
     */
    default Matcher<T> andThen(final Matcher<? super T> after) {
        Objects.requireNonNull(after);
        return (final T t) -> matches(t) && after.matches(t);
    }
}
