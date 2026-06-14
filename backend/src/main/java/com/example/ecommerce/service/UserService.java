
package com.example.ecommerce.service;

import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.LoginResponse;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.entity.User;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);
    User register(RegisterRequest registerRequest);
    User findByUsername(String username);
    User findById(Long id);
}
