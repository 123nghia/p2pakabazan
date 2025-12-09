package com.akabazan.websocket.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange websocketExchange(
            @Value("${app.rabbitmq.websocket.exchange:websocket.events.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue chatQueue(
            @Value("${app.rabbitmq.websocket.chat.queue:websocket.chat.messages.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Queue tradeQueue(
            @Value("${app.rabbitmq.websocket.trade.queue:websocket.trade.events.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Queue notificationQueue(
            @Value("${app.rabbitmq.websocket.notification.queue:websocket.notifications.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding chatMessageBinding(@Qualifier("chatQueue") Queue chatQueue,
                                      TopicExchange websocketExchange,
                                      @Value("${app.rabbitmq.websocket.chat.routing-key:chat.message}") String routingKey) {
        return BindingBuilder.bind(chatQueue).to(websocketExchange).with(routingKey);
    }

    @Bean
    public Binding chatSystemBinding(@Qualifier("chatQueue") Queue chatQueue,
                                     TopicExchange websocketExchange,
                                     @Value("${app.rabbitmq.websocket.chat.system-routing-key:chat.system}") String routingKey) {
        return BindingBuilder.bind(chatQueue).to(websocketExchange).with(routingKey);
    }

    @Bean
    public DirectExchange tradeEventExchange(
            @Value("${app.rabbitmq.trade.exchange:trade.events.exchange}") String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Binding tradeBinding(@Qualifier("tradeQueue") Queue tradeQueue,
                                DirectExchange tradeEventExchange,
                                @Value("${app.rabbitmq.websocket.trade.routing-key:trade.events.status}") String routingKey) {
        return BindingBuilder.bind(tradeQueue).to(tradeEventExchange).with(routingKey);
    }

    @Bean
    public Binding tradeWebsocketBinding(@Qualifier("tradeQueue") Queue tradeQueue,
                                         TopicExchange websocketExchange,
                                         @Value("${app.rabbitmq.websocket.trade.routing-key:trade.events.status}") String routingKey) {
        return BindingBuilder.bind(tradeQueue).to(websocketExchange).with(routingKey);
    }

    @Bean
    public Binding notificationBinding(@Qualifier("notificationQueue") Queue notificationQueue,
                                       TopicExchange websocketExchange,
                                       @Value("${app.rabbitmq.websocket.notification.routing-key:notification.new}") String routingKey) {
        return BindingBuilder.bind(notificationQueue).to(websocketExchange).with(routingKey);
    }
}
