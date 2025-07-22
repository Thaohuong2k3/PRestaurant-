package com.example.porestaurant.model;

public class Category {
    private int categoryId;
    private String categoryName;
    private String description;

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Category (" +
            "categoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "categoryName TEXT NOT NULL, " +
            "description TEXT" +
            ")";

    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        // Spinner sẽ gọi vào đây để lấy chuỗi hiển thị
        return categoryName;
    }
}
