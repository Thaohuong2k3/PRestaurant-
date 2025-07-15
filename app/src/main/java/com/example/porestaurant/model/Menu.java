package com.example.porestaurant.model;

public class Menu {
    private int menuId;
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private String categoryName;
    private boolean isAvailable;
    private String image;


    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS Menu (" +
                    "menuId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "price REAL NOT NULL, " +
                    "categoryId INTEGER, " +
                    "categoryName TEXT, " +
                    "isAvailable INTEGER" +
                    "image TEXT"  +
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + menuId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", image='" + image + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
