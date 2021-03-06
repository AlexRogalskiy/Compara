package com.wildbeeslabs.sensiblemetrics.diffy.validator.service;

import com.wildbeeslabs.sensiblemetrics.diffy.validator.utils.DecimalNumberComparatorHelper;

/**
 * Check that the number being validated is less than or equal to the maximum
 * value specified.
 */
public class DecimalMaxValidatorForLong extends AbstractDecimalMaxValidator<Long> {

    public DecimalMaxValidatorForLong(final String maxValue, final boolean inclusive) {
        super(maxValue, inclusive);
    }

    @Override
    protected int compare(final Long number) {
        return DecimalNumberComparatorHelper.compare(number, this.getMaxValue());
    }
}
