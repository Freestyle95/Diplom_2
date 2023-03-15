package org.example.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateOrderResponse extends BaseResponse {
    private String name;
    private Order order;
}
