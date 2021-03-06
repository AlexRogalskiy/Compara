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
package com.wildbeeslabs.sensiblemetrics.diffy.matcher.enumeration;

import com.wildbeeslabs.sensiblemetrics.diffy.matcher.interfaces.BaseMatcherMode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * RegexMatcher mode type {@link Enum} to process malformed and unexpected data
 * <p>
 * 2 basic implementations are provided:
 * <ul>
 * <li>{@link #STRICT} return "true" on any occurrence</li>
 * <li>{@link #SILENT} ignores any problem</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum BiMatcherModeType implements BaseMatcherMode {
    STRICT(MatcherStatusType.ENABLE, true),
    SILENT(MatcherStatusType.DISABLE, false),
    LENIENT(MatcherStatusType.DISABLE, true),
    SEALED(MatcherStatusType.ENABLE, false);

    /**
     * {@link MatcherStatusType} status
     */
    private final MatcherStatusType status;
    /**
     * Extensible binary flag
     */
    private final boolean extensible;

    /**
     * Return binary flag based on current mode {@code STRICT}
     *
     * @return true - if current mode is {@code STRICT}, false - otherwise
     */
    public boolean isStrict() {
        return this.equals(STRICT);
    }

    /**
     * Returns binary flag based on current mode status {@code ENABLE}
     *
     * @return true - if current mode status is {@code ENABLE}, false - otherwise
     */
    @Override
    public boolean isEnable() {
        return Objects.equals(this.getStatus(), MatcherStatusType.ENABLE);
    }

    /**
     * Returns {@link BiMatcherModeType} ordered by input {@link MatcherStatusType}
     *
     * @param statusType - initial input status type {@link MatcherStatusType}
     * @return {@link BiMatcherModeType}
     */
    @NonNull
    public BiMatcherModeType fromStatus(final MatcherStatusType statusType) {
        if (statusType.isEnable()) {
            return isExtensible() ? STRICT : SEALED;
        }
        return isExtensible() ? LENIENT : SILENT;
    }

    /**
     * Returns {@link BiMatcherModeType} ordered by input extensibility
     *
     * @param extensible - initial input extensible (if true - allows keys in actual that don't appear in expected, false - otherwise)
     * @return {@link BiMatcherModeType}
     */
    @NonNull
    public BiMatcherModeType fromExtension(final boolean extensible) {
        if (extensible) {
            return isEnable() ? STRICT : LENIENT;
        }
        return isEnable() ? SEALED : SILENT;
    }

    /**
     * Returns binary flag based on input {@link BiMatcherModeType}es comparison
     *
     * @param s1 - initial input {@link BiMatcherModeType} to compare with
     * @param s2 - initial input {@link BiMatcherModeType} to compare by
     * @return true - if {@link BiMatcherModeType} are equal, false - otherwise
     */
    public static boolean equals(final BiMatcherModeType s1, final BiMatcherModeType s2) {
        return Objects.equals(s1, s2);
    }
}
