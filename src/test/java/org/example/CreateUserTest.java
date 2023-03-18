package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.example.domain.AuthorizationResponse;
import org.example.domain.ErrorResponse;
import org.example.models.User;
import org.example.utils.CheckUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static io.qameta.allure.Allure.step;

public class CreateUserTest extends BaseUserTest {
    @After
    public void tearDown() {
        clearUsers();
    }

    @Before
    public void setUp() {
        init();
    }

    @Test
    @DisplayName("Create user")
    public void createUser() {
        User userToCreate = userSteps.generateRandomUser();
        createdUsers.add(userToCreate);
        ValidatableResponse response = userSteps.createUser(userToCreate);
        step("Check response", () -> {
            CheckUtils.checkStatusCode(response.extract().statusCode(), HttpStatus.SC_OK);

            AuthorizationResponse authResponse = response.extract().as(AuthorizationResponse.class);
            CheckUtils.checkFieldEquals("success", true, authResponse.isSuccess());
            CheckUtils.checkFieldContainsString("accessToken", "Bearer ", authResponse.getAccessToken());
            CheckUtils.checkFieldIsNotBlank("refreshToken", authResponse.getRefreshToken());

            User createdUser = authResponse.getUser();
            CheckUtils.checkFieldEquals("user name", userToCreate.getName(), createdUser.getName());
            CheckUtils.checkFieldEquals("user email", userToCreate.getEmail(), createdUser.getEmail());
        });
    }

    @Test
    @DisplayName("Create duplicated user")
    public void createDuplicatedUser() {
        User userToCreate = userSteps.generateRandomUser();
        createdUsers.add(userToCreate);
        step("Create unique user", () ->
        {
            userSteps.createUser(userToCreate);
        });
        AtomicReference<ValidatableResponse> response = new AtomicReference<>();
        step("Create user with same data again", () ->
                response.set(userSteps.createUser(userToCreate)));
        step("Check response", () -> {
            CheckUtils.checkStatusCode(response.get().extract().statusCode(), HttpStatus.SC_FORBIDDEN);

            ErrorResponse errorResponse = response.get().extract().as(ErrorResponse.class);
            CheckUtils.checkFieldEquals("success", false, errorResponse.isSuccess());
            CheckUtils.checkFieldEquals("message", "User already exists", errorResponse.getMessage());
        });
    }
}
