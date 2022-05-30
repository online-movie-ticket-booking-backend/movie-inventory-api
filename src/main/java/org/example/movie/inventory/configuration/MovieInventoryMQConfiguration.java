package org.example.movie.inventory.configuration;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("message")
public class MovieInventoryMQConfiguration {

  private MessageConfiguration messageConfiguration;
  private ExchangeConfiguration movieDetailsExchangeConfiguration;
  private ExchangeConfiguration movieInventoryExchangeConfiguration;

  @Bean
  public Exchange movieInventoryExchange() {
    return ExchangeBuilder.topicExchange(movieInventoryExchangeConfiguration.getExchange())
        .build();
  }

  @Bean
  public Queue movieInventoryQueue() {
    return new Queue(movieInventoryExchangeConfiguration.getQueue(), true);
  }

  @Bean
  public ExchangeConfiguration movieDetailsExchange() {
    return movieDetailsExchangeConfiguration;
  }

  @Bean
  public Binding movieInventoryBinding() {
    return BindingBuilder.bind(movieInventoryQueue())
        .to(movieInventoryExchange())
        .with(movieInventoryExchangeConfiguration.getRoutingKey())
        .noargs();
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public CachingConnectionFactory connectionFactory() {
    CachingConnectionFactory cachingConnectionFactory =
        new CachingConnectionFactory(messageConfiguration.getHost());
    cachingConnectionFactory.setUsername(messageConfiguration.getUsername());
    cachingConnectionFactory.setPassword(messageConfiguration.getPassword());
    cachingConnectionFactory.setPort(messageConfiguration.getPort());
    cachingConnectionFactory.setVirtualHost(messageConfiguration.getVirtualHost());
    return cachingConnectionFactory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }
}
