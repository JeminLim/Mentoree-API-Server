package com.mentoree.config;

import com.mentoree.config.interceptors.AuthorityInterceptor;
import com.mentoree.config.utils.FileUtils;
import com.mentoree.config.utils.FileUtilsImpl;
import com.mentoree.domain.repository.BoardRepository;
import com.mentoree.domain.repository.MenteeRepository;
import com.mentoree.domain.repository.MentorRepository;
import com.mentoree.domain.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final BoardRepository boardRepository;
    private final MissionRepository missionRepository;

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
                .allowedOrigins("http://localhost:8081")
                .allowedMethods("POST", "GET", "DELETE", "PUT");
    }

    @Bean
    public FileUtils fileUtils() {
        return new FileUtilsImpl();
    }

}
