package com.lql.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

//@AllArgsConstructor
//@NoArgsConstructor
public record UserAccount(String username, String password, String firstname, String lastname, String email) {

    public UserAccount(String username, String password) {
        this(username, password, "", "", "");
    }

    public UserAccount(String username, String password, String firstname, String lastname, String email) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }
}
