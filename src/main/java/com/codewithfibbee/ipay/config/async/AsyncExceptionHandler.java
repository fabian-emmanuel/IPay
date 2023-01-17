package com.codewithfibbee.ipay.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;


@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
                log.info("== This exception occurred in an async method ==");

                log.error("Async Exception :", throwable);
                log.error("Async Method: {}", method);
                log.error("Async method params :".concat(Arrays.toString(objects)));

                log.info("== End of async method exception ==");
        }
}
