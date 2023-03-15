package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.example.ApiConfig;
import org.example.models.AuthorizationResponse;
import org.example.models.User;

import java.util.List;

import static io.restassured.RestAssured.given;

public class UserSteps extends ApiConfig {
    private final static String AUTH_PATH = "/auth";
    private final static String REGISTER_PATH = AUTH_PATH + "/register";
    private final static String USER_PATH = AUTH_PATH + "/user";
    private final static String LOGIN_PATH = AUTH_PATH + "/login";
    private final static String LOGOUT_PATH = AUTH_PATH + "/logout";
    private final static String TOKEN_PATH = AUTH_PATH + "/token";

    @Step("Create user")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getSpecs())
                .body(user)
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Get user")
    public ValidatableResponse getUser(String accessToken) {
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .get(USER_PATH)
                .then();
    }

    @Step("Get user")
    public ValidatableResponse getUser(User user) {
        String accessToken = loginUser(user).extract().as(AuthorizationResponse.class).getAccessToken();
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .get(USER_PATH)
                .then();
    }

    @Step("Update user data with authorization")
    public ValidatableResponse updateUserWithAuthorization(User user, String accessToken) {
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .body(user)
                .patch(USER_PATH)
                .then();
    }

    @Step("Update user data with authorization")
    public ValidatableResponse updateUserWithAuthorization(User newUserData, User currentUser) {
        String accessToken = loginUser(currentUser).extract().as(AuthorizationResponse.class).getAccessToken();
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .body(newUserData)
                .patch(USER_PATH)
                .then();
    }

    @Step("Update user data without authorization")
    public ValidatableResponse updateUserWithoutAuthorization(User user) {
        return given()
                .spec(getSpecs())
                .body(user)
                .patch(USER_PATH)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .delete(USER_PATH)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(User user) {
        String accessToken = loginUser(user).extract().as(AuthorizationResponse.class).getAccessToken();
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .delete(USER_PATH)
                .then();
    }

    @Step("Login user")
    public ValidatableResponse loginUser(User user) {
        return given()
                .spec(getSpecs())
                .body(User.builder()
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .build())
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Generate random user")
    public User generateRandomUser() {
        return User.builder()
                .name(String.format("%s %s", RandomStringUtils.randomAlphabetic(8), RandomStringUtils.randomAlphabetic(8)))
                .email(String.format("%s@example.com", RandomStringUtils.randomAlphanumeric(8)).toLowerCase())
                .password(RandomStringUtils.randomAlphanumeric(10))
                .build();
    }

    @Step("Post-condition: delete created users")
    public void deleteUsersPostcondition(List<User> users) {
        for (User user : users) {
            try {
                deleteUser(user).statusCode(HttpStatus.SC_ACCEPTED);
            } catch (Exception e) {
                System.out.printf("Неудачная попытка удалить пользователя %s%n", user);
            }
        }
    }
}
