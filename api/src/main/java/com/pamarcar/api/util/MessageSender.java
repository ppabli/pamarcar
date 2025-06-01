package com.pamarcar.api.util;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

	private final RabbitTemplate rabbitTemplate;

	public MessageSender(RabbitTemplate rabbitTemplate) {

		this.rabbitTemplate = rabbitTemplate;

	}

	public void send(String queueName, Object message) {

		rabbitTemplate.convertAndSend(queueName, message);

	}

}
