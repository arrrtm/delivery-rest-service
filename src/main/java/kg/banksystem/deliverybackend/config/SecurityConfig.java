package kg.banksystem.deliverybackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtConfigurer;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String LOGOUT_ENDPOINT = "/api/auth/logout";
    private static final String RESET_PASSWORD = "/api/account/password/reset";
    private static final String ADMIN_ENDPOINT = "/api/admin/**";
    private static final String COURIER_ENDPOINT = "/api/courier/**";
    private static final String BANK_ENDPOINT = "/api/bank/**";
    private static final String BRANCH_ENDPOINT = "/api/branch/**";
    private static final String CONTROL_ENDPOINT = "/api/control/**";
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint())
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT, LOGOUT_ENDPOINT, RESET_PASSWORD).permitAll()
                .antMatchers(ADMIN_ENDPOINT).hasAuthority("ADMIN")
                .antMatchers(COURIER_ENDPOINT).hasAuthority("COURIER")
                .antMatchers(BANK_ENDPOINT).hasAuthority("BANK_EMPLOYEE")
                .antMatchers(BRANCH_ENDPOINT).hasAuthority("BRANCH_EMPLOYEE")
                .antMatchers(CONTROL_ENDPOINT).hasAnyAuthority("ADMIN", "BANK_EMPLOYEE")
                .anyRequest()
                .authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (httpServletRequest, httpServletResponse, e) -> {
            Map<String, Object> errorObject = new HashMap<>();
            int errorCode = 401;
            errorObject.put("message", "Unauthorized access of protected resource, invalid credentials.");
            errorObject.put("status", RestStatus.ERROR);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.setStatus(errorCode);
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorObject));
            log.error("Unauthorized access of protected resource, invalid credentials. Code - " + errorCode);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            Map<String, Object> errorObject = new HashMap<>();
            int errorCode = 403;
            errorObject.put("message", "Access to the resource is denied with the specified role, forbidden.");
            errorObject.put("status", RestStatus.ERROR);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.setStatus(errorCode);
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorObject));
            log.error("Access to the resource is denied with the specified role, forbidden. Code - " + errorCode);
        };
    }
}