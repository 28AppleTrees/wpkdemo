package org.wpk.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里通义千问配置项
 */
@Component
@ConfigurationProperties(prefix = "ai.tongyiqianwen.threadpool")
@Data
public class ThreadPoolExecutorProperties {

    private Integer core;
    private Integer max;
    private Long keepAlive;

}
