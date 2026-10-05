package com.library.member;

import com.library.member.model.Member;
import com.library.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class MemberServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberServiceApplication.class, args);
    }

    /** Quelques membres de demonstration au demarrage. */
    @Bean
    CommandLineRunner initData(MemberRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Member("Amine", "Ben Salah", "amine.bensalah@example.com", "20123456"));
                repository.save(new Member("Sarra", "Trabelsi", "sarra.trabelsi@example.com", "98765432"));
            }
        };
    }
}
