package com.mentoree.config;

import com.mentoree.config.filter.TemporalLoggingFilter;
import com.mentoree.config.interceptors.AuthorityInterceptor;
import com.mentoree.config.utils.files.aws.AwsS3FileUpload;
import com.mentoree.config.utils.files.FileUtils;
import com.mentoree.config.utils.files.FileUtilsImpl;
import com.mentoree.domain.repository.BoardRepository;
import com.mentoree.domain.repository.MenteeRepository;
import com.mentoree.domain.repository.MentorRepository;
import com.mentoree.domain.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${server.origins.address}")
    private String originAddr;
    private final int FRONT_PORT = 8081;

    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final BoardRepository boardRepository;
    private final MissionRepository missionRepository;
    private final Environment environment;
    private final AwsS3FileUpload awsS3FileUpload;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorityInterceptor(mentorRepository, menteeRepository, missionRepository, boardRepository))
                .order(Integer.MIN_VALUE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**")
                .addResourceLocations("classpath:/static/images");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(originAddr + ":" + FRONT_PORT)
                .allowedMethods("POST", "GET", "DELETE", "PUT");
    }

    @Bean
    public FileUtils fileUtils() {
        if(Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> env.equalsIgnoreCase("real")))
            return awsS3FileUpload;
        return new FileUtilsImpl();
    }

    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new TemporalLoggingFilter());
        filter.addUrlPatterns("/api/**");
        return filter;
    }

}
