package org.example.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.models.User;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResponse extends BaseResponse {
    private User user;
}