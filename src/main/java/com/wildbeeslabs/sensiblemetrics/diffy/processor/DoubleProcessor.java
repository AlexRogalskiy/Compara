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
package com.wildbeeslabs.sensiblemetrics.diffy.processor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.text.Format;
import java.util.Locale;

/**
 * <p><b>Double Validation</b> and Conversion routines (<code>java.lang.Double</code>).</p>
 *
 * <p>This validator provides a number of methods for
 * validating/converting a <code>String</code> value to
 * a <code>Double</code> using <code>java.text.NumberFormat</code>
 * to parse either:</p>
 * <ul>
 * <li>using the default format for the default <code>Locale</code></li>
 * <li>using a specified pattern with the default <code>Locale</code></li>
 * <li>using the default format for a specified <code>Locale</code></li>
 * <li>using a specified pattern with a specified <code>Locale</code></li>
 * </ul>
 *
 * <p>Use one of the <code>isValid()</code> methods to just validate or
 * one of the <code>validate()</code> methods to validate and receive a
 * <i>converted</i> <code>Double</code> value.</p>
 *
 * <p>Once a value has been successfully converted the following
 * methods can be used to perform minimum, maximum and range checks:</p>
 * <ul>
 * <li><code>minValue()</code> checks whether the value is greater
 * than or equal to a specified minimum.</li>
 * <li><code>maxValue()</code> checks whether the value is less
 * than or equal to a specified maximum.</li>
 * <li><code>isInRange()</code> checks whether the value is within
 * a specified range of values.</li>
 * </ul>
 *
 * <p>So that the same mechanism used for parsing an <i>input</i> value
 * for validation can be used to format <i>output</i>, corresponding
 * <code>format()</code> methods are also provided. That is you can
 * format either:</p>
 * <ul>
 * <li>using the default format for the default <code>Locale</code></li>
 * <li>using a specified pattern with the default <code>Locale</code></li>
 * <li>using the default format for a specified <code>Locale</code></li>
 * <li>using a specified pattern with a specified <code>Locale</code></li>
 * </ul>
 *
 * @version $Revision: 1739356 $
 * @since Validator 1.3.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DoubleProcessor extends AbstractNumberProcessor {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = 2673902050356612852L;

    /**
     * Default {@link DoubleProcessor} instance
     */
    private static final DoubleProcessor VALIDATOR = new DoubleProcessor();

    /**
     * Return a singleton instance of this validator.
     *
     * @return A singleton instance of the DoubleProcessor.
     */
    public static DoubleProcessor getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance.
     */
    public DoubleProcessor() {
        this(true, STANDARD_FORMAT);
    }

    /**
     * <p>Construct an instance with the specified strict setting
     * and format type.</p>
     *
     * <p>The <code>formatType</code> specified what type of
     * <code>NumberFormat</code> is created - valid types
     * are:</p>
     * <ul>
     * <li>AbstractNumberProcessor.STANDARD_FORMAT -to create
     * <i>standard</i> number formats (the default).</li>
     * <li>AbstractNumberProcessor.CURRENCY_FORMAT -to create
     * <i>currency</i> number formats.</li>
     * <li>AbstractNumberProcessor.PERCENT_FORMAT -to create
     * <i>percent</i> number formats (the default).</li>
     * </ul>
     *
     * @param strict     <code>true</code> if strict
     *                   <code>Format</code> parsing should be used.
     * @param formatType The <code>NumberFormat</code> type to
     *                   create for validation, default is STANDARD_FORMAT.
     */
    public DoubleProcessor(boolean strict, int formatType) {
        super(strict, formatType, true);
    }

    /**
     * <p>Validate/convert a <code>Double</code> using the default
     * <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @return The parsed <code>Double</code> if valid or <code>null</code>
     * if inva
     */
    @Override
    public Number processOrThrow(final String value) {
        return this.parse(value, null, null);
    }

    /**
     * <p>Validate/convert a <code>Double</code> using the
     * specified <i>pattern</i>.
     *
     * @param value   The value validation is being performed on.
     * @param pattern The pattern used to validate the value against.
     * @return The parsed <code>BigDecimal</code> if valid or <code>null</code> if invalid.
     */
    @Override
    public Number process(final String value, final String pattern) {
        return this.parse(value, pattern, null);
    }

    /**
     * <p>Validate/convert a <code>Double</code> using the
     * specified <code>Locale</code>.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use for the number format, system default if null.
     * @return The parsed <code>Double</code> if valid or <code>null</code> if invalid.
     */
    @Override
    public Number process(final String value, final Locale locale) {
        return this.parse(value, null, locale);
    }

    /**
     * <p>Validate/convert a <code>Double</code> using the
     * specified pattern and/ or <code>Locale</code>.
     *
     * @param value   The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *                default for the <code>Locale</code> if <code>null</code>.
     * @param locale  The locale to use for the date format, system default if null.
     * @return The parsed <code>Double</code> if valid or <code>null</code> if invalid.
     */
    @Override
    public Number process(final String value, final String pattern, final Locale locale) {
        return this.parse(value, pattern, locale);
    }

    /**
     * Check if the value is within a specified range.
     *
     * @param value The <code>Number</code> value to check.
     * @param min   The minimum value of the range.
     * @param max   The maximum value of the range.
     * @return <code>true</code> if the value is within the
     * specified range.
     */
    public boolean isInRange(double value, double min, double max) {
        return (value >= min && value <= max);
    }

    /**
     * Check if the value is within a specified range.
     *
     * @param value The <code>Number</code> value to check.
     * @param min   The minimum value of the range.
     * @param max   The maximum value of the range.
     * @return <code>true</code> if the value is within the
     * specified range.
     */
    public boolean isInRange(final Double value, double min, double max) {
        return this.isInRange(value.doubleValue(), min, max);
    }

    /**
     * Check if the value is greater than or equal to a minimum.
     *
     * @param value The value validation is being performed on.
     * @param min   The minimum value.
     * @return <code>true</code> if the value is greater than
     * or equal to the minimum.
     */
    public boolean minValue(double value, double min) {
        return (value >= min);
    }

    /**
     * Check if the value is greater than or equal to a minimum.
     *
     * @param value The value validation is being performed on.
     * @param min   The minimum value.
     * @return <code>true</code> if the value is greater than
     * or equal to the minimum.
     */
    public boolean minValue(final Double value, double min) {
        return this.minValue(value.doubleValue(), min);
    }

    /**
     * Check if the value is less than or equal to a maximum.
     *
     * @param value The value validation is being performed on.
     * @param max   The maximum value.
     * @return <code>true</code> if the value is less than
     * or equal to the maximum.
     */
    public boolean maxValue(double value, double max) {
        return (value <= max);
    }

    /**
     * Check if the value is less than or equal to a maximum.
     *
     * @param value The value validation is being performed on.
     * @param max   The maximum value.
     * @return <code>true</code> if the value is less than
     * or equal to the maximum.
     */
    public boolean maxValue(final Double value, double max) {
        return this.maxValue(value.doubleValue(), max);
    }

    /**
     * Convert the parsed value to a <code>Double</code>.
     *
     * @param value     The parsed <code>Number</code> object created.
     * @param formatter The Format used to parse the value with.
     * @return The validated/converted <code>Double</code> value if valid
     * or <code>null</code> if invalid.
     */
    @Override
    protected Number processParsedValue(final Object value, final Format formatter) {
        if (value instanceof Double) {
            return (Number) value;
        }
        return Double.valueOf(((Number) value).doubleValue());
    }
}
