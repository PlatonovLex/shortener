package ru.platonov.shortener.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * ConcurrentOperationFailureInterceptor.
 * <p>
 *      Advice that traps exceptions out of annotated calls and retries the call if appropriate.
 * </p>
 *
 * @author Platonov Alexey
 * @since 17.08.2017
 */
@Order(0)
@Configuration
@Aspect
class ConcurrentOperationFailureInterceptor implements Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentOperationFailureInterceptor.class);

    private static final int DEFAULT_MAX_RETRIES = 2;

    private int maxRetries = DEFAULT_MAX_RETRIES;

    private int order = 1;

    /**
     * Advice that traps an exception specified by an annotation so that the operation can be retried.
     *
     * @param pjp                      wrapper around method being executed
     * @param retryConcurrentOperation annotation indicating method should be wrapped
     * @return return value of wrapped call
     * @throws Exception if retries exceed maximum, rethrows exception configured in RetryConcurrentOperation annotation
     * @throws Throwable any other things the wrapped call throws will pass through
     */
    @Around("@annotation(retryConcurrentOperation)")
    public Object performOperation(
            ProceedingJoinPoint pjp, RetryConcurrentOperation retryConcurrentOperation) throws Throwable {
        Class<?> exceptionClass = retryConcurrentOperation.exception();
        int retries = retryConcurrentOperation.retries();
        if (!(retries > 0)) {
            retries = this.maxRetries;
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Attempting operation with potential for {} with maximum {} retries",
                    exceptionClass.getCanonicalName(), retries);
        }

        int numAttempts = 0;
        do {
            numAttempts++;
            try {
                return pjp.proceed();
            } catch (Throwable ex) {
                // if the exception is not what we're looking for, pass it through
                if (!exceptionClass.isInstance(ex)) {
                    throw ex;
                }

                // we caught the configured exception, retry unless we've reached the maximum
                if (numAttempts > retries) {
                    LOG.warn("Caught {} and exceeded maximum retries ({}), rethrowing.",
                            exceptionClass.getCanonicalName(), retries);
                    throw ex;
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("Caught {} and will retry, attempts: {}", exceptionClass.getCanonicalName(), numAttempts);
                }
            }
        } while (numAttempts <= retries);
        // this will never execute - we will have either succesfully returned or rethrown an exception
        throw new IllegalStateException("Unreachable statement reached!");
    }

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Allow overriding of the default order.
     *
     * @param order aspect order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Allow overriding of the default maximum number of retries.
     *
     * @param maxRetries maximum number of retries
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}