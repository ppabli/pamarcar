package com.pamarcar.api.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

	public static final String CREATE_ACCESS_QUEUE = "CREATE_ACCESS_QUEUE";

	@Bean
	public Queue travelerRegistryQueue() {

		return new Queue(CREATE_ACCESS_QUEUE, true);

	}

}
