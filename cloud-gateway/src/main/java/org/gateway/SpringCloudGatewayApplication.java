package org.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * SpringCloud-gateway默认是基于webflux, 没有servlet
 * gateway启动类不能和同项目其它目录相同, 不然springboot会自动扫描, 默认扫描启动类和下级, 其它服务使用了servlet模式
 * 导致报错 ClassNotFoundException: javax.servlet.http.HttpServletRequest
 *
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudGatewayApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringCloudGatewayApplication.class, args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = environment.getProperty("server.port");
        String path = environment.getProperty("server.servlet.context-path");
        log.info("\n----------------------------------------------------------\n\t" +
                "Application Spring-Boot is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
                "Swagger文档: \thttp://" + ip + ":" + port + path + "/doc.html\n" +
                "----------------------------------------------------------");

    }
}
