package com.example.resiliencemap.functional;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_SMS_DIRECT = "sms.direct";
    public static final String EXCHANGE_DLX = "sms.dlx";
    public static final String QUEUE_INBOUND = "sms.inbound";
    public static final String QUEUE_OUTBOUND = "sms.outbound";
    public static final String QUEUE_OUTBOUND_DLQ = "sms.outbound.dlq";
    public static final String ROUTING_KEY_INBOUND = "inbound";
    public static final String ROUTING_KEY_OUTBOUND = "outbound";

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public DirectExchange smsDirectExchange() {
        return new DirectExchange(EXCHANGE_SMS_DIRECT, true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(EXCHANGE_DLX, true, false);
    }


    @Bean
    public Queue inboundQueue() {
        return QueueBuilder.durable(QUEUE_INBOUND).build();
    }

    @Bean
    public Queue outboundQueue() {
        return QueueBuilder.durable(QUEUE_OUTBOUND)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .build();
    }

    @Bean
    public Queue outboundDlq() {
        return QueueBuilder.durable(QUEUE_OUTBOUND_DLQ).build();
    }

    @Bean
    public Binding inboundBinding(Queue inboundQueue, DirectExchange smsDirectExchange) {
        return BindingBuilder.bind(inboundQueue)
                .to(smsDirectExchange)
                .with(ROUTING_KEY_INBOUND);
    }

    @Bean
    public Binding outboundBinding(Queue outboundQueue, DirectExchange smsDirectExchange) {
        return BindingBuilder.bind(outboundQueue)
                .to(smsDirectExchange)
                .with(ROUTING_KEY_OUTBOUND);
    }

    @Bean
    public Binding dlqBinding(Queue outboundDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(outboundDlq)
                .to(dlxExchange)
                .with(ROUTING_KEY_OUTBOUND);
    }
}
