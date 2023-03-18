package org.example.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.models.Ingredient;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class IngredientsResponse extends BaseResponse {
    private List<Ingredient> data;
}
