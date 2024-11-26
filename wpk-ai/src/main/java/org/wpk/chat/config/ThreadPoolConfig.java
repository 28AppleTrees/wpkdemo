package org.wpk.chat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    private final ThreadPoolExecutorProperties threadPoolExecutorProperties;
    @Autowired
    public ThreadPoolConfig(ThreadPoolExecutorProperties threadPoolExecutorProperties) {
        this.threadPoolExecutorProperties = threadPoolExecutorProperties;
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        Integer core = threadPoolExecutorProperties.getCore();
        Integer max = threadPoolExecutorProperties.getMax();
        Long keepAlive = threadPoolExecutorProperties.getKeepAlive();
        return new ThreadPoolExecutor(core, max, keepAlive, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
    }

}
