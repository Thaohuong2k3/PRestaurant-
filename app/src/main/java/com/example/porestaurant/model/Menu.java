package com.example.porestaurant.model;

import com.google.gson.annotations.SerializedName;

public class Menu {
    private int menuId;
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private String categoryName;
    private boolean isAvailable;

    private transient byte[] imageData;
    @SerializedName("imageMimeType")
    private String imageMimeType;
    @SerializedName("imageData")
    private String imageDataBase64;
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS Menu (" +
                    "menuId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "price REAL NOT NULL, " +
                    "categoryId INTEGER, " +
                    "categoryName TEXT, " +
                    "isAvailable INTEGER, " +
                    "imageData BLOB, " +
                    "imageMimeType TEXT" +
                    ")";

    // Getters and Setters
    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }

    public String getImageMimeType() { return imageMimeType; }
    public void setImageMimeType(String imageMimeType) { this.imageMimeType = imageMimeType; }
    public String getImageDataBase64() {
        return imageDataBase64;
    }

    public void setImageDataBase64(String imageDataBase64) {
        this.imageDataBase64 = imageDataBase64;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "menuId=" + menuId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", isAvailable=" + isAvailable +
                ", imageMimeType='" + imageMimeType + '\'' +
                ", imageData=" + (imageData != null ? imageData.length + " bytes" : "null") +
                '}';
    }

    private int quantity = 1;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Menu menu = (Menu) obj;
        return this.getMenuId() == menu.getMenuId();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(menuId).hashCode();
    }
}
