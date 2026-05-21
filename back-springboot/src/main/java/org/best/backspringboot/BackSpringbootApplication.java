package org.best.backspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class BackSpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackSpringbootApplication.class, args);
    }
}
