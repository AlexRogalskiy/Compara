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
package com.wildbeeslabs.sensiblemetrics.diffy.executor.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wildbeeslabs.sensiblemetrics.diffy.executor.property.TaskExecutorProperty;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Objects;

/**
 * Task executor configuration
 */
@Slf4j
public class TaskExecutorConfiguration {

    /**
     * Returns {@link TaskExecutorProperty} by input file name {@link String}
     *
     * @param fileName - initial input file name {@link String}
     * @return {@link TaskExecutorProperty}
     * @throws NullPointerException if file name is {@code null}
     */
    @Nullable
    public static TaskExecutorProperty getProperty(final String fileName) {
        Objects.requireNonNull(fileName, "File name should not be null");
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        TaskExecutorProperty property = null;
        try {
            final URL url = TaskExecutorConfiguration.class.getClassLoader().getResource(fileName);
            property = mapper.readValue(url, TaskExecutorProperty.class);
        } catch (Exception e) {
            log.error(String.format("ERROR: cannot read properties from file = {%s}", fileName), e);
        }
        return property;
    }
}