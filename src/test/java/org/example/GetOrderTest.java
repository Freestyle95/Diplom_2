package org.example;

import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.example.domain.BaseResponse;
import org.example.domain.ErrorResponse;
import org.example.models.Order;
import org.example.utils.CheckUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.qameta.allure.Allure.step;

public class GetOrderTest extends BaseOrderTest {
    @After
    public void tearDown() {
        clearUsers();
    }

    @Before
    public void setUp() {
        init();
    }

    @Test
    public void getOrderWithAuthorization() {
        Order orderToCreate = orderSteps.generateRandomOrderWithIngredients(accessToken);
        ValidatableResponse createOrderResponse = orderSteps.createOrderWithAuthorization(orderToCreate, accessToken);
        CheckUtils.checkStatusCode(HttpStatus.SC_OK, createOrderResponse.extract().statusCode());

        ValidatableResponse getOrderResponse = orderSteps.getOrdersWithAuthorization(accessToken);
        CheckUtils.checkStatusCode(HttpStatus.SC_OK, getOrderResponse.extract().statusCode());
        CheckUtils.checkFieldEquals("success", true, getOrderResponse.extract().as(BaseResponse.class).isSuccess());

        CheckUtils.checkFieldIsNotBlank("id", getOrderResponse.extract().path("orders[0]._id").toString());
    }

    @Test
    public void getOrderWithoutAuthorization() {
        Order orderToCreate = orderSteps.generateRandomOrderWithIngredients(accessToken);
        ValidatableResponse createOrderResponse = orderSteps.createOrderWithAuthorization(orderToCreate, accessToken);
        CheckUtils.checkStatusCode(HttpStatus.SC_OK, createOrderResponse.extract().statusCode());

        ValidatableResponse getOrderResponse = orderSteps.getOrdersWithoutAuthorization();
        CheckUtils.checkStatusCode(HttpStatus.SC_UNAUTHORIZED, getOrderResponse.extract().statusCode());
        ErrorResponse errorResponse = getOrderResponse.extract().as(ErrorResponse.class);
        step("Check error", () -> {
            CheckUtils.checkFieldEquals("success", false, errorResponse.isSuccess());
            CheckUtils.checkFieldEquals("message", "You should be authorised", errorResponse.getMessage());
        });
    }
}
