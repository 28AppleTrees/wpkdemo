package org.wpk.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping
public class Controller {
    @Value("${spring.application.name}")
    private String serverName;

    @GetMapping("/test")
    public Object test(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return serverName;
    }
}
