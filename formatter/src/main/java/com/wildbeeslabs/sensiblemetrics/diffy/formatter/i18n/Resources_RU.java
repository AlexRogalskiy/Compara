/*
 * The MIT License
 *
 * Copyright 2018 WildBees Labs.
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
package com.wildbeeslabs.sensiblemetrics.diffy.formatter.i18n;

import com.wildbeeslabs.sensiblemetrics.diffy.common.helpers.iface.Duration;
import com.wildbeeslabs.sensiblemetrics.diffy.common.helpers.iface.TimeFormat;
import com.wildbeeslabs.sensiblemetrics.diffy.common.helpers.iface.TimeMeasure;
import com.wildbeeslabs.sensiblemetrics.diffy.common.helpers.unit.*;
import com.wildbeeslabs.sensiblemetrics.diffy.common.resources.BaseResourceBundle;
import com.wildbeeslabs.sensiblemetrics.diffy.formatter.interfaces.TimeFormatProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Default resources bundle [RU]
 *
 * @author Alex
 * @version 1.0.0
 * @since 2017-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Resources_RU extends Resources implements TimeFormatProvider {
    /**
     * Default {@link Locale} {@code "RU"}
     */
    private static final Locale LOCALE = new Locale("ru");
    /**
     * Default {@link Resources_RU} instance
     */
    private static final Resources_RU INSTANCE = new Resources_RU();

    private Object[][] resources;

    /**
     * Default resources constructor
     */
    private Resources_RU() {
        this.loadResources();
    }

    private static final Object[][] OBJECTS = new Object[0][0];
    private static final int tolerance = 50;
    // see http://translate.sourceforge.net/wiki/l10n/pluralforms
    private static final int russianPluralForms = 3;

    private static class TimeFormatAided implements TimeFormat {

        private final String[] plurals;

        public TimeFormatAided(final String... plurals) {
            if (plurals.length != Resources_RU.russianPluralForms) {
                throw new IllegalArgumentException(String.format("ERROR: Wrong plural forms number for russian language = %s, expected = %s", plurals.length, Resources_RU.russianPluralForms));
            }
            this.plurals = plurals;
        }

        @Override
        public String format(final Duration duration) {
            long quantity = duration.getQuantityRounded(Resources_RU.tolerance);
            final StringBuilder result = new StringBuilder();
            result.append(quantity);
            return result.toString();
        }

        @Override
        public String formatUnrounded(final Duration duration) {
            long quantity = duration.getQuantity();
            final StringBuilder result = new StringBuilder();
            result.append(quantity);
            return result.toString();
        }

        @Override
        public String decorate(final Duration duration, final String time) {
            return performDecoration(
                duration.isInPast(),
                duration.isInFuture(),
                duration.getQuantityRounded(Resources_RU.tolerance),
                time);
        }

        @Override
        public String decorateUnrounded(final Duration duration, final String time) {
            return performDecoration(
                duration.isInPast(),
                duration.isInFuture(),
                duration.getQuantity(),
                time);
        }

        private String performDecoration(boolean past, boolean future, long quantity, final String time) {
            int pluralIdx = (quantity % 10 == 1 && quantity % 100 != 11 ? 0 : (quantity % 10 >= 2 && quantity % 10 <= 4 && (quantity % 100 < 10 || quantity % 100 >= 20)) ? 1 : 2);
            if (pluralIdx > Resources_RU.russianPluralForms) {
                throw new IllegalArgumentException(String.format("ERROR: Wrong plural forms number for russian language = %s, expected = %s", pluralIdx, Resources_RU.russianPluralForms));
            }
            final StringBuilder result = new StringBuilder();
            if (future) {
                result.append("через ");
            }
            result.append(time);
            result.append(StringUtils.SPACE);
            result.append(this.plurals[pluralIdx]);
            if (past) {
                result.append(" назад");
            }
            return result.toString();
        }
    }

    @Override
    public Object[][] getResources() {
        return Resources_RU.OBJECTS;
    }

    @Override
    public TimeFormat getFormat(final TimeMeasure timeUnit) {
        if (timeUnit instanceof NowTimeUnit) {
            return new TimeFormat() {
                @Override
                public String format(final Duration duration) {
                    return this.performFormat(duration);
                }

                @Override
                public String formatUnrounded(final Duration duration) {
                    return this.performFormat(duration);
                }

                private String performFormat(final Duration duration) {
                    if (duration.isInFuture()) {
                        return "сейчас";
                    }
                    if (duration.isInPast()) {
                        return "только что";
                    }
                    return null;
                }

                @Override
                public String decorate(final Duration duration, final String time) {
                    return time;
                }

                @Override
                public String decorateUnrounded(final Duration duration, final String time) {
                    return time;
                }
            };
        } else if (timeUnit instanceof CenturyTimeUnit) {
            return new TimeFormatAided("век", "века", "веков");
        } else if (timeUnit instanceof DayTimeUnit) {
            return new TimeFormatAided("день", "дня", "дней");
        } else if (timeUnit instanceof DecadeTimeUnit) {
            return new TimeFormatAided("десятилетие", "десятилетия", "десятилетий");
        } else if (timeUnit instanceof HourTimeUnit) {
            return new TimeFormatAided("час", "часа", "часов");
        } else if (timeUnit instanceof MillenniumTimeUnit) {
            return new TimeFormatAided("тысячелетие", "тысячелетия", "тысячелетий");
        } else if (timeUnit instanceof MillisecondTimeUnit) {
            return new TimeFormatAided("миллисекунду", "миллисекунды", "миллисекунд");
        } else if (timeUnit instanceof MinuteTimeUnit) {
            return new TimeFormatAided("минуту", "минуты", "минут");
        } else if (timeUnit instanceof MonthTimeUnit) {
            return new TimeFormatAided("месяц", "месяца", "месяцев");
        } else if (timeUnit instanceof SecondTimeUnit) {
            return new TimeFormatAided("секунду", "секунды", "секунд");
        } else if (timeUnit instanceof WeekTimeUnit) {
            return new TimeFormatAided("неделю", "недели", "недель");
        } else if (timeUnit instanceof YearTimeUnit) {
            return new TimeFormatAided("год", "года", "лет");
        }
        return null;
    }

    /**
     * Loads {@link BaseResourceBundle} items
     */
    public void loadResources() {
        this.resources = BaseResourceBundle.getInstance(LOCALE).getResources();
    }

    /**
     * Returns new {@link Resources_RU}
     *
     * @return new {@link Resources_RU}
     */
    public static Resources_RU getInstance() {
        return INSTANCE;
    }
}
