package com.petshop.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MerchantDetailDTO {
    private Integer id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String logo;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<ServiceDTO> services;
    private List<ProductDTO> products;
    private List<AppointmentDTO> appointments;
    private List<ReviewDTO> reviews;
    
    private Long serviceCount;
    private Long productCount;
    private Long appointmentCount;
    private Long reviewCount;
    private Double averageRating;

    public MerchantDetailDTO() {
    }

    public MerchantDetailDTO(Integer id, String name, String contactPerson, String phone, String email, String address, String logo, String status, LocalDateTime createdAt, LocalDateTime updatedAt, List<ServiceDTO> services, List<ProductDTO> products, List<AppointmentDTO> appointments, List<ReviewDTO> reviews, Long serviceCount, Long productCount, Long appointmentCount, Long reviewCount, Double averageRating) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.logo = logo;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.services = services;
        this.products = products;
        this.appointments = appointments;
        this.reviews = reviews;
        this.serviceCount = serviceCount;
        this.productCount = productCount;
        this.appointmentCount = appointmentCount;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceDTO> services) {
        this.services = services;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }

    public Long getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(Long serviceCount) {
        this.serviceCount = serviceCount;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public Long getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(Long appointmentCount) {
        this.appointmentCount = appointmentCount;
    }

    public Long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String name;
        private String contactPerson;
        private String phone;
        private String email;
        private String address;
        private String logo;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<ServiceDTO> services;
        private List<ProductDTO> products;
        private List<AppointmentDTO> appointments;
        private List<ReviewDTO> reviews;
        private Long serviceCount;
        private Long productCount;
        private Long appointmentCount;
        private Long reviewCount;
        private Double averageRating;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder contactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder logo(String logo) {
            this.logo = logo;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder services(List<ServiceDTO> services) {
            this.services = services;
            return this;
        }

        public Builder products(List<ProductDTO> products) {
            this.products = products;
            return this;
        }

        public Builder appointments(List<AppointmentDTO> appointments) {
            this.appointments = appointments;
            return this;
        }

        public Builder reviews(List<ReviewDTO> reviews) {
            this.reviews = reviews;
            return this;
        }

        public Builder serviceCount(Long serviceCount) {
            this.serviceCount = serviceCount;
            return this;
        }

        public Builder productCount(Long productCount) {
            this.productCount = productCount;
            return this;
        }

        public Builder appointmentCount(Long appointmentCount) {
            this.appointmentCount = appointmentCount;
            return this;
        }

        public Builder reviewCount(Long reviewCount) {
            this.reviewCount = reviewCount;
            return this;
        }

        public Builder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public MerchantDetailDTO build() {
            return new MerchantDetailDTO(id, name, contactPerson, phone, email, address, logo, status, createdAt, updatedAt, services, products, appointments, reviews, serviceCount, productCount, appointmentCount, reviewCount, averageRating);
        }
    }
}
