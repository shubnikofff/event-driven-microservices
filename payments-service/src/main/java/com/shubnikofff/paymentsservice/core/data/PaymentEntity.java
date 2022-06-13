package com.shubnikofff.paymentsservice.core.data;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentEntity {

	@Id
	private String paymentId;

	@Column
	public String orderId;
}
