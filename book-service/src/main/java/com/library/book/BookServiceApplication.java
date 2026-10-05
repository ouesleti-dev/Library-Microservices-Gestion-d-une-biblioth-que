package com.library.book;

import com.library.book.model.Book;
import com.library.book.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class BookServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }

    /** Quelques livres de demonstration au demarrage. */
    @Bean
    CommandLineRunner initData(BookRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Book("Les Miserables", "Victor Hugo", "Roman", 1862, true));
                repository.save(new Book("Le Petit Prince", "Antoine de Saint-Exupery", "Conte", 1943, true));
                repository.save(new Book("Clean Code", "Robert C. Martin", "Informatique", 2008, true));
            }
        };
    }
}
