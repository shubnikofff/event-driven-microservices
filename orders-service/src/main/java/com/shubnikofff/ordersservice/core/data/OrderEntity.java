package com.shubnikofff.ordersservice.core.data;

import com.shubnikofff.core.model.OrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity implements Serializable {

	private static final long serialVersionUID = 4668263514426495128L;
	@Id
	@Column(unique = true)
	public String orderId;
	private String productId;
	private String userId;
	private int quantity;
	private String addressId;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

}
