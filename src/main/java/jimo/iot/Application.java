package jimo.iot;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling //定时器注解
@MapperScans({
        @MapperScan("jimo.iot.ctrl_module.mapper"),
        @MapperScan("jimo.iot.device_value.mapper")
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public RestTemplate RestTemplate(){
        return new RestTemplate();
    }

}
