package com.ewallet.auth_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "ewallet.exchange";

    // Queues
    public static final String USER_QUEUE   = "user.registered.queue";
    public static final String WALLET_QUEUE = "wallet.registered.queue";

    // Routing Keys
    public static final String USER_ROUTING_KEY   = "user.registered";
    public static final String WALLET_ROUTING_KEY = "wallet.registered";

    @Bean
    public DirectExchange ewalletExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(USER_QUEUE).build();
    }

    @Bean
    public Queue walletQueue() {
        return QueueBuilder.durable(WALLET_QUEUE).build();
    }

    @Bean
    public Binding userBinding(Queue userQueue, DirectExchange ewalletExchange) {
        return BindingBuilder
                .bind(userQueue)
                .to(ewalletExchange)
                .with(USER_ROUTING_KEY);
    }

    @Bean
    public Binding walletBinding(Queue walletQueue, DirectExchange ewalletExchange) {
        return BindingBuilder
                .bind(walletQueue)
                .to(ewalletExchange)
                .with(WALLET_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}