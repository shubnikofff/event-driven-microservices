package com.shubnikofff.productservice.core.errorhandling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
@Getter
public class ErrorMessage {

	private final Date timestamp;

	private final String message;

}
