package com.wildbeeslabs.sensiblemetrics.diffy.generator.utils;

import com.wildbeeslabs.sensiblemetrics.diffy.generator.exception.ReflectionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.security.PrivilegedAction;
import java.util.*;

import static java.lang.reflect.Modifier.isAbstract;
import static java.security.AccessController.doPrivileged;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public final class Reflection {
    private static final Map<Class<?>, Class<?>> PRIMITIVES = new HashMap<>(16);

    static {
        PRIMITIVES.put(Boolean.TYPE, Boolean.class);
        PRIMITIVES.put(Byte.TYPE, Byte.class);
        PRIMITIVES.put(Character.TYPE, Character.class);
        PRIMITIVES.put(Double.TYPE, Double.class);
        PRIMITIVES.put(Float.TYPE, Float.class);
        PRIMITIVES.put(Integer.TYPE, Integer.class);
        PRIMITIVES.put(Long.TYPE, Long.class);
        PRIMITIVES.put(Short.TYPE, Short.class);
    }

    private Reflection() {
        throw new UnsupportedOperationException();
    }

    public static Class<?> maybeWrap(Class<?> clazz) {
        Class<?> wrapped = PRIMITIVES.get(clazz);
        return wrapped == null ? clazz : wrapped;
    }

    public static <T> Constructor<T> findConstructor(Class<T> type, Class<?>... parameterTypes) {
        try {
            return type.getConstructor(parameterTypes);
        } catch (Exception ex) {
            throw reflectionException(ex);
        }
    }

    public static <T> Constructor<T> findDeclaredConstructor(Class<T> type, Class<?>... parameterTypes) {
        try {
            Constructor<T> ctor = type.getDeclaredConstructor(parameterTypes);
            ctor.setAccessible(true);
            return ctor;
        } catch (Exception ex) {
            throw reflectionException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> singleAccessibleConstructor(Class<T> type) {
        Constructor<?>[] constructors = type.getConstructors();
        if (constructors.length != 1)
            throw new ReflectionException(type + " needs a single accessible constructor");

        return (Constructor<T>) constructors[0];
    }

    public static <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            throw reflectionException(ex);
        }
    }

    public static <T> T instantiate(Constructor<T> ctor, Object... args) {
        try {
            return ctor.newInstance(args);
        } catch (Exception ex) {
            throw reflectionException(ex);
        }
    }

//    public static Set<Type<?>> supertypes(Type<?> bottom) {
//        Set<Type<?>> supertypes = new HashSet<>();
//        supertypes.add(bottom);
//        supertypes.addAll(bottom.getAllTypesAssignableFromThis());
//        return supertypes;
//    }

    public static Object defaultValueOf(Class<? extends Annotation> annotationType, String attribute) {
        try {
            return annotationType.getMethod(attribute).getDefaultValue();
        } catch (Exception ex) {
            throw reflectionException(ex);
        }
    }

    public static List<Annotation> allAnnotations(AnnotatedElement e) {
        List<Annotation> thisAnnotations = nonSystemAnnotations(e);

        List<Annotation> annotations = new ArrayList<>();
        for (Annotation each : thisAnnotations) {
            annotations.add(each);
            annotations.addAll(allAnnotations(each.annotationType()));
        }

        return annotations;
    }

    public static <T extends Annotation> List<T> allAnnotationsByType(AnnotatedElement e, Class<T> type) {
        List<T> annotations = new ArrayList<>();
        Collections.addAll(annotations, e.getAnnotationsByType(type));

        List<Annotation> thisAnnotations = nonSystemAnnotations(e);

        for (Annotation each : thisAnnotations)
            annotations.addAll(allAnnotationsByType(each.annotationType(), type));

        return annotations;
    }

    public static Method findMethod(Class<?> target, String methodName, Class<?>... argTypes) {
        try {
            return target.getMethod(methodName, argTypes);
        } catch (Exception ex) {
            throw reflectionException(ex);
        }
    }

    public static Object invoke(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            throw reflectionException(ex);
        }
    }

    public static Field findField(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw reflectionException(ex);
        }
    }

    public static List<Field> allDeclaredFieldsOf(Class<?> type) {
        List<Field> allFields = new ArrayList<>();

        for (Class<?> c = type; c != null; c = c.getSuperclass())
            Collections.addAll(allFields, c.getDeclaredFields());

        List<Field> results =
            allFields.stream()
                .filter(f -> !f.isSynthetic())
                .collect(toList());
        results.forEach(f -> f.setAccessible(true));

        return results;
    }

    public static void setField(
        Field field,
        Object target,
        Object value,
        boolean suppressProtection) {

        doPrivileged((PrivilegedAction<Void>) () -> {
            field.setAccessible(suppressProtection);
            return null;
        });

        try {
            field.set(target, value);
        } catch (Exception ex) {
            throw reflectionException(ex);
        }
    }

    public static Method singleAbstractMethodOf(Class<?> rawClass) {
        if (!rawClass.isInterface())
            return null;

        int abstractCount = 0;
        Method singleAbstractMethod = null;
        for (Method each : rawClass.getMethods()) {
            if (isAbstract(each.getModifiers()) && !overridesJavaLangObjectMethod(each)) {
                singleAbstractMethod = each;
                ++abstractCount;
            }
        }

        return abstractCount == 1 ? singleAbstractMethod : null;
    }

    private static boolean overridesJavaLangObjectMethod(Method method) {
        return isEquals(method) || isHashCode(method) || isToString(method);
    }

    private static boolean isEquals(Method method) {
        return "equals".equals(method.getName())
            && method.getParameterTypes().length == 1
            && Object.class.equals(method.getParameterTypes()[0]);
    }

    private static boolean isHashCode(Method method) {
        return "hashCode".equals(method.getName())
            && method.getParameterTypes().length == 0;
    }

    private static boolean isToString(Method method) {
        return "toString".equals(method.getName())
            && method.getParameterTypes().length == 0;
    }

    public static RuntimeException reflectionException(Exception ex) {
        if (ex instanceof InvocationTargetException)
            return new ReflectionException(((InvocationTargetException) ex).getTargetException());
        if (ex instanceof RuntimeException)
            return (RuntimeException) ex;

        return new ReflectionException(ex);
    }

    private static List<Annotation> nonSystemAnnotations(AnnotatedElement e) {
        return stream(e.getAnnotations())
            .filter(a -> !a.annotationType().getName().startsWith("java.lang.annotation."))
            .filter(a -> !a.annotationType().getName().startsWith("kotlin."))
            .collect(toList());
    }

    public static List<AnnotatedType> annotatedComponentTypes(AnnotatedType annotatedType) {
        if (annotatedType instanceof AnnotatedParameterizedType)
            return asList(((AnnotatedParameterizedType) annotatedType).getAnnotatedActualTypeArguments());

        if (annotatedType instanceof AnnotatedArrayType)
            return singletonList(((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType());

        if (annotatedType instanceof AnnotatedWildcardType) {
            AnnotatedWildcardType wildcard = (AnnotatedWildcardType) annotatedType;
            if (wildcard.getAnnotatedLowerBounds().length > 0)
                return singletonList(wildcard.getAnnotatedLowerBounds()[0]);

            return asList(wildcard.getAnnotatedUpperBounds());
        }

        return emptyList();
    }
}
