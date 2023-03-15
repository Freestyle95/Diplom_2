package org.example.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthorizationResponse extends BaseResponse {
    private String accessToken;
    private String refreshToken;
    private User user;
}
