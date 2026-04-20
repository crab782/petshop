package com.petshop.dto;

public class MerchantRegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String contact_person;
    private String name;
    private String logo;
    private String address;

    public MerchantRegisterRequest() {
    }

    public MerchantRegisterRequest(String username, String password, String email, String phone, String contact_person, String name, String logo, String address) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.contact_person = contact_person;
        this.name = name;
        this.logo = logo;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password;
        private String email;
        private String phone;
        private String contact_person;
        private String name;
        private String logo;
        private String address;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder contact_person(String contact_person) {
            this.contact_person = contact_person;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder logo(String logo) {
            this.logo = logo;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public MerchantRegisterRequest build() {
            return new MerchantRegisterRequest(username, password, email, phone, contact_person, name, logo, address);
        }
    }
}