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
package com.wildbeeslabs.sensiblemetrics.diffy.processor.digits.impl;

import com.wildbeeslabs.sensiblemetrics.diffy.common.exception.InvalidParameterException;
import com.wildbeeslabs.sensiblemetrics.diffy.processor.digits.iface.DigitProcessor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Modulus 11 <b>ISBN-10</b> Check Digit calculation/validation.
 * <p>
 * ISBN-10 Numbers are a numeric code except for the last (check) digit
 * which can have a value of "X".
 * <p>
 * Check digit calculation is based on <i>modulus 11</i> with digits being weighted
 * based by their position, from right to left  with the first digit being weighted
 * 1, the second 2 and so on. If the check digit is calculated as "10" it is converted
 * to "X".
 * <p>
 * <b>N.B.</b> From 1st January 2007 the book industry will start to use a new 13 digit
 * ISBN number (rather than this 10 digit ISBN number) which uses the EAN-13 / UPC
 * (see {@link EAN13DigitValidator}) standard.
 * <p>
 * For further information see:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/ISBN">Wikipedia - International
 * Standard Book Number (ISBN)</a>.</li>
 * <li><a href="http://www.isbn.org/standards/home/isbn/transition.asp">ISBN-13
 * Transition details</a>.</li>
 * </ul>
 *
 * @version $Revision: 1739356 $
 * @since Validator 1.4
 */

/**
 * Isbn10 {@link BaseDigitProcessor} implementation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ISBN10DigitProcessor extends BaseDigitProcessor {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -4992373905423789117L;

    /**
     * Singleton ISBN-10 Check Digit instance
     */
    public static final DigitProcessor ISBN10_CHECK_DIGIT = new ISBN10DigitProcessor();

    /**
     * Construct a modulus 11 Check Digit routine for ISBN-10.
     */
    public ISBN10DigitProcessor() {
        super(11);  // CHECKSTYLE IGNORE MagicNumber
    }

    /**
     * Calculates the <i>weighted</i> value of a charcter in the
     * code at a specified position.
     *
     * <p>For ISBN-10 (from right to left) digits are weighted
     * by their position.</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos   The position of the character in the code, counting from left to right
     * @param rightPos  The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        return charValue * rightPos;
    }

    /**
     * <p>Convert a character at a specified position to an
     * integer value.</p>
     *
     * <p>Character 'X' check digit converted to 10.</p>
     *
     * @param character The character to convert.
     * @param leftPos   The position of the character in the code, counting from left to right
     * @param rightPos  The position of the character in the code, counting from right to left
     * @return The integer value of the character.
     * @throws InvalidParameterException if an error occurs.
     */
    @Override
    protected int toInt(char character, int leftPos, int rightPos) throws InvalidParameterException {
        if (rightPos == 1 && character == 'X') {
            return 10;
        }
        return super.toInt(character, leftPos, rightPos);
    }

    /**
     * <p>Convert an integer value to a character at a specified position.</p>
     *
     * <p>Value '10' for position 1 (check digit) converted to 'X'.</p>
     *
     * @param charValue The integer value of the character.
     * @return The converted character.
     * @throws InvalidParameterException if an error occurs.
     */
    @Override
    protected String toCheckDigit(int charValue) throws InvalidParameterException {
        if (charValue == 10) {
            return "X";
        }
        return super.toCheckDigit(charValue);
    }
}
