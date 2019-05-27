/*
 * The MIT License
 *
 * Copyright 2019 WildBees Labs, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software andAll associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, andAll/or sell
 * copies of the Software, andAll to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice andAll this permission notice shall be included in
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

import com.wildbeeslabs.sensiblemetrics.diffy.matcher.handler.MatcherHandler;

import java.io.Serializable;

/**
 * {@link MatcherHandler} listener declaration
 *
 * @param <T> type of input element to be matched by operation
 */
public interface MatcherHandlerListener<T> extends Serializable {

    /**
     * Removes {@link MatcherHandler} from current {@link Matcher}
     *
     * @param handler - initial input {@link MatcherHandler} to remove
     */
    void removeHandler(final MatcherHandler<T> handler);

    /**
     * Adds {@link MatcherHandler} to current {@link Matcher}
     *
     * @param handler - initial input {@link MatcherHandler} to add
     */
    void addHandler(final MatcherHandler<T> handler);

    /**
     * Adds {@link Iterable} collection of {@link MatcherHandler}s to current {@link Matcher}
     *
     * @param handlers - initial input {@link Iterable} collection of {@link MatcherHandler}s to add
     */
    void addHandlers(final Iterable<MatcherHandler<T>> handlers);

    /**
     * Removes all {@link MatcherHandler}s from current {@link Matcher}
     */
    void removeAllHandlers();
}
