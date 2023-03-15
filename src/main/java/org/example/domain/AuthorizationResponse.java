package org.example.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.models.User;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthorizationResponse extends BaseResponse {
    private String accessToken;
    private String refreshToken;
    private User user;
}
