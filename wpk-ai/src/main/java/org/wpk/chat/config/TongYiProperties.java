package org.wpk.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里通义千问配置项
 */
@Component
@ConfigurationProperties(prefix = "ai.tongyiqianwen")
public class TongYiProperties {
    private String apikey;
    private String url;
}
