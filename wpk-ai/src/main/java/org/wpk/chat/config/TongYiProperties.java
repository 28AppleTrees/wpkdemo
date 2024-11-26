package org.wpk.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里通义千问配置项
 */
@Component
@ConfigurationProperties(prefix = "ai.tongyiqianwen")
@Data
public class TongYiProperties {
    private String apikey;
    private String url;
}
