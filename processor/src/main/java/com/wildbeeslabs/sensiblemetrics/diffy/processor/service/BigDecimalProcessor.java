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
package com.wildbeeslabs.sensiblemetrics.diffy.processor.service;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * <p><b>BigDecimal Validation</b> and Conversion routines (<code>java.math.BigDecimal</code>).</p>
 *
 * <p>This validator provides a number of methods for
 * validating/converting a <code>String</code> value to
 * a <code>BigDecimal</code> using <code>java.text.NumberFormat</code>
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
 * <i>converted</i> <code>BigDecimal</code> value.</p>
 *
 * <p>Fraction/decimal values are automatically trimmed to the appropriate length.</p>
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
 * @since IBANEntry 1.3.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BigDecimalProcessor extends AbstractNumberProcessor {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -7431349769514442847L;

    /**
     * Default {@link BigDecimalProcessor} instance
     */
    private static final BigDecimalProcessor VALIDATOR = new BigDecimalProcessor();

    /**
     * Return a singleton instance of this validator.
     *
     * @return A singleton instance of the BigDecimalValidator2.
     */
    public static BigDecimalProcessor getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance.
     */
    public BigDecimalProcessor() {
        this(true);
    }

    /**
     * <p>Construct an instance with the specified strict setting.</p>
     *
     * @param strict <code>true</code> if strict
     *               <code>Format</code> parsing should be used.
     */
    public BigDecimalProcessor(boolean strict) {
        this(strict, STANDARD_FORMAT, true);
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
     * @param strict         <code>true</code> if strict
     *                       <code>Format</code> parsing should be used.
     * @param formatType     The <code>NumberFormat</code> type to
     *                       create for validation, default is STANDARD_FORMAT.
     * @param allowFractions <code>true</code> if fractions are
     *                       allowed or <code>false</code> if integers only.
     */
    public BigDecimalProcessor(boolean strict, int formatType, boolean allowFractions) {
        super(strict, formatType, allowFractions);
    }

    /**
     * <p>Validate/convert a <code>BigDecimal</code> using the default
     * <code>Locale</code>.
     *
     * @param value The value validation is being performed on.
     * @return The parsed <code>BigDecimal</code> if valid or <code>null</code>
     * if invalid.
     */
    @Override
    public Number processOrThrow(final String value) {
        return this.parse(value, null, null);
    }

    /**
     * <p>Validate/convert a <code>BigDecimal</code> using the
     * specified <i>pattern</i>.
     *
     * @param value   The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *                default for the <code>Locale</code> if <code>null</code>.
     * @return The parsed <code>BigDecimal</code> if valid or <code>null</code> if invalid.
     */
    @Override
    public Number process(final String value, final String pattern) {
        return this.parse(value, pattern, null);
    }

    /**
     * <p>Validate/convert a <code>BigDecimal</code> using the
     * specified <code>Locale</code>.
     *
     * @param value  The value validation is being performed on.
     * @param locale The locale to use for the number format, system default if null.
     * @return The parsed <code>BigDecimal</code> if valid or <code>null</code> if invalid.
     */
    @Override
    public Number process(final String value, final Locale locale) {
        return this.parse(value, null, locale);
    }

    /**
     * <p>Validate/convert a <code>BigDecimal</code> using the
     * specified pattern and/ or <code>Locale</code>.
     *
     * @param value   The value validation is being performed on.
     * @param pattern The pattern used to validate the value against, or the
     *                default for the <code>Locale</code> if <code>null</code>.
     * @param locale  The locale to use for the date format, system default if null.
     * @return The parsed <code>BigDecimal</code> if valid or <code>null</code> if invalid.
     */
    @Override
    public Number process(final String value, final String pattern, final Locale locale) {
        return this.parse(value, pattern, locale);
    }

    /**
     * Convert the parsed value to a <code>BigDecimal</code>.
     *
     * @param value     The parsed <code>Number</code> object created.
     * @param formatter The Format used to parse the value with.
     * @return The parsed <code>Number</code> converted to a
     * <code>BigDecimal</code>.
     */
    @Override
    protected Number processParsedValue(final Object value, final Format formatter) {
        BigDecimal decimal = null;
        if (value instanceof Long) {
            decimal = BigDecimal.valueOf(((Long) value).longValue());
        } else {
            decimal = new BigDecimal(value.toString());
        }

        int scale = this.determineScale((NumberFormat) formatter);
        if (scale >= 0) {
            decimal = decimal.setScale(scale, RoundingMode.DOWN);
        }
        return decimal;
    }
}
