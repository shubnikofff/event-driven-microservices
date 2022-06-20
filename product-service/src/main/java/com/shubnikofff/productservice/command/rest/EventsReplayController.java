package com.shubnikofff.productservice.command.rest;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.axonframework.springboot.autoconfig.EventProcessingAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/management")
public class EventsReplayController {

	private final EventProcessingConfiguration eventProcessingAutoConfiguration;

	@PostMapping("/eventProcessor/{processorName}/reset")
	public ResponseEntity<String> replayEvents(@PathVariable String processorName) {
		return eventProcessingAutoConfiguration.eventProcessor(processorName, TrackingEventProcessor.class)
				.map(eventProcessor -> {
					eventProcessor.shutDown();
					eventProcessor.resetTokens();
					eventProcessor.start();
					return ResponseEntity.ok().body(String.format("The event processor with name [%s] has been reset", eventProcessor.getName()));
				})
				.orElse(ResponseEntity.badRequest()
						.body(String.format("The event processor with a name [%s] is not a tracking event processor." +
								" Only event processor is supported", processorName)));
	}
}
