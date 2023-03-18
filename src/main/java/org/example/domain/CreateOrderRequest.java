package org.example.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateOrderRequest {
    private List<String> ingredients;
}
