package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomUtils;
import org.example.ApiConfig;
import org.example.domain.CreateOrderRequest;
import org.example.domain.IngredientsResponse;
import org.example.models.Ingredient;
import org.example.models.Order;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderSteps extends ApiConfig {
    private final static String ORDER_PATH = "/orders";
    private final static String INGREDIENTS_PATH = "/ingredients";

    @Step("Get ingredients")
    public ValidatableResponse getIngredients(String accessToken) {
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .get(INGREDIENTS_PATH)
                .then();
    }

    @Step("Get orders with authorization")
    public ValidatableResponse getOrdersWithAuthorization(String accessToken) {
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .get(ORDER_PATH)
                .then();
    }

    @Step("Get orders without authorization")
    public ValidatableResponse getOrdersWithoutAuthorization() {
        return given()
                .spec(getSpecs())
                .get(ORDER_PATH)
                .then();
    }

    @Step("Create order with authorization")
    public ValidatableResponse createOrderWithAuthorization(Order order, String accessToken) {
        List<String> ingredientsHashes = new ArrayList<>();
        for (Ingredient item :
                order.getIngredients()) {
            ingredientsHashes.add(item.get_id());
        }
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .body(CreateOrderRequest.builder()
                        .ingredients(ingredientsHashes)
                        .build())
                .post(ORDER_PATH)
                .then();
    }

    @Step("Create order without authorization")
    public ValidatableResponse createOrderWithoutAuthorization(Order order) {
        List<String> ingredientsHashes = new ArrayList<>();
        for (Ingredient item :
                order.getIngredients()) {
            ingredientsHashes.add(item.get_id());
        }
        return given()
                .spec(getSpecs())
                .body(CreateOrderRequest.builder()
                        .ingredients(ingredientsHashes)
                        .build()
                )
                .post(ORDER_PATH)
                .then();
    }

    @Step("Generate random order with ingredients")
    public Order generateRandomOrderWithIngredients(String accessToken) {
        List<Ingredient> allIngredients = getIngredients(accessToken).extract().as(IngredientsResponse.class).getData();
        List<Ingredient> ingredientsToOrder = new ArrayList<>();
        for (int i = 0; i < RandomUtils.nextInt(3, allIngredients.size()); i++) {
            int ingredientIndex = RandomUtils.nextInt(0, allIngredients.size());
            ingredientsToOrder.add(allIngredients.get(ingredientIndex));
            allIngredients.remove(ingredientIndex);
        }
        return Order.builder()
                .ingredients(ingredientsToOrder)
                .build();
    }
    @Step("Generate random order without ingredients")
    public Order generateRandomOrderWithoutIngredients() {
        return Order.builder()
                .ingredients(List.of())
                .build();
    }
}
