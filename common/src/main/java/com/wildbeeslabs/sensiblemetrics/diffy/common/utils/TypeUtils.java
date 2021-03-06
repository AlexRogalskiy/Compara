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
package com.wildbeeslabs.sensiblemetrics.diffy.common.utils;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.collect.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils.formatMessage;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Types utilities implementation
 *
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@Slf4j
@UtilityClass
@SuppressWarnings("unchecked")
public class TypeUtils {

    /**
     * Default {@link ImmutableCollection} of primitive wrapper types
     */
    public static final ImmutableCollection<Class<?>> DEFAULT_PRIMITIVE_WRAPPER_TYPES = getPrimitiveWrapperTypes();
    /**
     * Default {@link ImmutableCollection} of primitive numeric types
     */
    public static final ImmutableCollection<Class<?>> DEFAULT_PRIMITIVE_NUMERIC_TYPES = getPrimitiveNumericTypes();
    /**
     * Default {@link ImmutableBiMap} map of primitive types
     */
    public static final ImmutableBiMap<Class<?>, Class<?>> DEFAULT_PRIMITIVE_TYPES = getPrimitiveTypes();

    public static final ImmutableMultimap<Class<?>, Object> PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES = getPrimitiveOrWrapperDefaultValues();

    /**
     * Default collection {@link Set} of base name types
     */
    public static final Set<String> BASE_TYPE_NAMES = ImmutableSet.<String>builder()
        .add("int")
        .add("date")
        .add("string")
        .add("double")
        .add("float")
        .add("boolean")
        .add("byte")
        .add("object")
        .add("long")
        .add("date-time")
        .add("__file")
        .add("biginteger")
        .add("bigdecimal")
        .build();

    /**
     * Default {@link ImmutableMap} of simple types and corresponding names
     */
    public static final Map<Type, String> BASE_TYPE_NAME_LOOKUP = ImmutableMap.<Type, String>builder()
        .put(Long.TYPE, "long")
        .put(Short.TYPE, "int")
        .put(Integer.TYPE, "int")
        .put(Double.TYPE, "double")
        .put(Float.TYPE, "float")
        .put(Byte.TYPE, "byte")
        .put(Boolean.TYPE, "boolean")
        .put(Character.TYPE, "string")

        .put(Date.class, "date-time")
        .put(java.sql.Date.class, "date")
        .put(String.class, "string")
        .put(Object.class, "object")
        .put(Long.class, "long")
        .put(Integer.class, "int")
        .put(Short.class, "int")
        .put(Double.class, "double")
        .put(Float.class, "float")
        .put(Boolean.class, "boolean")
        .put(Byte.class, "byte")
        .put(BigDecimal.class, "bigdecimal")
        .put(BigInteger.class, "biginteger")
        .put(Currency.class, "string")
        .put(UUID.class, "string")
        .build();

    /**
     * Default {@link ImmutableCollection} of extendable simple types
     */
    @SuppressWarnings("unchecked")
    public static final ImmutableCollection<Class<?>> EXTENDABLE_SIMPLE_TYPES = ImmutableSet.<Class<?>>builder()
        .add(BigDecimal.class)
        .add(BigInteger.class)
        .add(CharSequence.class)
        .add(ThreadLocal.class)
        .add(Calendar.class)
        .add(Date.class)
        .add(Enum.class)
        .add(Number.class)
        .add(Process.class)
        .build();
    /**
     * Default {@link ImmutableCollection} of final simple types
     */
    @SuppressWarnings("unchecked")
    public static final ImmutableCollection<Class<? extends Serializable>> FINAL_SIMPLE_TYPES = ImmutableSet.<Class<? extends Serializable>>builder()
        .add(Class.class)
        .add(URI.class)
        .add(URL.class)
        .add(Currency.class)
        .add(Locale.class)
        .add(UUID.class)
        .add(String.class)
        .build();

    /**
     * Returns {@link ImmutableCollection} of primitive wrapper types
     *
     * @return collection of primitive wrapper types {@link ImmutableCollection}
     */
    public static ImmutableCollection<Class<?>> getPrimitiveWrapperTypes() {
        return ImmutableSet.<Class<?>>builder()
            .add(Boolean.class)
            .add(Character.class)
            .add(Byte.class)
            .add(Short.class)
            .add(Integer.class)
            .add(Long.class)
            .add(Float.class)
            .add(Double.class)
            .add(Void.class)
            .build();
    }

    /**
     * Returns {@link ImmutableBiMap} of primitive / wrapper default values
     *
     * @return collection of primitive / wrapper default values {@link ImmutableBiMap}
     */
    public static ImmutableMultimap<Class<?>, Object> getPrimitiveOrWrapperDefaultValues() {
        return ImmutableMultimap.<Class<?>, Object>builder()
            .put(Boolean.class, false)
            .put(Character.class, '\u0000')
            .put(Byte.class, (byte) 0)
            .put(Short.class, (short) 0)
            .put(Integer.class, 0)
            .put(Long.class, 0L)
            .put(Float.class, 0F)
            .put(Double.class, 0D)
            .put(boolean.class, false)
            .put(char.class, '\u0000')
            .put(byte.class, (byte) 0)
            .put(short.class, (short) 0)
            .put(int.class, 0)
            .put(long.class, 0L)
            .put(float.class, 0F)
            .put(double.class, 0D)
            .build();
    }

    /**
     * Returns {@link ImmutableCollection} of primitive numeric types
     *
     * @return collection of primitive numeric types {@link ImmutableCollection}
     */
    public static ImmutableCollection<Class<?>> getPrimitiveNumericTypes() {
        return ImmutableSet.<Class<?>>builder()
            .add(char.class)
            .add(byte.class)
            .add(short.class)
            .add(int.class)
            .add(long.class)
            .add(float.class)
            .add(double.class)
            .add(boolean.class)
            .add(void.class)
            .build();
    }

    /**
     * Returns {@link ImmutableBiMap} of primitive types
     *
     * @return collection map of primitive types {@link ImmutableBiMap}
     */
    public static ImmutableBiMap<Class<?>, Class<?>> getPrimitiveTypes() {
        return ImmutableBiMap.<Class<?>, Class<?>>builder()
            .put(boolean.class, Boolean.class)
            .put(char.class, Character.class)
            .put(byte.class, Byte.class)
            .put(short.class, Short.class)
            .put(int.class, Integer.class)
            .put(long.class, Long.class)
            .put(float.class, Float.class)
            .put(double.class, Double.class)
            .build();
    }

    public static <T> Class<T> primitiveTypeOf(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            return clazz;
        }
        return (Class<T>) DEFAULT_PRIMITIVE_TYPES.get(clazz);
    }

    /**
     * Indicates if the given class is primitive type or a primitive wrapper.
     *
     * @param type The type to check
     * @return <code>true</code> if primitive or wrapper, <code>false</code> otherwise.
     */
    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.containsKey(type);
    }

    /**
     * Returns the boxed default value for a primitive or a primitive wrapper.
     *
     * @param primitiveOrWrapperType The type to lookup the default value
     * @return The boxed default values as defined in Java Language Specification,
     * <code>null</code> if the type is neither a primitive nor a wrapper
     */
    public static <T> T defaultValueForPrimitiveOrWrapper(Class<T> primitiveOrWrapperType) {
        return (T) PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.get(primitiveOrWrapperType);
    }

    /**
     * Returns type name {@link String} by input {@link Type} instance
     *
     * @param type - input {@link Type} instance
     * @return ype name {@link String}
     */
    public static String typeNameFor(final Type type) {
        return BASE_TYPE_NAME_LOOKUP.get(type);
    }

    /**
     * Returns binary flag based on input type name {@link String}
     *
     * @param typeName - input type name {@link String}
     * @return true - if type name exists in type collection, false - otherwise
     */
    public static boolean isBaseType(final String typeName) {
        return BASE_TYPE_NAMES.contains(typeName);
    }

    /**
     * Returns binary flag based on input {@link ResolvedType} instance
     *
     * @param type - input {@link ResolvedType} instance
     * @return true - if type name exists in base type collection, false - otherwise
     */
    public static boolean isBaseType(final ResolvedType type) {
        return BASE_TYPE_NAMES.contains(typeNameFor(type.getErasedType()));
    }

    /**
     * Returns binary flag based on input {@link ResolvedType} instance
     *
     * @param returnType input {@link ResolvedType} instance to be returned
     * @return true - if returned type name is {@link Void}, false - otherwise
     */
    public static boolean isVoid(final ResolvedType returnType) {
        return Void.class.equals(returnType.getErasedType()) || Void.TYPE.equals(returnType.getErasedType());
    }

    @UtilityClass
    public static class Maps {

        public static ResolvedType mapValueType(final ResolvedType type) {
            if (Map.class.isAssignableFrom(type.getErasedType())) {
                return mapValueType(type, Map.class);
            }
            return new TypeResolver().resolve(Object.class);
        }

        private static ResolvedType mapValueType(final ResolvedType container, final Class<Map> mapClass) {
            List<ResolvedType> resolvedTypes = container.typeParametersFor(mapClass);
            if (resolvedTypes.size() == 2) {
                return resolvedTypes.get(1);
            }
            return new TypeResolver().resolve(Object.class);
        }

        public static boolean isMapType(final ResolvedType type) {
            return Map.class.isAssignableFrom(type.getErasedType());
        }
    }

    @UtilityClass
    public static class Collections {

        public static ResolvedType collectionElementType(final ResolvedType type) {
            if (List.class.isAssignableFrom(type.getErasedType())) {
                return elementType(type, List.class);
            } else if (Set.class.isAssignableFrom(type.getErasedType())) {
                return elementType(type, Set.class);
            } else if (type.isArray()) {
                return type.getArrayElementType();
            } else if ((Collection.class.isAssignableFrom(type.getErasedType()) && !Maps.isMapType(type))) {
                return elementType(type, Collection.class);
            }
            return null;
        }

        public static boolean isContainerType(final ResolvedType type) {
            return List.class.isAssignableFrom(type.getErasedType()) ||
                Set.class.isAssignableFrom(type.getErasedType()) ||
                (Collection.class.isAssignableFrom(type.getErasedType()) && !Maps.isMapType(type)) ||
                type.isArray();
        }

        public static String containerType(final ResolvedType type) {
            if (List.class.isAssignableFrom(type.getErasedType())) {
                return "List";
            } else if (Set.class.isAssignableFrom(type.getErasedType())) {
                return "Set";
            } else if (type.isArray()) {
                return "Array";
            } else if (Collection.class.isAssignableFrom(type.getErasedType()) && !Maps.isMapType(type)) {
                return "List";
            }
            throw new UnsupportedOperationException(StringUtils.formatMessage("Type is negate collection type %s", type));
        }

        private static <T extends Collection> ResolvedType elementType(final ResolvedType container, final Class<T> collectionType) {
            List<ResolvedType> resolvedTypes = container.typeParametersFor(collectionType);
            if (resolvedTypes.size() == 1) {
                return resolvedTypes.get(0);
            }
            return new TypeResolver().resolve(Object.class);
        }
    }

    @UtilityClass
    public class Annotations {

        /**
         * Finds first annotation of the given type on the given bean property and returns it.
         * Search precedence is getter, setter, field.
         *
         * @param beanPropertyDefinition introspected jackson property definition
         * @param annotationClass        class object representing desired annotation
         * @param <A>                    type that extends Annotation
         * @return first annotation found for property
         */
        public static <A extends Annotation> Optional<A> findPropertyAnnotation(final BeanPropertyDefinition beanPropertyDefinition, final Class<A> annotationClass) {
            return tryGetFieldAnnotation(beanPropertyDefinition, annotationClass)
                .or(() -> tryGetGetterAnnotation(beanPropertyDefinition, annotationClass))
                .or(() -> tryGetSetterAnnotation(beanPropertyDefinition, annotationClass));
        }

        public static boolean memberIsUnwrapped(AnnotatedMember member) {
            if (Objects.isNull(member)) {
                return false;
            }
            return Optional.ofNullable(member.getAnnotation(JsonUnwrapped.class)).isPresent();
        }

        @SuppressWarnings("PMD")
        private static <A extends Annotation> Optional<A> tryGetGetterAnnotation(final BeanPropertyDefinition beanPropertyDefinition, final Class<A> annotationClass) {
            if (beanPropertyDefinition.hasGetter()) {
                return Optional.ofNullable(beanPropertyDefinition.getGetter().getAnnotation(annotationClass));
            }
            return Optional.empty();
        }

        @SuppressWarnings("PMD")
        private static <A extends Annotation> Optional<A> tryGetSetterAnnotation(final BeanPropertyDefinition beanPropertyDefinition, final Class<A> annotationClass) {
            if (beanPropertyDefinition.hasSetter()) {
                return Optional.ofNullable(beanPropertyDefinition.getSetter().getAnnotation(annotationClass));
            }
            return Optional.empty();
        }

        @SuppressWarnings("PMD")
        private static <A extends Annotation> Optional<A> tryGetFieldAnnotation(final BeanPropertyDefinition beanPropertyDefinition, final Class<A> annotationClass) {
            if (beanPropertyDefinition.hasField()) {
                return Optional.ofNullable(beanPropertyDefinition.getField().getAnnotation(annotationClass));
            }
            return Optional.empty();
        }

        public static String memberName(final AnnotatedMember member) {
            if (Objects.isNull(member) || Objects.isNull(member.getMember())) {
                return EMPTY;
            }
            return member.getMember().getName();
        }
    }
}
