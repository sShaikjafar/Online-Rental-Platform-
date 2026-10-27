package com.rental.platform.dto;

import com.rental.platform.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal pricePerDay;
    private String categoryName;
    private Long categoryId;
    private String ownerName;
    private Long ownerId;
    private List<String> imageUrls;
    private String location;
    private Double latitude;
    private Double longitude;
    private String status;
    private boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor from Product entity
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.pricePerDay = product.getPricePerDay();
        this.categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        this.categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        this.ownerName = product.getOwner() != null ? product.getOwner().getName() : null;
        this.ownerId = product.getOwner() != null ? product.getOwner().getId() : null;
        this.imageUrls = product.getImageUrls();
        this.location = product.getLocation();
        this.latitude = product.getLatitude();
        this.longitude = product.getLongitude();
        this.status = product.getStatus() != null ? product.getStatus().name() : null;
        this.approved = product.isApproved();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}