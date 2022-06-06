package com.shubnikofff.core.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FetchUserPaymentDetailsQuery {
	private final String userId;
}
