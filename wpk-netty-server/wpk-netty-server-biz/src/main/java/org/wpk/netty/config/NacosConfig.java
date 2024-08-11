package org.wpk.netty.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 *
 */
@Configuration
public class NacosConfig {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    /**
     * 将nacos的NamingService创建, 注入spring
     * @return
     * @throws NacosException
     */
    @Bean
    public NamingService namingService() throws NacosException {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosDiscoveryProperties.getServerAddr());
        properties.setProperty(PropertyKeyConst.NAMESPACE, nacosDiscoveryProperties.getNamespace());
        NamingService namingService = NamingFactory.createNamingService(properties);
        return namingService;
    }
}
