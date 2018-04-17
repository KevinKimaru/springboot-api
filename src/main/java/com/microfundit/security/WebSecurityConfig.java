package com.microfundit.security;

import com.microfundit.dao.UserRepository;
import com.microfundit.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration()
@EnableWebSecurity()
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository users;

    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable().authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/v2/api-docs",           // swagger
                        "/webjars/**",            // swagger-ui webjars
                        "/swagger-resources/**",  // swagger-ui resources
                        "/configuration/**",      // swagger configuration
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/files/**",                //Access files dir which contains stories images, videos and other files
                        "/stories/**"              //All stories
                ).permitAll()
                .antMatchers(HttpMethod.POST, "/login", "/donors").permitAll()
                .antMatchers(HttpMethod.POST, "/donate").hasRole("USER")

                .antMatchers(HttpMethod.POST, "/brands/**", "/**/brands", "/**/brand",
                        "/fundings/**", "/**/fundings", "/**/funding",
                        "/organisations/**", "/**/organisations", "/**/organisation",
                        "/pointsCompanies/**", "/**/pointsCompanies", "/**/pointsCompany",
                        "/stories/**", "/**/stories", "/**/story",
                        "/users/**", "/**/users", "/**/user").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/transactions/**", "/**/transactions", "/**/transaction",
                        "/donations/**", "/**/donations", "/**/donation",
                        "/subscriptions/**", "/**/subscriptions", "/**/subscription",
                        "/transactions/**", "/**/transactions", "/**/transaction").hasRole("SUPER_ADMIN")

                .antMatchers(HttpMethod.PATCH, "/brands/**", "/**/brands", "/**/brand",
                        "/fundings/**", "/**/fundings", "/**/funding",
                        "/organisations/**", "/**/organisations", "/**/organisation",
                        "/pointsCompanies/**", "/**/pointsCompanies", "/**/pointsCompany",
                        "/stories/**", "/**/stories", "/**/story",
                        "/users/**", "/**/users", "/**/user").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/transactions/**", "/**/transactions", "/**/transaction",
                        "/donations/**", "/**/donations", "/**/donation",
                        "/subscriptions/**", "/**/subscriptions", "/**/subscription",
                        "/transactions/**", "/**/transactions", "/**/transaction").hasRole("SUPER_ADMIN")

                .antMatchers(HttpMethod.PUT, "/brands/**", "/**/brands", "/**/brand",
                        "/fundings/**", "/**/fundings", "/**/funding",
                        "/organisations/**", "/**/organisations", "/**/organisation",
                        "/pointsCompanies/**", "/**/pointsCompanies", "/**/pointsCompany",
                        "/stories/**", "/**/stories", "/**/story",
                        "/users/**", "/**/users", "/**/user").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/transactions/**", "/**/transactions", "/**/transaction",
                        "/donations/**", "/**/donations", "/**/donation",
                        "/subscriptions/**", "/**/subscriptions", "/**/subscription",
                        "/transactions/**", "/**/transactions", "/**/transaction").hasRole("SUPER_ADMIN")

                .antMatchers(HttpMethod.DELETE, "/brands/**", "/**/brands", "/**/brand",
                        "/fundings/**", "/**/fundings", "/**/funding",
                        "/organisations/**", "/**/organisations", "/**/organisation",
                        "/pointsCompanies/**", "/**/pointsCompanies", "/**/pointsCompany",
                        "/stories/**", "/**/stories", "/**/story",
                        "/users/**", "/**/users", "/**/user").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/transactions/**", "/**/transactions", "/**/transaction",
                        "/donations/**", "/**/donations", "/**/donation",
                        "/subscriptions/**", "/**/subscriptions", "/**/subscription",
                        "/transactions/**", "/**/transactions", "/**/transaction").hasRole("SUPER_ADMIN")

                .antMatchers(HttpMethod.GET,
                        "/fundings/**", "/**/fundings", "/**/funding",
                        "/users/**", "/**/users", "/**/user",
                        "/donations/**",
                        "/subscriptions/**",
                        "/transactions/**", "/**/transactions/**", "/**/transaction/**").hasRole("ADMIN")

                .antMatchers(HttpMethod.GET, "/**/brands", "/**/brand", "/brands/**",
                        "/organisations/**", "/**/organisations", "/**/organisation",
                        "/pointsCompanies/**", "/**/pointsCompanies", "/**/pointsCompany",
                        "/stories/**", "/**/stories", "/**/story").permitAll()

//                .antMatchers(HttpMethod.GET, "/donations/search/findByDonor").hasRole("USER")
                .antMatchers(HttpMethod.OPTIONS, "/**").hasRole("SUPER_ADMIN")

                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(users, authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService))
//                 this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
//        auth.inMemoryAuthentication()
//                .withUser("admin")
//                .password("password")
//                .roles("ADMIN");
    }

    @Bean
    public EvaluationContextExtension securityExtension() {
        return new EvaluationContextExtensionSupport() {
            @Override
            public String getExtensionId() {
                return "security";
            }

            @Override
            public Object getRootObject() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                return new SecurityExpressionRoot(authentication) {
                };
            }
        };
    }

}