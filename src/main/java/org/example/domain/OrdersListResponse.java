package org.example.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.models.Order;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrdersListResponse extends BaseResponse {

    private List<Order> orders;
    private int total;
    private int totalToday;


}
