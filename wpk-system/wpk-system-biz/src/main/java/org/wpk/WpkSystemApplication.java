package org.wpk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication(scanBasePackages = "org.wpk")
public class WpkSystemApplication {


    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(WpkSystemApplication.class, args);
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
//        log.info("spring.application.name-->{}", environment.getProperty("spring.application.name"));
//        while (true) {
//            log.info("----------------------------------------------------------\t" +
//                    "Application Spring-Boot is running! Access URLs:\t" +
//                    "Local: \t\thttp://localhost:" + "port" + "path" + "/\t" +
//                    "External: \thttp://" + "ip" + ":" + "port" + "path" + "/\t" +
//                    "Swagger文档: \thttp://" + "ip" + ":" + "port" + "path" + "/doc.html" +
//                    "----------------------------------------------------------");
//            Thread.sleep(10);
//        }
    }

}
