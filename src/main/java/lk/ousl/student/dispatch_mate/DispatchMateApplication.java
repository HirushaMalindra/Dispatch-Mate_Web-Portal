package lk.ousl.student.dispatch_mate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
// This ONE annotation tells Spring Boot to:
//   1. Scan all classes in this package (and sub-packages) for @Component, @Service, etc.
//   2. Auto-configure JPA, Thymeleaf, web MVC, etc. based on what's in the pom.xml
//   3. Start an embedded Tomcat server

@SpringBootApplication
public class DispatchMateApplication {

    public static void main(String[] args) {
        // This launches the entire application — Tomcat starts on port 8080
        SpringApplication.run(DispatchMateApplication.class, args);
    }
}
