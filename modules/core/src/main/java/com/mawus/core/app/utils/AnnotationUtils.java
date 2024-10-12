package com.mawus.core.app.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AnnotationUtils {

    public static <A extends Annotation> A findTypeAnnotation(final Class<?> type,
                                                              final Class<A> targetAnnotationClass) {
        Objects.requireNonNull(type, "incoming 'type' is not valid");
        Objects.requireNonNull(targetAnnotationClass, "incoming 'targetAnnotationClass' is not valid");

        if (type.equals(Object.class))
            return null;
        //check annotation presence on class itself and its annotations if any
        final A annotation = findAnnotation(type, targetAnnotationClass);
        if (annotation != null)
            return annotation;
        //recursive call to check superclass if present
        final Class<?> superClass = type.getSuperclass();
        if (superClass != null)
            return findTypeAnnotation(superClass, targetAnnotationClass);
        return null;
    }

    public static <A extends Annotation> A findAnnotation(final AnnotatedElement source,
                                                          final Class<A> targetAnnotationClass) {
        Objects.requireNonNull(source, "incoming 'source' is not valid");
        Objects.requireNonNull(targetAnnotationClass, "incoming 'targetAnnotationClass' is not valid");

        final A result = source.getAnnotation(targetAnnotationClass);
        if (result != null)
            return result;

        return findAnnotationInAnnotations(source, targetAnnotationClass);
    }

    public static <A extends Annotation> A findAnnotation(final Annotation source,
                                                          final Class<A> targetAnnotationClass) {
        Objects.requireNonNull(source, "incoming 'source' is not valid");
        Objects.requireNonNull(targetAnnotationClass, "incoming 'targetAnnotationClass' is not valid");
        return findAnnotation(source, targetAnnotationClass, new HashSet<Class<? extends Annotation>>());
    }

    private static <A extends Annotation> A findAnnotation(final Annotation sourceAnnotation,
                                                           final Class<A> targetAnnotationClass,
                                                           final Set<Class<? extends Annotation>> alreadyCheckedAnnotations) {

        final Class<? extends Annotation> currentAnnotationType = sourceAnnotation.annotationType();
        alreadyCheckedAnnotations.add(currentAnnotationType);

        final A result = currentAnnotationType.getAnnotation(targetAnnotationClass);
        if (result != null)
            return result;

        final Annotation[] allAnnotations = currentAnnotationType.getAnnotations();
        for (final Annotation annotation : allAnnotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (alreadyCheckedAnnotations.contains(annotationType))
                continue;
            final A found = findAnnotation(annotation, targetAnnotationClass, alreadyCheckedAnnotations);
            if (found != null)
                return found;
        }

        return null;
    }

    private static <A extends Annotation> A findAnnotationInAnnotations(final AnnotatedElement source,
                                                                        final Class<A> targetAnnotationClass) {
        final Annotation[] allAnnotations = source.getAnnotations();
        for (final Annotation annotation : allAnnotations) {
            final A result = findAnnotation(annotation, targetAnnotationClass);
            if (result != null)
                return result;

        }
        return null;
    }
}
