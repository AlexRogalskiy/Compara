package com.wildbeeslabs.sensiblemetrics.diffy.generator.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.wildbeeslabs.sensiblemetrics.diffy.generator.utils.Sequences.halvingDecimal;
import static com.wildbeeslabs.sensiblemetrics.diffy.generator.utils.Sequences.halvingIntegral;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Base class for generators of decimal types, such as {@code double} and
 * {@link BigDecimal}. All numbers are converted to/from BigDecimal for processing.
 *
 * @param <T> type of values this generator produces
 */
public abstract class DecimalGenerator<T extends Number> extends Generator<T> {
    protected DecimalGenerator(Class<T> type) {
        super(singletonList(type));
    }

    protected DecimalGenerator(List<Class<T>> types) {
        super(types);
    }

    @Override
    public List<T> doShrink(SourceOfRandomness random, T largestGeneric) {
        if (largestGeneric.equals(leastMagnitude()))
            return emptyList();

        // We work with BigDecimal, so convert all inputs
        BigDecimal largest = widen().apply(largestGeneric);
        BigDecimal least = widen().apply(leastMagnitude());

        List<T> results = new ArrayList<>();

        // Positive numbers are considered easier than negative ones
        if (negative(largestGeneric))
            results.add(negate(largestGeneric));

        // Try your luck by testing the smallest possible value
        results.add(leastMagnitude());

        // Try values between smallest and largest, with smaller and smaller increments as we approach the largest

        // Integrals are considered easier than decimals
        results.addAll(shrunkenIntegrals(largest, least));
        results.addAll(shrunkenDecimals(largest, least));

        return results;
    }

    private List<T> shrunkenIntegrals(BigDecimal largest, BigDecimal least) {
        return decimalsFrom(
            stream(halvingIntegral(
                largest.toBigInteger(),
                least.toBigInteger())
                    .spliterator(),
                false)
                .map(BigDecimal::new));
    }

    private List<T> shrunkenDecimals(BigDecimal largest, BigDecimal least) {
        return decimalsFrom(
            stream(halvingDecimal(largest, least).spliterator(), false));
    }

    private List<T> decimalsFrom(Stream<BigDecimal> stream) {
        return stream.limit(15)
            .map(narrow())
            .filter(inRange())
            .distinct()
            .collect(toList());
    }

    /**
     * @return a function converting a value of the base type into a {@link BigDecimal}.
     */
    protected abstract Function<T, BigDecimal> widen();

    /**
     * @return a function converting a {@link BigDecimal} into the equivalent value in the base type.
     */
    protected abstract Function<BigDecimal, T> narrow();

    /**
     * @return a predicate checking whether its input is in the configured range.
     */
    protected abstract Predicate<T> inRange();

    /**
     * @return the lowest magnitude number, respecting the configured range. The ideal shrink value is always this value (i.e. this value cannot be shrunk any further).
     */
    protected abstract T leastMagnitude();

    /**
     * @return whether the given number is negative or not.
     */
    protected abstract boolean negative(T target);

    /**
     * Used when shrinking negative numbers to add the positive equivalent value at the top of shrinks list.
     *
     * @param target always a negative number
     * @return the positive equivalent to target
     */
    protected abstract T negate(T target);
}
