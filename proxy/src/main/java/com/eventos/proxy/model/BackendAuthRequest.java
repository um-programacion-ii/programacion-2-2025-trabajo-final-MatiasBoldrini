package com.eventos.proxy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackendAuthRequest {
    private String username;
    private String password;
    private boolean rememberMe;
}


