package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.example.domain.CreateOrderResponse;
import org.example.domain.ErrorResponse;
import org.example.models.Ingredient;
import org.example.models.Order;
import org.example.utils.CheckUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;

public class CreateOrderTest extends BaseOrderTest {
    @After
    public void tearDown() {
        clearUsers();
    }

    @Before
    public void setUp() {
        init();
    }

    @Test
    @DisplayName("Create order with authorization")
    public void createOrderWithAuthorization() {
        Order orderToCreate = orderSteps.generateRandomOrderWithIngredients(accessToken);
        ValidatableResponse createOrderResponse = orderSteps.createOrderWithAuthorization(orderToCreate, accessToken);
        CheckUtils.checkStatusCode(HttpStatus.SC_OK, createOrderResponse.extract().statusCode());

        CreateOrderResponse response = createOrderResponse.extract().as(CreateOrderResponse.class);

        CheckUtils.checkFieldIsNotNull(
                "orders list size",
                response.getOrder().getNumber()
        );
    }

    @Test
    @DisplayName("Create order without authorization")
    public void createOrderWithoutAuthorization() {
        Order orderToCreate = orderSteps.generateRandomOrderWithIngredients(accessToken);
        ValidatableResponse createOrderResponse = orderSteps.createOrderWithoutAuthorization(orderToCreate);
        CheckUtils.checkStatusCode(HttpStatus.SC_OK, createOrderResponse.extract().statusCode());
        CreateOrderResponse response = createOrderResponse.extract().as(CreateOrderResponse.class);

        CheckUtils.checkFieldIsNotNull(
                "orders list size",
                response.getOrder().getNumber()
        );
    }

    @Test
    @DisplayName("Create order with ingredients")
    public void createOrderWithIngredients() {
        Order orderToCreate = orderSteps.generateRandomOrderWithIngredients(accessToken);
        ValidatableResponse createOrderResponse = orderSteps.createOrderWithAuthorization(orderToCreate, accessToken);
        CheckUtils.checkStatusCode(HttpStatus.SC_OK, createOrderResponse.extract().statusCode());

        Order createdOrder = createOrderResponse.statusCode(HttpStatus.SC_OK).extract().as(CreateOrderResponse.class).getOrder();

        CheckUtils.checkFieldEquals("ingredients list size", orderToCreate.getIngredients().size(), createdOrder.getIngredients().size());
    }

    @Test
    @DisplayName("Create order without ingredients")
    public void createOrderWithoutIngredients() {
        Order orderToCreate = orderSteps.generateRandomOrderWithoutIngredients();
        ValidatableResponse createOrderResponse = orderSteps.createOrderWithAuthorization(orderToCreate, accessToken);
        CheckUtils.checkStatusCode(HttpStatus.SC_BAD_REQUEST, createOrderResponse.extract().statusCode());
        ErrorResponse errorResponse = createOrderResponse.extract().as(ErrorResponse.class);
        step("Check error", () -> {
            CheckUtils.checkFieldEquals("success", false, errorResponse.isSuccess());
            CheckUtils.checkFieldEquals("message", "Ingredient ids must be provided", errorResponse.getMessage());

        });
    }

    @Test
    @DisplayName("Create order with invalid ingredients")
    public void createOrderWithInvalidIngredients() {
        Order orderToCreate = orderSteps.generateRandomOrderWithoutIngredients();
        orderToCreate.setIngredients(
                List.of(
                        Ingredient.builder()
                                ._id(RandomStringUtils.randomAlphabetic(15))
                                .build()
                )
        );
        step("Create order with invalid ingredient hash", () -> {
            ValidatableResponse createOrderResponse = orderSteps.createOrderWithAuthorization(orderToCreate, accessToken);
            CheckUtils.checkStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR, createOrderResponse.extract().statusCode());
        });
    }
}
