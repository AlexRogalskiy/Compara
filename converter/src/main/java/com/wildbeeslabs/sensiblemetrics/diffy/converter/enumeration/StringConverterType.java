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
package com.wildbeeslabs.sensiblemetrics.diffy.converter.enumeration;

import com.google.common.base.Strings;
import com.wildbeeslabs.sensiblemetrics.diffy.common.utils.RegexUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;

import java.util.function.Function;

/**
 * String converter type {@link Enum}
 */
@Getter
@RequiredArgsConstructor
public enum StringConverterType {
    /**
     * org.apache.commons.lang3.StringUtils
     */
    UPPER_CASE(StringUtils::upperCase),
    LOWER_CASE(StringUtils::lowerCase),
    CAPITALIZE(StringUtils::capitalize),
    CAPITALIZE_FULLY(WordUtils::capitalizeFully),
    UN_CAPITALIZE(StringUtils::uncapitalize),
    SWAP_CASE(StringUtils::swapCase),
    NORMALIZED(StringUtils::normalizeSpace),
    NON_WHITE_SPACE(StringUtils::deleteWhitespace),
    STRIP_ACCENTS(StringUtils::stripAccents),
    STRIP_TO_EMPTY(StringUtils::stripToEmpty),
    STRIP_TO_NULL(StringUtils::stripToNull),
    STRIP(StringUtils::strip),
    TRIM_TO_EMPTY(StringUtils::trimToEmpty),
    TRIM_TO_NULL(StringUtils::trimToNull),
    TRIM(StringUtils::trim),
    CHOP(StringUtils::chop),
    /**
     * com.google.common.base.Strings
     */
    EMPTY_TO_NULL(Strings::emptyToNull),
    NULL_TO_EMPTY(Strings::nullToEmpty),
    /**
     * com.wildbeeslabs.sensiblemetrics.diffy.utility.StringUtils
     */
    SORT(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::sort),
    COMPRESS(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::compress),
    REVERSE(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::reverse),
    CAPITALIZE_WORD_FULLY(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::titleCaseWordFull),
    CAPITALIZE_TITLE(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::titleCaseWord),
    BASE64_ENCODE(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::toBase64),
    BASE64_DECODE(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::fromBase64),
    ENCODE_UTF8(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::encodeUtf8),
    DECODE_UTF8(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::decodeUtf8),
    RANDOM_PASSWORD(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils::generatePassword),
    WRAP_STRING(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils.wrapStr),
    /**
     * com.wildbeeslabs.sensiblemetrics.diffy.utility.ParserUtils
     */
    NATIVE_TO_ASCII(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::native2Ascii),
    STRIP_SLASHES(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::stripSlashes),
    CHOMP_LEADING_SLASH(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::chompLeadingSlash),
    CHOMP_TRAILING_SLASH(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::chompTrailingSlash),
    FIRST_PATH_SEGMENT(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::firstPathSegment),
    SANITIZE(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::sanitizeRequest),
    REMOVE_FORWARD_SLASHES(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::removeAdjacentForwardSlashes),
    HTML_TO_TEXT(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::html2Text),
    HOST(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::getHost),
    FETCH_URL(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::readUrl),
    UN_GZIP(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::ungzip),
    FROM_FILE(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::readFile),
    FROM_FILE2(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::readFile2),
    FROM_FILE3(com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ParserUtils::readFile3),
    /**
     * org.apache.commons.text.StringEscapeUtils
     */
    ESCAPE_JAVA(StringEscapeUtils::escapeJava),
    UNESCAPE_JAVA(StringEscapeUtils::unescapeJava),
    ESCAPE_ECMA_SCRIPT(StringEscapeUtils::escapeEcmaScript),
    UNESCAPE_ECMA_SCRIPT(StringEscapeUtils::unescapeEcmaScript),
    ESCAPE_JSON(StringEscapeUtils::escapeJson),
    UNESCAPE_JSON(StringEscapeUtils::unescapeJson),
    ESCAPE_HTML(StringEscapeUtils::escapeHtml4),
    UNESCAPE_HTML(StringEscapeUtils::unescapeHtml4),
    ESCAPE_XML(StringEscapeUtils::escapeXml11),
    UNESCAPE_XML(StringEscapeUtils::unescapeXml),
    ESCAPE_CSV(StringEscapeUtils::escapeCsv),
    UNESCAPE_CSV(StringEscapeUtils::unescapeCsv),
    UNESCAPE_XSI(StringEscapeUtils::unescapeXSI),
    ESCAPE_XSI(StringEscapeUtils::escapeXSI),
    /**
     * org.apache.commons.codec.digest.DigestUtils
     */
    SHA_384_HEX(DigestUtils::sha384Hex),
    SHA_512_HEX(DigestUtils::sha512Hex),
    SHA_256_HEX(DigestUtils::sha256Hex),
    SHA_1_HEX(DigestUtils::sha1Hex),
    MD5_HEX(DigestUtils::md5Hex),
    MD2_HEX(DigestUtils::md2Hex),
    /**
     * java.util.regex.RegexMatcher
     */
    REGEX_QUOTE_REPLACE(java.util.regex.Matcher::quoteReplacement),
    /**
     * java.util.regex.Pattern
     */
    REGEX_QUOTE(java.util.regex.Pattern::quote),
    /**
     * com.wildbeeslabs.sensiblemetrics.diffy.utility.RegexUtils
     */
    TO_REGEX(RegexUtils::asRegex),
    REGEX_ERROR(RegexUtils::regexError);

    /**
     * String {@link Function} converter operator
     */
    private final Function<String, String> converter;
}
