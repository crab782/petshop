package com.petshop.dto;

public class LoginRequest {
    private String username;
    private String phone;
    private String password;
    private String role;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        if (loginIdentifier != null && !loginIdentifier.isEmpty()) {
            if (loginIdentifier.contains("@")) {
                this.username = loginIdentifier;
            } else {
                this.phone = loginIdentifier;
            }
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLoginIdentifier() {
        if (phone != null && !phone.isEmpty()) {
            return phone;
        }
        return username;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String phone;
        private String password;
        private String role;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public LoginRequest build() {
            LoginRequest request = new LoginRequest();
            request.username = this.username;
            request.phone = this.phone;
            request.password = this.password;
            request.role = this.role;
            return request;
        }
    }
}
