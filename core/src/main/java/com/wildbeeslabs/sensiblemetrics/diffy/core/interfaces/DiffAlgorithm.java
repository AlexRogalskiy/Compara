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
package com.wildbeeslabs.sensiblemetrics.diffy.core.interfaces;

import com.wildbeeslabs.sensiblemetrics.diffy.common.entry.iface.Patch;

/**
 * Copy from https://code.google.com/p/java-diff-utils/.
 * <p>
 * The general interface for computing diffs between two lists of elements of type T.
 *
 * @param <T> The type of the compared elements in the 'lines'.
 * @author <a href="dm.naumenko@gmail.com">Dmitry Naumenko</a>
 */
public interface DiffAlgorithm<T> {

    /**
     * Computes the difference between the original sequence and the revised
     * sequence and returns it as a {@link Patch} object.
     *
     * @param original The original sequence. Must not be {@code null}.
     * @param revised  The revised sequence. Must not be {@code null}.
     * @return The patch representing the diff of the given sequences. Never {@code null}.
     */
    Patch<T> diff(final Iterable<T> original, final Iterable<T> revised);
}
