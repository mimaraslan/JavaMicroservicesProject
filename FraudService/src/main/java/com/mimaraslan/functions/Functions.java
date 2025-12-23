
package com.mimaraslan.functions;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class Functions {

    @Bean
    public Function<String, String> uppercase() {
        return v -> v.toUpperCase();
    }

    @Bean
    public Function<String, String> reverse() {
        return v -> new StringBuilder(v).reverse().toString();
    }

    @Bean
    public Function<String, String> processMessage() {
        return msg -> "PROCESSED:" + msg;
    }
}
