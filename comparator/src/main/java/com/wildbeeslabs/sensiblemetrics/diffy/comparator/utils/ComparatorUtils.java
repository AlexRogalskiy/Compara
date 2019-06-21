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
package com.wildbeeslabs.sensiblemetrics.diffy.utils;

import com.codepoetics.protonpack.StreamUtils;
import com.google.common.collect.Iterables;
import com.wildbeeslabs.sensiblemetrics.diffy.annotation.Factory;
import com.wildbeeslabs.sensiblemetrics.diffy.common.entry.iface.Delta;
import com.wildbeeslabs.sensiblemetrics.diffy.common.utils.impl.DefaultTransition;
import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.wildbeeslabs.sensiblemetrics.diffy.utility.ServiceUtils.listOf;
import static com.wildbeeslabs.sensiblemetrics.diffy.utility.ServiceUtils.streamOf;

/**
 * Comparator utilities implementation {@link Comparator}
 *
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@Slf4j
@UtilityClass
@SuppressWarnings("unchecked")
public class ComparatorUtils {

    /**
     * Default {@link Comparator}
     *
     * @see ComparableComparator#getInstance
     */
    public static final Comparator DEFAULT_COMPARATOR = ComparableComparator.getInstance();

    /**
     * Lexicographic order {@link CharSequence} comparator
     */
    public final static Comparator<CharSequence> LEXICOGRAPHIC_ORDER = (s1, s2) -> {
        Objects.requireNonNull(s1, "First string should not be null");
        Objects.requireNonNull(s2, "Last string should not be null");

        final int lens1 = s1.length();
        final int lens2 = s2.length();
        final int max = Math.min(lens1, lens2);

        for (int i = 0; i < max; i++) {
            final char c1 = s1.charAt(i);
            final char c2 = s2.charAt(i);
            if (c1 != c2)
                return c1 - c2;
        }
        return lens1 - lens2;
    };

    /**
     * Default class {@link Comparator}
     */
    private static final Comparator<Class<?>> CLASS_COMPARATOR = Comparator.comparing(Class::getSimpleName);

    /**
     * Default comparator with false first order {@link Comparator}
     */
    public static final Comparator<? super Boolean> DEFAULT_FALSE_FIRST_COMPARATOR = (a, b) -> Objects.equals(a, b) ? 0 : b ? -1 : 1;

    /**
     * Default comparator with true first order {@link Comparator}
     */
    public static final Comparator<? super Boolean> DEFAULT_TRUE_FIRST_COMPARATOR = (a, b) -> Objects.equals(a, b) ? 0 : a ? -1 : 1;

    /**
     * Returns boolean comparator with false first order {@link Comparator}
     *
     * @return boolean comparator with false first order {@link Comparator}
     */
    public static Comparator<? super Boolean> lastIf() {
        return DEFAULT_FALSE_FIRST_COMPARATOR;
    }

    /**
     * Returns boolean comparator with true first order {@link Comparator}
     *
     * @return boolean comparator with true first order {@link Comparator}
     */
    public static Comparator<? super Boolean> firstIf() {
        return DEFAULT_TRUE_FIRST_COMPARATOR;
    }

    /**
     * Returns comparator with false first order {@link Comparator} with initial predicate value {@link Predicate}
     *
     * @param <T>       type of value to be compared by
     * @param predicate - initial input predicate value {@link Predicate}
     * @return comparator with false first order {@link Comparator}
     */
    public static <T> Comparator<? super T> lastIf(@NonNull final Predicate<T> predicate) {
        return (a, b) -> {
            final boolean pa = predicate.test(a);
            final boolean pb = predicate.test(b);
            return pa == pb ? 0 : pb ? -1 : 1;
        };
    }

    /**
     * Default method name {@link Comparator} in ascending lexicographic sort order
     */
    public static final Comparator<Method> NAME_ASCENDING = (m1, m2) -> {
        final int comparison = m1.getName().compareTo(m2.getName());
        if (comparison != 0) {
            return comparison;
        }
        return m1.toString().compareTo(m2.toString());
    };

    /**
     * Default method name {@link Comparator}
     */
    public static final Comparator<Method> DEFAULT = (m1, m2) -> {
        int i1 = m1.getName().hashCode();
        int i2 = m2.getName().hashCode();
        if (i1 != i2) {
            return i1 < i2 ? -1 : 1;
        }
        return NAME_ASCENDING.compare(m1, m2);
    };

    /**
     * Returns comparator with true first order {@link Comparator} by initial predicate value {@link Predicate}
     *
     * @param <T>       type of value to be compared by
     * @param predicate - initial input predicate value {@link Predicate}
     * @return comparator with true first order {@link Comparator}
     */
    public static <T> Comparator<? super T> firstIf(@NonNull final Predicate<T> predicate) {
        return (a, b) -> {
            final boolean pa = predicate.test(a);
            final boolean pb = predicate.test(b);
            return pa == pb ? 0 : pa ? -1 : 1;
        };
    }

    /**
     * Returns comparator with true first order {@link Comparator} by initial predicate value {@link Predicate}, first order comparator {@link Comparator}, last order comparator {@link Comparator}
     *
     * @param <T>        type of value to be compared by
     * @param predicate  - initial input predicate value {@link Predicate}
     * @param firstOrder - initial input first order comparator {@link Comparator}
     * @param lastOrder  - initial input last order comparator {@link Comparator}
     * @return comparator with true first order {@link Comparator}
     */
    public static <T> Comparator<? super T> firstIf(@NonNull final Predicate<T> predicate, @NonNull final Comparator<T> firstOrder, @NonNull final Comparator<T> lastOrder) {
        return (a, b) -> {
            final boolean pa = predicate.test(a);
            final boolean pb = predicate.test(b);
            if (pa == pb) {
                return (pa ? firstOrder : lastOrder).compare(a, b);
            }
            return pb ? -1 : 1;
        };
    }

    /**
     * Returns comparator with false first order {@link Comparator} by initial predicate value {@link Predicate}, first order comparator {@link Comparator}, last order comparator {@link Comparator}
     *
     * @param <T>        type of value to be compared by
     * @param predicate  - initial input predicate value {@link Predicate}
     * @param firstOrder - initial input first order comparator {@link Comparator}
     * @param lastOrder  - initial input last order comparator {@link Comparator}
     * @return comparator with false first order {@link Comparator}
     */
    public static <T> Comparator<? super T> lastIf(@NonNull final Predicate<T> predicate, final Comparator<T> firstOrder, final Comparator<T> lastOrder) {
        return firstIf(predicate.negate(), firstOrder, lastOrder);
    }

    /**
     * Returns comparator with instances first order {@link Comparator} by initial class instance {@link Class}
     *
     * @param <T>   type of value to be compared by
     * @param clazz - initial input class instance {@link Class}
     * @return comparator with instances first order {@link Comparator}
     */
    public static <T> Comparator<? super T> instancesFirst(@NonNull final Class<? extends T> clazz) {
        return firstIf(clazz::isInstance);
    }

    /**
     * Returns comparator with instances last order {@link Comparator} by initial class instance {@link Class}
     *
     * @param <T>   type of value to be compared by
     * @param clazz - initial input class instance {@link Class}
     * @return comparator with instances last order {@link Comparator}
     */
    public static <T> Comparator<? super T> instancesLast(@NonNull final Class<? extends T> clazz) {
        return lastIf(clazz::isInstance);
    }

    /**
     * Returns comparator with instances first order {@link Comparator} by initial class {@link Class} and comparator instance {@link Comparator}
     *
     * @param <T>        type of value to be compared by
     * @param <U>        type of value to be compared with
     * @param clazz      - initial input class instance {@link Class}
     * @param comparator - initial input comparator instance {@link Comparator} to compare clazz instances by
     * @return comparator with instances first order {@link Comparator}
     */
    public static <T, U> Comparator<? super T> instancesFirst(@NonNull final Class<? extends U> clazz, @NonNull final Comparator<? super U> comparator) {
        return (a, b) -> {
            if (clazz.isInstance(a)) {
                if (clazz.isInstance(b)) {
                    return comparator.compare(clazz.cast(a), clazz.cast(b));
                }
                return -1;
            }
            return clazz.isInstance(b) ? 1 : 0;
        };
    }

    /**
     * Returns comparator with instances last order {@link Comparator} by initial class {@link Class} and comparator instance {@link Comparator}
     *
     * @param <T>        type of value to be compared by
     * @param <U>        type of value to be compared with
     * @param clazz      - initial input class instance {@link Class}
     * @param comparator - initial input comparator instance {@link Comparator} to compare clazz instances by
     * @return comparator with instances last order {@link Comparator}
     */
    public static <T, U> Comparator<? super T> instancesLast(@NonNull final Class<? extends U> clazz, @NonNull final Comparator<? super U> comparator) {
        return (a, b) -> {
            if (clazz.isInstance(a)) {
                if (clazz.isInstance(b)) {
                    return comparator.compare(clazz.cast(a), clazz.cast(b));
                }
                return 1;
            }
            return clazz.isInstance(b) ? -1 : 0;
        };
    }

    /**
     * Returns object {@link Object} comparator instance {@link Comparator}
     *
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return object {@link Object} comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super Object> getObjectComparator(@Nullable final Comparator<? super Object> comparator, boolean nullsInPriority) {
        return new DefaultNullSafeObjectComparator(comparator, nullsInPriority);
    }

    /**
     * Returns char sequence {@link CharSequence} comparator instance {@link Comparator}
     *
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return char sequence {@link CharSequence} comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super CharSequence> getCharSequenceComparator(@Nullable final Comparator<? super CharSequence> comparator, boolean nullsInPriority) {
        return new DefaultNullSafeCharSequenceComparator(comparator, nullsInPriority);
    }

    /**
     * Returns comparable {@link Comparable} comparator instance {@link Comparator}
     *
     * @param <T> type of input element to be compared by
     * @return comparable {@link Comparable} comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static <T> Comparator<? super T> getComparableComparator() {
        return new DefaultComparableComparator();
    }

    /**
     * Returns class {@link Class} comparator instance {@link Comparator}
     *
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return class {@link Class} comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super Class<?>> getClassComparator(final Comparator<? super Class<?>> comparator, boolean nullsInPriority) {
        return new DefaultNullSafeClassComparator(comparator, nullsInPriority);
    }

    /**
     * Returns iterableOf {@link Iterable} comparator instance {@link Comparator}
     *
     * @param <T>             type of input element to be compared by {@link Iterable}
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return iterableOf comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static <T> Comparator<? super Iterable<T>> getIterableComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
        return new DefaultNullSafeIterableComparator(comparator, nullsInPriority);
    }

    /**
     * Returns big decimal {@link BigDecimal} comparator instance {@link Comparator}
     *
     * @param significantDecimalPlaces - initial input significant decimal places argument
     * @param comparator               - initial input comparator instance {@link Comparator}
     * @param nullsInPriority          - initial input "null" priority {@link Boolean}
     * @return big decimal comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super BigDecimal> getBigDecimalComparator(int significantDecimalPlaces, @Nullable final Comparator<? super BigDecimal> comparator, boolean nullsInPriority) {
        return new DefaultNullSafeBigDecimalComparator(significantDecimalPlaces, comparator, nullsInPriority);
    }

    /**
     * Returns map value comparator instance {@link Comparator}
     *
     * @param <K>             type of key entry element
     * @param <V>             type of value entry element to be compared by
     * @param map             - initial input map collection instance {@link Map}
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return value map comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static <K, V> Comparator<? super K> getValueMapComparator(final Map<K, V> map, @Nullable final Comparator<? super V> comparator, boolean nullsInPriority) {
        return new DefaultMapValueComparator(map, comparator, nullsInPriority);
    }

    /**
     * Returns map entry comparator instance {@link Comparator}
     *
     * @param <K>        type of key entry element
     * @param <V>        type of value entry element
     * @param comparator - initial input comparator instance {@link Comparator}
     * @return map entry comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Comparator<? super Map.Entry<K, V>> getMapEntryComparator(@Nullable final Comparator<? super Map.Entry<K, V>> comparator) {
        return DefaultMapEntryComparator
            .<K, V>builder()
            .comparator(comparator)
            .build();
    }

    /**
     * Returns array comparator instance {@link Comparator}
     *
     * @param <T>             type of array element
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return array comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static <T> Comparator<? super T[]> getArrayComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
        return new DefaultNullSafeArrayComparator(comparator, nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical array comparator instance {@link LexicographicalNullSafeArrayComparator}
     *
     * @param <T>             type of array element
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographical array comparator instance {@link LexicographicalNullSafeArrayComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static <T> Comparator<? super T[]> getLexicographicalArrayComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
        return new LexicographicalNullSafeArrayComparator<>(comparator, nullsInPriority);
    }

    /**
     * Returns null-safe number {@link Number} comparator instance {@link DefaultNullSafeNumberComparator}
     *
     * @param <T>             type of number element {@link Number}
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe number {@link Number} comparator instance {@link DefaultNullSafeNumberComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static <T extends Number> Comparator<? super T> getNumberComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
        return new DefaultNullSafeNumberComparator(comparator, nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical byte array comparator instance {@link LexicographicalNullSafeByteArrayComparator}
     *
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographical byte array comparator instance {@link LexicographicalNullSafeByteArrayComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super byte[]> getByteArrayComparator(boolean nullsInPriority) {
        return new LexicographicalNullSafeByteArrayComparator(nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical short array comparator instance {@link LexicographicalNullSafeShortArrayComparator}
     *
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographical short array comparator instance {@link LexicographicalNullSafeShortArrayComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super short[]> getShortArrayComparator(boolean nullsInPriority) {
        return new LexicographicalNullSafeShortArrayComparator(nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical int array comparator instance {@link Comparator}
     *
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographical int array comparator instance {@link Comparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super int[]> getIntArrayComparator(boolean nullsInPriority) {
        return new LexicographicalNullSafeIntArrayComparator(nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical long array comparator instance {@link LexicographicalNullSafeLongArrayComparator}
     *
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographical long array comparator instance {@link LexicographicalNullSafeLongArrayComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super long[]> getLongArrayComparator(boolean nullsInPriority) {
        return new LexicographicalNullSafeLongArrayComparator(nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical double array comparator instance {@link LexicographicalNullSafeDoubleArrayComparator}
     *
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographical double array comparator instance {@link LexicographicalNullSafeDoubleArrayComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super double[]> getDoubleArrayComparator(boolean nullsInPriority) {
        return new LexicographicalNullSafeDoubleArrayComparator(nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical float array comparator instance {@link LexicographicalNullSafeFloatArrayComparator}
     *
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographical float array comparator instance {@link LexicographicalNullSafeFloatArrayComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super float[]> getFloatArrayComparator(boolean nullsInPriority) {
        return new LexicographicalNullSafeFloatArrayComparator(nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical char array comparator instance {@link LexicographicalNullSafeCharacterArrayComparator}
     *
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographicalchar array comparator instance {@link LexicographicalNullSafeCharacterArrayComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super char[]> getCharacterArrayComparator(boolean nullsInPriority) {
        return new LexicographicalNullSafeCharacterArrayComparator(nullsInPriority);
    }

    /**
     * Returns null-safe lexicographical boolean array comparator instance {@link LexicographicalNullSafeBooleanArrayComparator}
     *
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe lexicographical boolean array comparator instance {@link LexicographicalNullSafeBooleanArrayComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super boolean[]> getBooleanArrayComparator(boolean nullsInPriority) {
        return new LexicographicalNullSafeBooleanArrayComparator(nullsInPriority);
    }

    /**
     * Returns null-safe locale {@link Locale} comparator instance {@link DefaultNullSafeLocaleComparator}
     *
     * @param comparator      - initial input comparator instance {@link Comparator}
     * @param nullsInPriority - initial input "null" priority {@link Boolean}
     * @return null-safe locale {@link Locale} comparator instance {@link DefaultNullSafeLocaleComparator}
     */
    @SuppressWarnings("unchecked")
    @Factory
    public static Comparator<? super Locale> getLocaleComparator(@Nullable final Comparator<? super Locale> comparator, boolean nullsInPriority) {
        return new DefaultNullSafeLocaleComparator(comparator, nullsInPriority);
    }

    /**
     * Returns numeric result of initial arguments comparison by {@link Comparator}
     *
     * @param <T>   type of input element to be compared by operation
     * @param first - initial first input argument
     * @param last  - initial last input argument
     * @return numeric value of comparison
     */
    public static <T extends Comparable<? super T>> int compare(final T first, final T last) {
        return compare(first, last, getComparableComparator());
    }

    /**
     * Returns numeric result by initial comparator instance {@link Comparator}
     *
     * @param <T>        type of input element to be compared by operation
     * @param first      - initial first input argument
     * @param last       - initial last input argument
     * @param comparator - initial comparator instance {@link Comparator}
     * @return numeric value of comparison
     */
    public static <T> int compare(final T first, final T last, final Comparator<? super T> comparator) {
        return Objects.compare(first, last, comparator);
    }

    /**
     * Returns numeric result by null-safe integer arguments comparison
     *
     * @param <T>   type of input element to be compared by operation
     * @param first - initial first input argument
     * @param last  - initial last input argument
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static <T extends Number> int intCompareTo(final T first, final T last) {
        return compareTo(Objects.nonNull(first) ? first.intValue() : null, Objects.nonNull(last) ? last.intValue() : null);
    }

    /**
     * Returns numeric result by null-safe long arguments comparison
     *
     * @param <T>   type of input element to be compared by operation
     * @param first - initial first input argument
     * @param last  - initial last input argument
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static <T extends Number> int longCompareTo(final T first, final T last) {
        return compareTo(Objects.nonNull(first) ? first.longValue() : null, Objects.nonNull(last) ? last.longValue() : null);
    }

    /**
     * Returns numeric result by null-safe float arguments comparison
     *
     * @param <T>   type of input element to be compared by operation
     * @param first - initial first input argument
     * @param last  - initial last input argument
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static <T extends Number> int floatCompareTo(final T first, final T last) {
        return compareTo(Objects.nonNull(first) ? first.floatValue() : null, Objects.nonNull(last) ? last.floatValue() : null);
    }

    /**
     * Returns numeric result by null-safe double arguments comparison
     *
     * @param <T>   type of input element to be compared by operation
     * @param first - initial first input argument
     * @param last  - initial last input argument
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static <T extends Number> int doubleCompareTo(final T first, final T last) {
        return compareTo(Objects.nonNull(first) ? first.doubleValue() : null, Objects.nonNull(last) ? last.doubleValue() : null);
    }

    /**
     * Returns numeric result by null-safe BigDecimal-like arguments comparison
     *
     * @param <T>   type of input element to be compared by operation
     * @param first - initial first input argument
     * @param last  - initial last input argument
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static <T extends Number> int bigDecimalCompareTo(final T first, final T last) {
        return bigDecimalCompareTo(Objects.nonNull(first) ? new BigDecimal(first.toString()) : null, Objects.nonNull(last) ? new BigDecimal(last.toString()) : null);
    }

    /**
     * Returns numeric result by null-safe {@link BigDecimal} arguments comparison
     *
     * @param first - initial first input argument {@link BigDecimal}
     * @param last  - initial last input argument {@link BigDecimal}
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static int bigDecimalCompareTo(final BigDecimal first, final BigDecimal last) {
        return compareTo(first, last);
    }

    /**
     * Returns numeric result by null-safe string-like arguments comparison
     *
     * @param <T>   type of input element to be compared by operation
     * @param first - initial first input argument {@link BigDecimal}
     * @param last  - initial last input argument {@link BigDecimal}
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static <T> int stringCompareTo(final T first, final T last) {
        return stringCompareTo(String.valueOf(first), String.valueOf(last));
    }

    /**
     * Returns numeric result by null-safe {@link String} arguments comparison
     *
     * @param first - initial first input argument {@link String}
     * @param last  - initial last input argument {@link String}
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static int stringCompareTo(final String first, final String last) {
        final boolean f1, f2;
        return (f1 = Objects.isNull(first)) ^ (f2 = Objects.isNull(last)) ? (f1 ? -1 : 1) : (f1 && f2 ? 0 : first.compareToIgnoreCase(last));
    }

    /**
     * Returns numeric result by null-safe general arguments comparison
     *
     * @param <T>   type of input element to be compared by operation
     * @param first - initial first input argument
     * @param last  - initial last input argument
     * @return a negative integer, zero, or a positive integer if the first
     * argument is less than, equal to, or greater than the second
     */
    public static <T extends Comparable<? super T>> int compareTo(final T first, final T last) {
        final boolean f1, f2;
        return (f1 = Objects.isNull(first)) ^ (f2 = Objects.isNull(last)) ? f1 ? -1 : 1 : f1 && f2 ? 0 : first.compareTo(last);
    }

    /**
     * Default null-safe {@code T} comparator declaration {@link Comparator}
     *
     * @param <T> type of input element to be compared by operation
     */
    private interface NullSafeComparator<T> extends Comparator<T> {

        /**
         * Returns numeric result of arguments comparison:
         * "-1" - first argument is greater than the last one
         * "1" - last argument is greater than the first one
         * "0" - arguments are equal
         * Byte.MAX_VALUE - if arguments are different (and negate null either)
         *
         * @param first - initial input first argument
         * @param last  - initial input last argument
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        default int compare(final T first, final T last) {
            return safeCompare(first, last);
        }

        /**
         * Returns numeric result of safe type arguments comparison:
         * "-1" - first argument is greater than the last one
         * "1" - last argument is greater than the first one
         * "0" - arguments are equal
         * Byte.MAX_VALUE - if arguments are different (and negate null either)
         *
         * @param first - initial input first argument
         * @param last  - initial input last argument
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        int safeCompare(final T first, final T last);
    }

    /**
     * Returns {@link List} {@link Comparator} by input {@link Comparator} of {@code T} items
     *
     * @param <T>            type of comparable value
     * @param itemComparator - initial input {@link Comparator} of {@code T} items
     * @return {@link List} {@link Comparator}
     */
    public static <T> Comparator<? super List<T>> getListComparator(final Comparator<? super T> itemComparator) {
        return (o1, o2) -> StreamUtils.zip(o1.stream(), o2.stream(), itemComparator::compare)
            .filter(c -> c != 0)
            .findFirst()
            .orElseGet(() -> Integer.compare(o1.size(), o2.size()));
    }

    /**
     * Default abstract null-safe {@code T} comparator implementation {@link Comparator}
     *
     * @param <T> type of input element to be compared by operation
     */
    @Builder
    @Data
    @EqualsAndHashCode
    @ToString
    public static class DefaultNullSafeComparator<T> implements NullSafeComparator<T>, Serializable {

        /**
         * Default explicit serialVersionUID for interoperability
         */
        private static final long serialVersionUID = -2708816211482344601L;

        /**
         * Default comparator instance {@link Comparator}
         */
        private final transient Comparator<? super T> comparator;
        /**
         * Default "null" priority (true - if nulls are first, false - otherwise)
         */
        private final boolean nullsInPriority;

        /**
         * Default null-safe comparator constructor
         */
        public DefaultNullSafeComparator() {
            this(null);
        }

        /**
         * Default null-safe comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeComparator(@Nullable final Comparator<? super T> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe comparator constructor with initial comparator instance {@link Comparator} and "null" priority {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority {@link Boolean}
         */
        public DefaultNullSafeComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            this.comparator = comparator;
            this.nullsInPriority = nullsInPriority;
        }

        /**
         * Returns numeric result of arguments comparison:
         * "-1" - first argument is greater than the last one
         * "1" - last argument is greater than the first one
         * "0" - arguments are equal
         * Byte.MAX_VALUE - if arguments are different (and negate null either)
         *
         * @param first - initial input first argument
         * @param last  - initial input last argument
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        @Override
        public int safeCompare(final T first, final T last) {
            if (first == last) return 0;
            if (Objects.isNull(first)) return (this.isNullsInPriority() ? 1 : -1);
            if (Objects.isNull(last)) return (this.isNullsInPriority() ? -1 : 1);
            return (Objects.isNull(this.getComparator()) ? 0 : Objects.compare(first, last, this.getComparator()));
        }

        /**
         * Returns comparator instance {@link Comparator} based on input comparator {@link Comparator}
         *
         * @param otherComparator - initial input comparator instance {@link Comparator}
         * @return comparator instance {@link Comparator}
         */
        @Override
        @NonNull
        public Comparator<T> thenComparing(@Nonnull final Comparator<? super T> otherComparator) {
            return new DefaultNullSafeComparator<>(
                Objects.isNull(this.getComparator()) ? otherComparator : ((Comparator<T>) this.getComparator()).thenComparing(otherComparator),
                this.isNullsInPriority()
            );
        }

        /**
         * Returns reversed comparator instance {@link Comparator}
         *
         * @return reversed comparator instance {@link Comparator}, or null if no comparator exists
         */
        @Override
        @NonNull
        public Comparator<T> reversed() {
            return new DefaultNullSafeComparator<>(
                Objects.isNull(this.getComparator()) ? null : this.getComparator().reversed(),
                !this.isNullsInPriority()
            );
        }
    }

    /**
     * Default null-safe object {@link Object} comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeObjectComparator<T> extends DefaultNullSafeComparator<T> {

        /**
         * Default null-safe object comparator constructor
         */
        public DefaultNullSafeObjectComparator() {
            this(Comparator.comparing(Object::toString));
        }

        /**
         * Default null-safe object comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeObjectComparator(@Nullable final Comparator<? super Object> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe object comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeObjectComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            super(Optional.ofNullable(comparator).orElseGet(ComparableComparator::getInstance), nullsInPriority);
        }
    }

    /**
     * Default comparable comparator implementation {@link DefaultNullSafeObjectComparator}
     *
     * @param <T> type of input element to be compared by operation
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultComparableComparator<T extends Comparable<? super T>> extends DefaultNullSafeObjectComparator<T> {

        /**
         * Default null-safe comparable comparator constructor
         */
        public DefaultComparableComparator() {
            super(DEFAULT_COMPARATOR, false);
        }
    }

    /**
     * Default null-safe currency {@link Currency} comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeCurrencyComparator extends DefaultNullSafeObjectComparator<Currency> {

        /**
         * Default null-safe currency {@link Currency} comparator constructor
         */
        public DefaultNullSafeCurrencyComparator() {
            super();
        }

        /**
         * Default null-safe currency {@link Currency} comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeCurrencyComparator(@Nullable final Comparator<? super Currency> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe currency {@link Currency} comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeCurrencyComparator(@Nullable final Comparator<? super Currency> comparator, boolean nullsInPriority) {
            super(Optional.ofNullable(comparator).orElseGet(() -> Comparator.comparing(Object::toString)), nullsInPriority);
        }
    }

    /**
     * Default null-safe url {@link URL} comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeUrlComparator extends DefaultNullSafeObjectComparator<URL> {

        /**
         * Default null-safe url {@link URL} comparator constructor
         */
        public DefaultNullSafeUrlComparator() {
            super();
        }

        /**
         * Default null-safe url {@link URL} comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeUrlComparator(@Nullable final Comparator<? super URL> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe url {@link URL} comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeUrlComparator(@Nullable final Comparator<? super URL> comparator, boolean nullsInPriority) {
            super(Optional.ofNullable(comparator).orElseGet(() -> Comparator.comparing(Object::toString)), nullsInPriority);
        }
    }

    /**
     * Default null-safe char sequence {@link String} comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeCharSequenceComparator extends DefaultNullSafeObjectComparator<CharSequence> {

        /**
         * Default null-safe char sequence comparator constructor
         */
        public DefaultNullSafeCharSequenceComparator() {
            super();
        }

        /**
         * Default null-safe char sequence comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeCharSequenceComparator(@Nullable final Comparator<? super CharSequence> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe char sequence comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeCharSequenceComparator(@Nullable final Comparator<? super CharSequence> comparator, boolean nullsInPriority) {
            super(Optional.ofNullable(comparator).orElseGet(() -> Comparator.comparing(Object::toString)), nullsInPriority);
        }
    }

    /**
     * Default null-safe class {@link Class} comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeClassComparator extends DefaultNullSafeObjectComparator<Class<?>> {

        /**
         * Default null-safe class comparator constructor
         */
        public DefaultNullSafeClassComparator() {
            this(null);
        }

        /**
         * Default null-safe class comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeClassComparator(@Nullable final Comparator<? super Class<?>> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe class comparator constructor with initil comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeClassComparator(@Nullable final Comparator<? super Class<?>> comparator, boolean nullsInPriority) {
            super(Objects.isNull(comparator) ? (o1, o2) -> {
                if (o1.isAssignableFrom(o2)) return 1;
                if (o2.isAssignableFrom(o1)) return -1;
                return 0;
            } : comparator, nullsInPriority);
        }
    }

    /**
     * Default null-safe locale {@link Locale} comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeLocaleComparator extends DefaultNullSafeObjectComparator<Locale> {

        /**
         * Default null safe locale comparator constructor
         */
        public DefaultNullSafeLocaleComparator() {
            this(null);
        }

        /**
         * Default null safe locale comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeLocaleComparator(@Nullable final Comparator<? super Locale> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe locale comparator constructor with comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeLocaleComparator(@Nullable final Comparator<? super Locale> comparator, boolean nullsInPriority) {
            super(Objects.isNull(comparator) ? (o1, o2) -> {
                if (Objects.equals(o1.getLanguage(), o2.getLanguage())) {
                    if (Objects.equals(o1.getCountry(), o2.getCountry())) {
                        return 0;
                    }
                    return 1;
                }
                return -1;
            } : comparator, nullsInPriority);
        }
    }

    /**
     * Default null-safe throwable {@link Throwable} comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeThrowableComparator<T extends Throwable> extends DefaultNullSafeObjectComparator<T> {

        /**
         * Default null safe locale comparator constructor
         */
        public DefaultNullSafeThrowableComparator() {
            this(null);
        }

        /**
         * Default null safe {@link Throwable} comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeThrowableComparator(@Nullable final Comparator<? super T> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe {@link Throwable} comparator constructor with comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeThrowableComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            super(Objects.isNull(comparator) ? (o1, o2) -> {
                if (o1.getClass().isAssignableFrom(o2.getClass())) return 1;
                if (o2.getClass().isAssignableFrom(o1.getClass())) return -1;
                return Objects.compare(o1.getMessage(), o2.getMessage(), String::compareToIgnoreCase);
            } : comparator, nullsInPriority);
        }
    }

    /**
     * Default null-safe {@code T[]} array comparator implementation {@link DefaultNullSafeComparator}
     *
     * @param <T> type of array element to be compared by operation
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeArrayComparator<T> extends DefaultNullSafeComparator<T[]> {

        /**
         * Default null-safe array comparator constructor
         */
        public DefaultNullSafeArrayComparator() {
            this(null, false);
        }

        /**
         * Default null-safe array comparator constructor with input comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeArrayComparator(@Nullable final Comparator<? super T> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe array comparator constructor with initial comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeArrayComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            super((o1, o2) -> {
                int firstSize = o1.length;
                int lastSize = o2.length;
                if (firstSize < lastSize) return -1;
                if (firstSize > lastSize) return 1;
                final Comparator<? super T> comp = Optional.ofNullable(comparator).orElse(DEFAULT_COMPARATOR);
                for (int i = 0; i < firstSize; i++) {
                    int temp = Objects.compare(o1[i], o2[i], comp);
                    if (0 != temp) return temp;
                }
                return 0;
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code T[]} array comparator implementation {@link DefaultNullSafeComparator}
     *
     * @param <T> type of array element to be compared by operation
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeArrayComparator<T> extends DefaultNullSafeComparator<T[]> {

        /**
         * Default null-safe lexicographical array comparator constructor
         */
        public LexicographicalNullSafeArrayComparator() {
            this(null, false);
        }

        /**
         * Default null-safe lexicographical array comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public LexicographicalNullSafeArrayComparator(@Nullable final Comparator<? super T> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe lexicographical array comparator constructor with initial comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeArrayComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                final Comparator<? super T> comp = Optional.ofNullable(comparator).orElse(DEFAULT_COMPARATOR);
                for (int i = 0; i < minLength; i++) {
                    int result = Objects.compare(o1[i], o2[i], comp);
                    if (0 != result) return result;
                }
                return (o1.length - o2.length);
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code short[]} array comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeShortArrayComparator extends DefaultNullSafeComparator<short[]> {

        /**
         * Default null-safe lexicographical short array comparator constructor
         */
        public LexicographicalNullSafeShortArrayComparator() {
            this(false);
        }

        /**
         * Default null-safe lexicographical short array comparator constructor with initial "null" priority argument {@link Boolean}
         *
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeShortArrayComparator(boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                for (int i = 0; i < minLength; i++) {
                    int result = Short.compare(o1[i], o2[i]);
                    if (0 != result) return result;
                }
                return (o1.length - o2.length);
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code int[]} array comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeIntArrayComparator extends DefaultNullSafeComparator<int[]> {

        /**
         * Default null-safe lexicographical int array comparator constructor
         */
        public LexicographicalNullSafeIntArrayComparator() {
            this(false);
        }

        /**
         * Default null-safe lexicographical int array comparator constructor with initial "null" priority argument {@link Boolean}
         *
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeIntArrayComparator(boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                for (int i = 0; i < minLength; i++) {
                    int result = Integer.compare(o1[i], o2[i]);
                    if (0 != result) return result;
                }
                return (o1.length - o2.length);
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code long[]} array comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeLongArrayComparator extends DefaultNullSafeComparator<long[]> {

        /**
         * Default null-safe lexicographical long array comparator constructor
         */
        public LexicographicalNullSafeLongArrayComparator() {
            this(false);
        }

        /**
         * Default null-safe lexicographical long array comparator constructor with initial "null" priority argument {@link Boolean}
         *
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeLongArrayComparator(boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                for (int i = 0; i < minLength; i++) {
                    int result = Long.compare(o1[i], o2[i]);
                    if (0 != result) return result;
                }
                return (o1.length - o2.length);
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code float[]} array comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeFloatArrayComparator extends DefaultNullSafeComparator<float[]> {

        /**
         * Default null-safe lexicographical float array comparator constructor
         */
        public LexicographicalNullSafeFloatArrayComparator() {
            this(false);
        }

        /**
         * Default null-safe lexicographical float array comparator constructor with initial "null" priority argument {@link Boolean}
         *
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeFloatArrayComparator(boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                for (int i = 0; i < minLength; i++) {
                    int result = Float.compare(o1[i], o2[i]);
                    if (0 != result) return result;
                }
                return (o1.length - o2.length);
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code double[]} array comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeDoubleArrayComparator extends DefaultNullSafeComparator<double[]> {

        /**
         * Default null-safe lexicographical double array comparator constructor
         */
        public LexicographicalNullSafeDoubleArrayComparator() {
            this(false);
        }

        /**
         * Default null-safe lexicographical double array comparator constructor with initial "null" priority argument {@link Boolean}
         *
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeDoubleArrayComparator(boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                for (int i = 0; i < minLength; i++) {
                    int result = Double.compare(o1[i], o2[i]);
                    if (0 != result) return result;
                }
                return (o1.length - o2.length);
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code char[]} array comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeCharacterArrayComparator extends DefaultNullSafeComparator<char[]> {

        /**
         * Default null-safe lexicographical char array comparator constructor
         */
        public LexicographicalNullSafeCharacterArrayComparator() {
            this(false);
        }

        /**
         * Default null-safe lexicographical char array comparator constructor with initial "null" priority argument {@link Boolean}
         *
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeCharacterArrayComparator(boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                for (int i = 0; i < minLength; i++) {
                    int result = Character.compare(o1[i], o2[i]);
                    if (0 != result) return result;
                }
                return (o1.length - o2.length);
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code boolean[]} array comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeBooleanArrayComparator extends DefaultNullSafeComparator<boolean[]> {

        /**
         * Default null-safe lexicographical boolean array comparator constructor
         */
        public LexicographicalNullSafeBooleanArrayComparator() {
            this(false);
        }

        /**
         * Default null-safe lexicographical boolean array comparator constructor with initial "null" priority argument {@link Boolean}
         *
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeBooleanArrayComparator(boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                for (int i = 0; i < minLength; i++) {
                    int result = Boolean.compare(o1[i], o2[i]);
                    if (0 != result) return result;
                }
                return (o1.length - o2.length);
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe lexicographical {@code byte} array comparator implementation {@link DefaultNullSafeComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LexicographicalNullSafeByteArrayComparator extends DefaultNullSafeComparator<byte[]> {

        /**
         * Default unsigned mask filter
         */
        private static final int DEFAULT_UNSIGNED_MASK = 0xFF;

        /**
         * Default null-safe lexicographical boolean array comparator constructor
         */
        public LexicographicalNullSafeByteArrayComparator() {
            this(false);
        }

        /**
         * Default null-safe lexicographical boolean array comparator constructor with initial "null" priority argument {@link Boolean}
         *
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public LexicographicalNullSafeByteArrayComparator(boolean nullsInPriority) {
            super((o1, o2) -> {
                int minLength = Math.min(o1.length, o2.length);
                for (int i = 0; i < minLength; i++) {
                    int result = compareBy(o1[i], o2[i]);
                    if (0 != result) return result;
                }
                return 0;
            }, nullsInPriority);
        }

        /**
         * Compares input objects by value
         *
         * @param a - initial input first type argument
         * @param b - initial input last type argument
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        private static int compareBy(byte a, byte b) {
            return toInt(a) - toInt(b);
        }

        /**
         * Converts initial input value to integer formatted value {@link Integer}
         *
         * @param value - initial input value to be converted by {@byte}
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        private static int toInt(byte value) {
            return value & DEFAULT_UNSIGNED_MASK;
        }
    }

    /**
     * Default null-safe iterableOf {@link Iterable} comparator implementation {@link DefaultNullSafeObjectComparator}
     *
     * @param <T> type of input element to be compared by operation
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeIterableComparator<T> extends DefaultNullSafeObjectComparator<Iterable<T>> {

        /**
         * Default null-safe iterableOf comparator constructor
         */
        public DefaultNullSafeIterableComparator() {
            this(null);
        }

        /**
         * Default null-safe iterableOf comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeIterableComparator(@Nullable final Comparator<? super T> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe iterableOf comparator constructor with initial comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeIterableComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            super((o1, o2) -> {
                int firstSize = Iterables.size(o1);
                int lastSize = Iterables.size(o2);
                if (firstSize < lastSize) return -1;
                if (firstSize > lastSize) return 1;
                final Iterator<T> iteratorFirst = o1.iterator();
                final Iterator<T> iteratorLast = o2.iterator();
                final Comparator<? super T> comp = Optional.ofNullable(comparator).orElseGet(() -> Comparator.comparing(Object::toString));
                while (iteratorFirst.hasNext()) {
                    int temp = Objects.compare(iteratorFirst.next(), iteratorLast.next(), comp);
                    if (0 != temp) return temp;
                }
                return 0;
            }, nullsInPriority);
        }
    }

    /**
     * Default null-safe big decimal {@link BigDecimal} comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeBigDecimalComparator extends DefaultNullSafeObjectComparator<BigDecimal> {

        /**
         * Default null-safe big decimal comparator constructor
         */
        public DefaultNullSafeBigDecimalComparator() {
            this(0, null);
        }

        /**
         * Default null-safe big decimal comparator constructor with initial significant decimal places number, comparator instance {@link Comparator}
         *
         * @param significantDecimalPlaces - initial significant decimal places number
         * @param comparator               - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeBigDecimalComparator(int significantDecimalPlaces, @Nullable final Comparator<? super BigDecimal> comparator) {
            this(significantDecimalPlaces, comparator, false);
        }

        /**
         * Default null-safe big decimal comparator constructor with initial significant decimal places number, comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param significantDecimalPlaces - initial significant decimal places number
         * @param comparator               - initial input comparator instance {@link Comparator}
         * @param nullsInPriority          - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeBigDecimalComparator(int significantDecimalPlaces, @Nullable final Comparator<? super BigDecimal> comparator, boolean nullsInPriority) {
            super((o1, o2) -> {
                final Comparator<? super BigDecimal> comp = Optional.ofNullable(comparator).orElseGet(ComparableComparator::getInstance);
                final BigDecimal firstRounded = o1.setScale(significantDecimalPlaces, RoundingMode.HALF_UP);
                final BigDecimal lastRounded = o2.setScale(significantDecimalPlaces, RoundingMode.HALF_UP);
                return Objects.compare(firstRounded, lastRounded, comp);
            }, nullsInPriority);
        }
    }

    /**
     * Default map value comparator implementation {@link Comparator}
     *
     * @param <K> type of map key element
     * @param <V> type of map value element
     */
    @Data
    @EqualsAndHashCode
    @ToString
    public static class DefaultMapValueComparator<K, V> implements Comparator<K> {

        /**
         * Custom value map instance {@link Map}
         */
        private final Map<K, V> map;
        /**
         * Custom value comparator instance {@link Comparator}
         */
        private final Comparator<? super V> comparator;

        /**
         * Default map value comparator with initial map collection instance {@link Map}
         *
         * @param map - initial input map collection instance {@link Map}
         */
        public DefaultMapValueComparator(final Map<K, V> map) {
            this(map, null, false);
        }

        /**
         * Default value map comparator with initial map collection instance {@link Map}, map value comparator {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param map             - initial input map collection instance {@link Map}
         * @param comparator      - initial input value map comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultMapValueComparator(@NonNull final Map<K, V> map, @Nullable final Comparator<? super V> comparator, boolean nullsInPriority) {
            this.map = map;
            this.comparator = new NullComparator(Optional.ofNullable(comparator).orElseGet(ComparableComparator::getInstance), nullsInPriority);
        }

        /**
         * Returns numeric result of arguments comparison:
         * "-1" - first argument is greater than the last one
         * "1" - last argument is greater than the first one
         * "0" - arguments are equal
         *
         * @param first - initial input first argument
         * @param last  - initial input last argument
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        @Override
        public int compare(final K first, final K last) {
            return Objects.compare(getMap().get(first), getMap().get(last), getComparator());
        }
    }

    /**
     * Default list positional comparator implementation {@link Comparator}
     *
     * @param <T> type of collection element
     */
    @Data
    @EqualsAndHashCode
    @ToString
    public static class DefaultListPositionComparator<T> implements Comparator<T> {

        /**
         * Custom list instance {@link List}
         */
        private final List<? extends T> list;
        /**
         * Custom list comparator instance {@link Comparator}
         */
        private final Comparator<? super Integer> comparator;

        /**
         * Default map value comparator with initial map collection instance {@link Map}
         *
         * @param list - initial input list collection instance {@link List}
         */
        public DefaultListPositionComparator(final List<? extends T> list) {
            this(list, null, false);
        }

        /**
         * Default value map comparator with initial map collection instance {@link Map}, map value comparator {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param list            - initial input list collection instance {@link List}
         * @param comparator      - initial input value map comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultListPositionComparator(@NonNull final List<? extends T> list, @Nullable final Comparator<? super Integer> comparator, boolean nullsInPriority) {
            this.list = list;
            this.comparator = new NullComparator(Optional.ofNullable(comparator).orElseGet(ComparableComparator::getInstance), nullsInPriority);
        }

        /**
         * Returns numeric result of arguments comparison:
         * "-1" - first argument is greater than the last one
         * "1" - last argument is greater than the first one
         * "0" - arguments are equal
         *
         * @param first - initial input first argument {@code T}
         * @param last  - initial input last argument {@code T}
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        @Override
        public int compare(final T first, final T last) {
            return Objects.compare(getList().indexOf(first), getList().indexOf(last), getComparator());
        }
    }

    /**
     * Default map entry {@code Map.Entry} comparator implementation {@link Comparator}
     */
    @Builder
    @Data
    @EqualsAndHashCode
    @ToString
    public static class DefaultMapEntryComparator<K, V> implements Comparator<Map.Entry<K, V>> {

        /**
         * Custom map entry comparator instance {@link Comparator}
         */
        private final Comparator<? super Map.Entry<K, V>> comparator;

        /**
         * Default map entry comparator
         */
        public DefaultMapEntryComparator() {
            this(null);
        }

        /**
         * Default map entry comparator with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultMapEntryComparator(@Nullable final Comparator<? super Map.Entry<K, V>> comparator) {
            this.comparator = Optional.ofNullable(comparator).orElseGet(() -> Comparator.comparing(Object::toString));
        }

        /**
         * Returns numeric result of arguments comparison:
         * "-1" - first argument is greater than the last one
         * "1" - last argument is greater than the first one
         * "0" - arguments are equal
         *
         * @param first - initial input first argument {@link Map.Entry}
         * @param last  - initial input last argument {@link Map.Entry}
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        @Override
        public int compare(final Map.Entry<K, V> first, final Map.Entry<K, V> last) {
            return Objects.compare(first, last, getComparator());
        }
    }

    /**
     * Default null-safe number {@link Number} comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeNumberComparator<T extends Number> extends DefaultNullSafeObjectComparator<T> {

        /**
         * Default null-safe number comparator constructor
         */
        public DefaultNullSafeNumberComparator() {
            this(null);
        }

        /**
         * Default null-safe number comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeNumberComparator(@Nullable final Comparator<? super T> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe number comparator constructor with initial comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeNumberComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            super(comparator, nullsInPriority);
        }
    }

    /**
     * Default null-safe string array comparator implementation {@link DefaultNullSafeObjectComparator}
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultNullSafeStringArrayComparator extends DefaultNullSafeObjectComparator<String[]> {

        /**
         * Default null-safe number comparator constructor
         */
        public DefaultNullSafeStringArrayComparator() {
            this(null);
        }

        /**
         * Default null-safe number comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultNullSafeStringArrayComparator(@Nullable final Comparator<? super String[]> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe number comparator constructor with initial comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultNullSafeStringArrayComparator(@Nullable final Comparator<? super String[]> comparator, boolean nullsInPriority) {
            super(Objects.isNull(comparator) ? Comparator.comparingInt(StringUtils::length) : comparator, nullsInPriority);
        }
    }

    /**
     * Default multi comparator implementation {@link Comparator}
     *
     * @param <T> type of list element
     */
    @Data
    @EqualsAndHashCode
    @ToString
    public static class DefaultMultiComparator<T> implements Comparator<T> {
        /**
         * Default collection of comparators {@link List}
         */
        private final List<Comparator<? super T>> comparators;

        /**
         * Default multi comparator constructor by input array of {@link Comparator}s
         *
         * @param comparators - initial input array of {@link Comparator}
         */
        public DefaultMultiComparator(final Comparator<? super T>... comparators) {
            this.comparators = streamOf(comparators).collect(Collectors.toList());
        }

        /**
         * Default multi comparator constructor by input {@link Iterable} collection of {@link Comparator}s
         *
         * @param comparators - initial input {@link Iterable} collection of {@link Comparator}s
         */
        public DefaultMultiComparator(final Iterable<Comparator<? super T>> comparators) {
            this.comparators = listOf(comparators);
        }

        /**
         * Returns numeric result of arguments comparison:
         * "-1" - first argument is greater than the last one
         * "1" - last argument is greater than the first one
         * "0" - arguments are equal
         *
         * @param first - initial input first argument {@code T}
         * @param last  - initial input last argument {@code T}
         * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
         */
        @Override
        public int compare(final T first, final T last) {
            return this.getComparators().stream()
                .filter(Objects::nonNull)
                .map(c -> Objects.compare(first, last, c))
                .filter(r -> 0 != r)
                .findFirst()
                .orElse(0);
        }

        public static <T> void sort(final List<T> list, final Comparator<? super T>... comparators) {
            Collections.sort(list, new DefaultMultiComparator<>(comparators));
        }
    }

    /**
     * Default literal comparator implementation {@link Comparator}
     *
     * @param <T> type of list element
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultLiteralComparator<T> extends DefaultNullSafeObjectComparator<T> {

        /**
         * Default predefined elements {@code T} order
         */
        private final T[] predefinedOrder;

        /**
         * Default null-safe currency {@link Currency} comparator constructor
         */
        public DefaultLiteralComparator() {
            this((T[]) null);
        }

        /**
         * Default null-safe currency {@link Currency} comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultLiteralComparator(@Nullable final Comparator<? super T> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe currency {@link Currency} comparator constructor with initial predefinedOrder {@code T}
         *
         * @param predefinedOrder - initial input elements {@code T} that define the order of comparison
         */
        public DefaultLiteralComparator(final T[] predefinedOrder) {
            this(predefinedOrder, null, false);
        }

        /**
         * Default null-safe currency {@link Currency} comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultLiteralComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            this(null, comparator, nullsInPriority);
        }

        /**
         * Default null-safe literal {@code T} comparator constructor with input predefined order, {@link Comparator} and null-priority argument {@link Boolean}
         *
         * @param predefinedOrder - initial input elements {@code T} that define the order of comparison
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultLiteralComparator(final T[] predefinedOrder, @Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            super(comparator, nullsInPriority);
            this.predefinedOrder = Objects.isNull(predefinedOrder) ? null : Arrays.copyOf(predefinedOrder, predefinedOrder.length);
        }

        @Override
        public int safeCompare(final T first, final T last) {
            final int index1 = ArrayUtils.indexOf(this.getPredefinedOrder(), first);
            final int index2 = ArrayUtils.indexOf(this.getPredefinedOrder(), last);
            if (index1 != ArrayUtils.INDEX_NOT_FOUND) {
                if (index2 == ArrayUtils.INDEX_NOT_FOUND) {
                    return -1;
                }
                return Integer.compare(index1, index2);
            } else {
                if (index2 == ArrayUtils.INDEX_NOT_FOUND) {
                    return super.safeCompare(first, last);
                }
                return 1;
            }
        }
    }

    /**
     * Double {@link DefaultNullSafeComparator} implementation
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultDoubleComparator extends DefaultNullSafeComparator<Double> {

        /**
         * Default precision
         */
        public static final double DEFAULT_PRECISION = 0.000005;

        /**
         * Default double precision
         */
        private final double precision;

        /**
         * Default null-safe double comparator constructor
         */
        public DefaultDoubleComparator() {
            this(Comparator.comparing(Object::toString));
        }

        /**
         * Default null-safe double comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultDoubleComparator(@Nullable final Comparator<? super Double> comparator) {
            this(comparator, false, DEFAULT_PRECISION);
        }

        /**
         * Default null-safe double comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultDoubleComparator(@Nullable final Comparator<? super Double> comparator, boolean nullsInPriority) {
            this(comparator, nullsInPriority, DEFAULT_PRECISION);
        }

        /**
         * Default null-safe double comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         * @param precision       - initial input "null" precision argument {@link Double}
         */
        public DefaultDoubleComparator(@Nullable final Comparator<? super Double> comparator, boolean nullsInPriority, double precision) {
            super(Objects.isNull(comparator)
                ? (o1, o2) -> {
                if (closeEnough(o1, o2, precision)) return 0;
                return o1 < o2 ? -1 : 1;
            }
                : comparator, nullsInPriority);
            this.precision = precision;
        }

        /**
         * Returns binary flag by input parameters
         *
         * @param first   - initial input first argument {@link Double}
         * @param last    - initial input last argument {@link Double}
         * @param epsilon - initial input epsilon value
         * @return true - if input {@link Double}s matches, false - otherwise
         */
        private static boolean closeEnough(final Double first, final Double last, double epsilon) {
            return Math.abs(first - last) <= epsilon;
        }
    }

    /**
     * Float {@link DefaultNullSafeComparator} implementation
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DefaultFloatComparator extends DefaultNullSafeComparator<Float> {

        /**
         * Default precision
         */
        public static final float DEFAULT_PRECISION = 0.000005f;

        /**
         * Default float precision
         */
        private final float precision;

        /**
         * Default null-safe double comparator constructor
         */
        public DefaultFloatComparator() {
            this(Comparator.comparing(Object::toString));
        }

        /**
         * Default null-safe double comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DefaultFloatComparator(@Nullable final Comparator<? super Float> comparator) {
            this(comparator, false, DEFAULT_PRECISION);
        }

        /**
         * Default null-safe double comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DefaultFloatComparator(@Nullable final Comparator<? super Float> comparator, boolean nullsInPriority) {
            this(comparator, nullsInPriority, DEFAULT_PRECISION);
        }

        /**
         * Default null-safe float comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         * @param precision       - initial input "null" precision argument {@link Float}
         */
        public DefaultFloatComparator(@Nullable final Comparator<? super Float> comparator, boolean nullsInPriority, float precision) {
            super(Objects.isNull(comparator)
                ? (o1, o2) -> {
                if (closeEnough(o1, o2, precision)) return 0;
                return o1 < o2 ? -1 : 1;
            }
                : comparator, nullsInPriority);
            this.precision = precision;
        }

        /**
         * Returns binary flag by input parameters
         *
         * @param first   - initial input first argument {@link Float}
         * @param last    - initial input last argument {@link Float}
         * @param epsilon - initial input epsilon value
         * @return true - if input {@link Float}s matches, false - otherwise
         */
        private static boolean closeEnough(final Float first, final Float last, float epsilon) {
            return Math.abs(first - last) <= epsilon;
        }
    }

    /**
     * Number {@link DefaultNullSafeComparator} implementation
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static abstract class AbstractComparableNumberComparator<T extends Number & Comparable<T>> extends DefaultNullSafeComparator<T> {

        /**
         * Default null-safe number comparator constructor
         */
        public AbstractComparableNumberComparator() {
            this(null);
        }

        /**
         * Default null-safe number comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public AbstractComparableNumberComparator(@Nullable final Comparator<? super T> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe number comparator constructor with initial comparator instance {@link Comparator} and "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public AbstractComparableNumberComparator(@Nullable final Comparator<? super T> comparator, boolean nullsInPriority) {
            super(Objects.isNull(comparator) ? Comparator.naturalOrder() : comparator, nullsInPriority);
        }
    }

    /**
     * Big decimal {@link AbstractComparableNumberComparator} implementation
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class BigDecimalComparator extends AbstractComparableNumberComparator<BigDecimal> {

        /**
         * an instance of {@link BigDecimalComparator}.
         */
        public static final BigDecimalComparator BIG_DECIMAL_COMPARATOR = new BigDecimalComparator();
    }

    /**
     * Big integer {@link AbstractComparableNumberComparator} implementation
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class BigIntegerComparator extends AbstractComparableNumberComparator<BigInteger> {

        /**
         * Default instance of {@link BigIntegerComparator}.
         */
        public static final BigIntegerComparator BIG_INTEGER_COMPARATOR = new BigIntegerComparator();
    }

    /**
     * Delta {@link DefaultNullSafeComparator} implementation
     *
     * @param <T> type of delta item
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DeltaComparator<T> extends DefaultNullSafeComparator<Delta<T>> {

        /**
         * Default instance of {@link DeltaComparator}.
         */
        public static final Comparator<?> INSTANCE = new DeltaComparator<>();

        /**
         * Default null-safe delta comparator constructor
         */
        public DeltaComparator() {
            this(Comparator.comparing(Object::toString));
        }

        /**
         * Default null-safe delta comparator constructor with initial comparator instance {@link Comparator}
         *
         * @param comparator - initial input comparator instance {@link Comparator}
         */
        public DeltaComparator(@Nullable final Comparator<? super Delta<T>> comparator) {
            this(comparator, false);
        }

        /**
         * Default null-safe delta comparator constructor with input "null" priority argument {@link Boolean}
         *
         * @param comparator      - initial input comparator instance {@link Comparator}
         * @param nullsInPriority - initial input "null" priority argument {@link Boolean}
         */
        public DeltaComparator(@Nullable final Comparator<? super Delta<T>> comparator, boolean nullsInPriority) {
            super(Objects.isNull(comparator)
                ? (o1, o2) -> {
                final int posA = o1.getOriginal().getPosition();
                final int posB = o2.getOriginal().getPosition();
                if (posA > posB) {
                    return 1;
                } else if (posA < posB) {
                    return -1;
                }
                return 0;
            } : comparator, nullsInPriority);
        }
    }

    /**
     * Custom comparable {@link Comparator} implementation
     */
    public enum CustomComparableComparator implements Comparator<Object> {
        INSTANCE;

        /**
         * Comparable based compare implementation.
         *
         * @param obj1 left hand side of comparison
         * @param obj2 right hand side of comparison
         * @return negative, 0, positive comparison value
         */
        @Override
        public int compare(final Object obj1, final Object obj2) {
            return ((Comparable) obj1).compareTo(obj2);
        }
    }

    /**
     * Transition {@link Comparator} implementation
     *
     * @param T type of transition item
     */
    @Data
    @EqualsAndHashCode
    @ToString
    public static class TransitionComparator<T> implements Comparator<DefaultTransition<T>> {

        /**
         * Default transition end point marker
         */
        private final boolean toFirst;

        /**
         * Default transition {@link Comparator}
         */
        public final Comparator<? super T> comparator;

        /**
         * Default transition comparator constructor by input parameters
         *
         * @param toFirst - initial input transition end point marker
         */
        public TransitionComparator(final boolean toFirst) {
            this(toFirst, Comparator.comparing(Object::toString));
        }

        /**
         * Default transition comparator constructor by input parameters
         *
         * @param toFirst    - initial input transition end point marker
         * @param comparator - initial input {@link Comparator}
         */
        public TransitionComparator(final boolean toFirst, @Nullable final Comparator<? super T> comparator) {
            this.toFirst = toFirst;
            this.comparator = Optional.ofNullable(comparator).orElseGet(ComparableComparator::getInstance);
        }

        /**
         * Compares by (min, reverse max, to) or (to, min, reverse max).
         */
        @Override
        public int compare(final DefaultTransition<T> t1, final DefaultTransition<T> t2) {
            if (this.toFirst) {
                if (t1.getTo() != t2.getTo()) {
                    if (Objects.isNull(t1.getTo())) return -1;
                    else if (Objects.isNull(t2.getTo())) return 1;
                    else if (t1.getTo().getNumber() < t2.getTo().getNumber()) return -1;
                    else if (t1.getTo().getNumber() > t2.getTo().getNumber()) return 1;
                }
            }

            if (Objects.compare(t1.getMin(), t2.getMin(), this.getComparator()) < 0) return -1;
            if (Objects.compare(t1.getMin(), t2.getMin(), this.getComparator()) > 0) return 1;
            if (Objects.compare(t1.getMax(), t2.getMax(), this.getComparator()) > 0) return -1;
            if (Objects.compare(t1.getMax(), t2.getMax(), this.getComparator()) > 0) return 1;

            if (!this.toFirst) {
                if (t1.getTo() != t2.getTo()) {
                    if (Objects.isNull(t1.getTo())) return -1;
                    else if (Objects.isNull(t2.getTo())) return 1;
                    else if (t1.getTo().getNumber() < t2.getTo().getNumber()) return -1;
                    else if (t1.getTo().getNumber() > t2.getTo().getNumber()) return 1;
                }
            }
            return 0;
        }
    }
}
