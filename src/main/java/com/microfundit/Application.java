package com.microfundit;

import com.microfundit.dao.UserRepository;
import com.microfundit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Random;

import static com.microfundit.listener.UserEventHandler.bCryptPasswordEncoder;

/**
 * Created by Kevin Kimaru Chege on 3/21/2018.
 */
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
@Import(SpringDataRestConfiguration.class)
public class Application implements CommandLineRunner {

    @Autowired
    UserRepository users;

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public TaskScheduler scheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        CorsConfiguration corsConfig = new CorsConfiguration();
        if (corsConfig.getAllowedOrigins() == null) {
            corsConfig.addAllowedOrigin("*");
        }
        if (corsConfig.getAllowedMethods() == null) {
            corsConfig.setAllowedMethods(Arrays.asList(HttpMethod.DELETE.name(), HttpMethod.PUT.name(),
                    HttpMethod.PATCH.name(), HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name()));
            corsConfig.getAllowedMethods().removeIf(m -> m.contentEquals(HttpMethod.OPTIONS.name()));
        }
        if (corsConfig.getAllowedHeaders() == null) {
            corsConfig.addAllowedHeader("*");
        }
        if (corsConfig.getExposedHeaders() == null) {
            corsConfig.addExposedHeader("Authorization");
            corsConfig.addExposedHeader("role");
            corsConfig.addExposedHeader("username");
            corsConfig.addExposedHeader("user_ID");
        }
        if (corsConfig.getAllowCredentials() == null) {
            corsConfig.setAllowCredentials(true);
        }
        if (corsConfig.getMaxAge() == null) {
            corsConfig.setMaxAge(1800L);
        }
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Override
    public void run(String... args) throws Exception {
        User user = users.findByUsername("admin");
        if(user == null) {
            User u = new User("admin", "pass", "ROLE_ADMIN");
            u.setPassword(bCryptPasswordEncoder.encode(u.getPassword()));
            users.save(u);
        }
        User u2 = users.findByUsername("admin");
        if(u2 != null) {
            System.out.println("+====================\n==================\n==================");
            System.out.println(u2.getPassword());
            System.out.println(u2.getUsername());
            System.out.println(u2.getRole());
        }

    }
}
