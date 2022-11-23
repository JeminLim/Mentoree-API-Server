package com.mentoree.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JPAConfig {

    // 단위 테스트에서 @EnableJpaAuditing 때문에 따로 분리
    // 단위 테스트 실행 시, MentoreeApplication 에 @EnableJpaAuditing 어노테이션이 있을 경우 JPA 빈을 로드하기 때문에
    // 따로 분리해서 해당 설정을 읽지 않도록 하기 위함

}
