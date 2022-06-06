package com.shubnikofff.usersservice.query;

import com.shubnikofff.core.model.PaymentDetails;
import com.shubnikofff.core.model.User;
import com.shubnikofff.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEventsHandler {

	@QueryHandler
	public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query) {
		final var paymentDetails = PaymentDetails.builder()
				.cardNumber("123Card")
				.cvv("123")
				.name("JOHN SMITH")
				.validUntilMonth(12)
				.validUntilYear(2030)
				.build();

		return User.builder()
				.firstName("John")
				.lastName("Smith")
				.userId(query.getUserId())
				.paymentDetails(paymentDetails)
				.build();
	}
}
