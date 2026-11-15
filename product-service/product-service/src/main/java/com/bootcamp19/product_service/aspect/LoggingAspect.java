package com.bootcamp19.product_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.bootcamp19.product_service.service..*.*(..))")
    public Object logExecutionDuration(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        String argsString = Arrays.toString(joinPoint.getArgs());
        if (argsString.toLowerCase().contains("password")) {
            argsString = "[PROTECTED SENSITIVE DATA]";
        }

        log.info("[AOP] >> Entering method: {} with arguments: {}", methodName, argsString);

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - startTime;
        log.info("[AOP] << Exiting method: {} executed in {} ms", methodName, duration);

        return result;
    }

    @AfterThrowing(pointcut = "execution(* com.bootcamp19.product_service.service..*.*(..))", throwing = "exception")
    public void logServiceException(JoinPoint joinPoint, Exception exception) {
        log.error("[AOP EXCEPTION] Error in {}: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }
}
