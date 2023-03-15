package org.example.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrdersListReponse extends BaseResponse {

    private List<Order> orders;
    private int total;
    private int totalToday;


}
