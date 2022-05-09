package com.shubnikofff.productservice.query;

import com.shubnikofff.productservice.core.data.ProductEntity;
import com.shubnikofff.productservice.core.data.ProductsRepository;
import com.shubnikofff.productservice.query.rest.ProductRestModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductsQueryHandler {

	final private ProductsRepository productsRepository;

	@QueryHandler
	public List<ProductRestModel> findProducts(FindProductsQuery query) {
		return productsRepository.findAll().stream()
				.map(productEntity -> {
					final var productRestModel = new ProductRestModel();
					BeanUtils.copyProperties(productEntity, productRestModel);
					return productRestModel;
				}).collect(Collectors.toList());
	}

}
