package org.wpk.netty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wpk.netty")
@Data
public class NettyServerProperties {

    private Integer port;
    private Integer bossThreads;
    private Integer workerThreads;
    private String webSocketPath;

    private Long readerIdleTime;
    private Long writerIdleTime;
    private Long allIdleTime;

    private String name;

}
