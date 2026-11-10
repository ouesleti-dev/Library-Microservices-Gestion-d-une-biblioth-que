package com.library.book.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "book.exchange";
    public static final String STATISTICS_QUEUE = "statistics.book.queue";
    public static final String ROUTING_PATTERN = "book.*";

    @Bean
    public TopicExchange bookExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue statisticsQueue() {
        return new Queue(STATISTICS_QUEUE, true);   // durable : survit à un redémarrage
    }

    @Bean
    public Binding statisticsBinding(Queue statisticsQueue, TopicExchange bookExchange) {
        return BindingBuilder.bind(statisticsQueue).to(bookExchange).with(ROUTING_PATTERN);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}