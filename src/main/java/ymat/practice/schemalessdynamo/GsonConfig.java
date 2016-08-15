package ymat.practice.schemalessdynamo;

import com.google.gson.Gson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        Gson gson = new Gson();
        return gson;
    }

}
