package com.wildbeeslabs.sensiblemetrics.diffy.validator.impl;

import com.wildbeeslabs.sensiblemetrics.diffy.exception.ValidationException;
import com.wildbeeslabs.sensiblemetrics.diffy.validator.digits.iface.DigitProcessorValidator;
import com.wildbeeslabs.sensiblemetrics.diffy.validator.iface.GenericProcessorValidator;
import lombok.Data;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;

import java.util.Objects;

/**
 * Generic <b>Code Validation</b> providing format, minimum/maximum
 * length and {@link CheckDigit} validations.
 * <p>
 * Performs the following validations on a code:
 * <ul>
 * <li>if the code is null, return null/false as appropriate</li>
 * <li>trim the input. If the resulting code is empty, return null/false as appropriate</li>
 * <li>Check the <i>format</i> of the code using a <i>regular expression.</i> (if specified)</li>
 * <li>Check the <i>minimum</i> and <i>maximum</i> length  (if specified) of the <i>parsed</i> code
 * (i.e. parsed by the <i>regular expression</i>).</li>
 * <li>Performs {@link CheckDigit} validation on the parsed code (if specified).</li>
 * <li>The {@link #validate(String)} method returns the trimmed, parsed input (or null if validation failed)</li>
 * </ul>
 * <p>
 * <b>Note</b>
 * The {@link #validate(String)} method will return true if the input passes validation.
 * Since this includes trimming as well as potentially dropping parts of the input,
 * it is possible for a String to pass validation
 * but fail the checkdigit test if passed directly to it (the check digit routines generally don't trim input
 * nor do they generally check the format/length).
 * To be sure that you are passing valid input to a method use {@link #validate(String)} as follows:
 * <pre>
 * Object valid = validator.validate(input);
 * if (valid != null) {
 *    some_method(valid.toString());
 * }
 * </pre>
 * <p>
 * Configure the validator with the appropriate regular expression, minimum/maximum length
 * and {@link CheckDigit} validator and then call one of the two validation
 * methods provided:</p>
 * <ul>
 * <li><code>boolean isValid(code)</code></li>
 * <li><code>String validate(code)</code></li>
 * </ul>
 * <p>
 * Codes often include <i>format</i> characters - such as hyphens - to make them
 * more easily human readable. These can be removed prior to length and check digit
 * validation by  specifying them as a <i>non-capturing</i> group in the regular
 * expression (i.e. use the <code>(?:   )</code> notation).
 * <br>
 * Or just avoid using parentheses except for the parts you want to capture
 *
 * @version $Revision: 1781789 $
 * @since Validator 1.4
 */
@Data
public final class CodeValidator implements GenericProcessorValidator<String, Object, ValidationException> {

    private final int minLength;
    private final int maxLength;
    private final DigitProcessorValidator validator;
    private final RegexValidator regexValidator;

    /**
     * Construct a code validator with a specified regular
     * expression and {@link CheckDigit}.
     * The RegexValidator validator is created to be case-sensitive
     *
     * @param regex     The format regular expression
     * @param validator The check digit validation routine
     */
    public CodeValidator(final String regex, final DigitProcessorValidator validator) {
        this(regex, -1, -1, validator);
    }

    /**
     * Construct a code validator with a specified regular
     * expression, length and {@link CheckDigit}.
     * The RegexValidator validator is created to be case-sensitive
     *
     * @param regex     The format regular expression.
     * @param length    The length of the code
     *                  (sets the mimimum/maximum to the same)
     * @param validator The check digit validation routine
     */
    public CodeValidator(final String regex, int length, final DigitProcessorValidator validator) {
        this(regex, length, length, validator);
    }

    /**
     * Construct a code validator with a specified regular
     * expression, minimum/maximum length and {@link CheckDigit} validation.
     * The RegexValidator validator is created to be case-sensitive
     *
     * @param regex     The regular expression
     * @param minLength The minimum length of the code
     * @param maxLength The maximum length of the code
     * @param validator The check digit validation routine
     */
    public CodeValidator(final String regex, int minLength, int maxLength, final DigitProcessorValidator validator) {
        if (Objects.nonNull(regex) && regex.length() > 0) {
            this.regexValidator = new RegexValidator(regex);
        } else {
            this.regexValidator = null;
        }
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.validator = validator;
    }

    /**
     * Construct a code validator with a specified regular expression,
     * validator and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param validator      The check digit validation routine.
     */
    public CodeValidator(final RegexValidator regexValidator, final DigitProcessorValidator validator) {
        this(regexValidator, -1, -1, validator);
    }

    /**
     * Construct a code validator with a specified regular expression,
     * validator, length and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param length         The length of the code
     *                       (sets the mimimum/maximum to the same value)
     * @param validator      The check digit validation routine
     */
    public CodeValidator(final RegexValidator regexValidator, int length, final DigitProcessorValidator validator) {
        this(regexValidator, length, length, validator);
    }

    /**
     * Construct a code validator with a specified regular expression
     * validator, minimum/maximum length and {@link CheckDigit} validation.
     *
     * @param regexValidator The format regular expression validator
     * @param minLength      The minimum length of the code
     * @param maxLength      The maximum length of the code
     * @param validator      The check digit validation routine
     */
    public CodeValidator(final RegexValidator regexValidator, int minLength, int maxLength, final DigitProcessorValidator validator) {
        this.regexValidator = regexValidator;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.validator = validator;
    }

    /**
     * Validate the code returning either <code>true</code>
     * or <code>false</code>.
     * <p>
     * This calls {@link #validate(String)} and returns false
     * if the return value is null, true otherwise.
     * <p>
     * Note that {@link #validate(String)} trims the input
     * and if there is a {@link RegexValidator} it may also
     * change the input as part of the validation.
     *
     * @param input The code to validate
     * @return <code>true</code> if valid, otherwise
     * <code>false</code>
     */
    @Override
    public boolean validate(final String input) {
        return Objects.nonNull(this.process(input));
    }

    /**
     * Validate the code returning either the valid code or
     * <code>null</code> if invalid.
     * <p>
     * Note that this method trims the input
     * and if there is a {@link RegexValidator} it may also
     * change the input as part of the validation.
     *
     * @param input The code to validate
     * @return The code if valid, otherwise <code>null</code>
     * if invalid
     */
    @Override
    public Object processOrThrow(final String input) throws ValidationException {
        if (Objects.isNull(input)) {
            return null;
        }
        String code = input.trim();
        if (code.length() == 0) {
            return null;
        }

        // validate/reformat using regular expression
        if (Objects.nonNull(this.regexValidator)) {
            code = this.regexValidator.process(code);
            if (Objects.isNull(code)) {
                return null;
            }
        }

        // check the length (must be done after validate as that can change the code)
        if ((this.minLength >= 0 && code.length() < this.minLength) || (this.maxLength >= 0 && code.length() > this.maxLength)) {
            return null;
        }
        try {
            if (Objects.nonNull(this.validator) && !this.validator.validate(code)) {
                return null;
            }
        } catch (Throwable t) {
            throw new ValidationException(String.format("ERROR: cannot validate input argument = {%s}", input), t);
        }
        return code;
    }
}
