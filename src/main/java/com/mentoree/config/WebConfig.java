package com.mentoree.config;

import com.amazonaws.services.s3.AmazonS3;
import com.mentoree.config.interceptors.AuthorityInterceptor;
import com.mentoree.config.utils.AwsS3FileUpload;
import com.mentoree.config.utils.FileUtils;
import com.mentoree.config.utils.FileUtilsImpl;
import com.mentoree.domain.repository.BoardRepository;
import com.mentoree.domain.repository.MenteeRepository;
import com.mentoree.domain.repository.MentorRepository;
import com.mentoree.domain.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final BoardRepository boardRepository;
    private final MissionRepository missionRepository;
    private final AmazonS3 amazonS3;
    private final Environment environment;

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
                .allowedOrigins("http://localhost:80")
                .allowedMethods("POST", "GET", "DELETE", "PUT");
    }

    @Bean
    public FileUtils fileUtils() {
        if(Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> env.equalsIgnoreCase("real")))
            return new AwsS3FileUpload(amazonS3);
        return new FileUtilsImpl();
    }

}
