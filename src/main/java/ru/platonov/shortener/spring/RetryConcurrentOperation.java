package ru.platonov.shortener.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RetryConcurrentOperation.
 * <p>
 *     Annotation that indicates an operation should be retried if the specified exception is encountered.
 * </p>
 *
 * @author Platonov Alexey
 * @since 17.08.2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RetryConcurrentOperation {

    /**
     * Specify exception for which operation should be retried.
     */
    Class<?> exception() default Exception.class;

    /**
     * Sets the number of times to retry the operation.
     * The default of -1 indicates we want to use whatever the global default is.
     */
    int retries() default -1;
}