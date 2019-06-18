package com.wildbeeslabs.sensiblemetrics.diffy.validator.digits.impl;

import com.wildbeeslabs.sensiblemetrics.diffy.validator.digits.iface.DigitProcessorValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Modulus 10 <b>ABA Number</b> (or <b>Routing Transit Number</b> (RTN)) Check Digit
 * calculation/validation.
 *
 * <p>
 * ABA Numbers (or Routing Transit Numbers) are a nine digit numeric code used
 * to identify American financial institutions for things such as checks or deposits
 * (ABA stands for the American Bankers Association).
 * </p>
 * <p>
 * Check digit calculation is based on <i>modulus 10</i> with digits being weighted
 * based on their position (from right to left) as follows:
 *
 * <ul>
 * <li>Digits 1, 4 and &amp; 7 are weighted 1</li>
 * <li>Digits 2, 5 and &amp; 8 are weighted 7</li>
 * <li>Digits 3, 6 and &amp; 9 are weighted 3</li>
 * </ul>
 *
 * <p>
 * For further information see
 * <a href="http://en.wikipedia.org/wiki/Routing_transit_number">Wikipedia -
 * Routing transit number</a>.
 * </p>
 *
 * @version $Revision: 1739356 $
 * @since Validator 1.4
 */

/**
 * Aban {@link BaseDigitValidator} implementation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ABANDigitValidator extends BaseDigitValidator {

    /**
     * Singleton Routing Transit Number Check Digit instance
     */
    public static final DigitProcessorValidator ABAN_CHECK_DIGIT = new ABANDigitValidator();

    /**
     * weighting given to digits depending on their right position
     */
    private static final int[] POSITION_WEIGHT = new int[]{3, 1, 7};

    /**
     * Construct a modulus 10 Check Digit routine for ABA Numbers.
     */
    public ABANDigitValidator() {
        super(10);
    }

    /**
     * Calculates the <i>weighted</i> value of a character in the
     * code at a specified position.
     * <p>
     * ABA Routing numbers are weighted in the following manner:
     * <pre><code>
     *     left position: 1  2  3  4  5  6  7  8  9
     *            weight: 3  7  1  3  7  1  3  7  1
     * </code></pre>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos   The position of the character in the code, counting from left to right
     * @param rightPos  The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 3];
        return charValue * weight;
    }
}
