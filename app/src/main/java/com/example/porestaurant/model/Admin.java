package com.example.porestaurant.model;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Admin {

    // DTO cho doanh thu theo tháng
    public static class RevenueByMonthDto {
        private int month;

        @SerializedName("totalRevenue")
        private double revenue;

        public RevenueByMonthDto() { }

        public RevenueByMonthDto(int month, double revenue) {
            this.month = month;
            this.revenue = revenue;
        }

        public int getMonth() {
            return month;
        }
        public void setMonth(int month) {
            this.month = month;
        }
        public double getRevenue() {
            return revenue;
        }
        public void setRevenue(double revenue) {
            this.revenue = revenue;
        }
    }

    // DTO cho số đơn theo tháng
    public static class OrderCountByMonthDto {
        private int month;
        private int orderCount;

        public OrderCountByMonthDto() { }

        public OrderCountByMonthDto(int month, int orderCount) {
            this.month = month;
            this.orderCount = orderCount;
        }

        public int getMonth() {
            return month;
        }
        public void setMonth(int month) {
            this.month = month;
        }
        public int getOrderCount() {
            return orderCount;
        }
        public void setOrderCount(int orderCount) {
            this.orderCount = orderCount;
        }
    }

    // DTO cho top menu items
    public static class TopMenuItemDto {
        @SerializedName("menuID")
        private int menuItemId;

        @SerializedName("name")
        private String name;

        @SerializedName("quantitySold")
        private int totalSold;

        public TopMenuItemDto() { }

        public TopMenuItemDto(int menuItemId, String name, int totalSold) {
            this.menuItemId = menuItemId;
            this.name = name;
            this.totalSold = totalSold;
        }

        public int getMenuItemId() {
            return menuItemId;
        }
        public void setMenuItemId(int menuItemId) {
            this.menuItemId = menuItemId;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getTotalSold() {
            return totalSold;
        }
        public void setTotalSold(int totalSold) {
            this.totalSold = totalSold;
        }
    }
    // DTO cho tỷ lệ bàn ngồi
    public static class TableOccupancyDto {
        @SerializedName("tableID")
        private int tableId;

        @SerializedName("tableNumber")
        private String tableNumber;

        @SerializedName("occupancyRate")
        private double occupancyRate;

        public TableOccupancyDto() { }

        public TableOccupancyDto(int tableId, String tableNumber, double occupancyRate) {
            this.tableId = tableId;
            this.tableNumber = tableNumber;
            this.occupancyRate = occupancyRate;
        }

        public int getTableId() {
            return tableId;
        }
        public void setTableId(int tableId) {
            this.tableId = tableId;
        }
        public String getTableNumber() {
            return tableNumber;
        }
        public void setTableNumber(String tableNumber) {
            this.tableNumber = tableNumber;
        }
        public double getOccupancyRate() {
            return occupancyRate;
        }
        public void setOccupancyRate(double occupancyRate) {
            this.occupancyRate = occupancyRate;
        }
    }

    // DTO cho phương thức thanh toán
    public static class PaymentMethodDto {
        private String method;
        private int count;

        public PaymentMethodDto() { }

        public PaymentMethodDto(String method, int count) {
            this.method = method;
            this.count = count;
        }

        public String getMethod() {
            return method;
        }
        public void setMethod(String method) {
            this.method = method;
        }
        public int getCount() {
            return count;
        }
        public void setCount(int count) {
            this.count = count;
        }
    }

    // DTO cho số khách theo tháng
    public static class CustomerCountByMonthDto {
        private int month;
        private int customerCount;

        public CustomerCountByMonthDto() { }

        public CustomerCountByMonthDto(int month, int customerCount) {
            this.month = month;
            this.customerCount = customerCount;
        }

        public int getMonth() {
            return month;
        }
        public void setMonth(int month) {
            this.month = month;
        }
        public int getCustomerCount() {
            return customerCount;
        }
        public void setCustomerCount(int customerCount) {
            this.customerCount = customerCount;
        }
    }

    // DTO cho thống kê chung menu (nếu vẫn cần)
    public static class MenuStatsDto {
        private int totalItems;
        private double averagePrice;
        private String mostExpensiveItem;

        public MenuStatsDto() { }

        public MenuStatsDto(int totalItems, double averagePrice, String mostExpensiveItem) {
            this.totalItems = totalItems;
            this.averagePrice = averagePrice;
            this.mostExpensiveItem = mostExpensiveItem;
        }

        public int getTotalItems() {
            return totalItems;
        }
        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }
        public double getAveragePrice() {
            return averagePrice;
        }
        public void setAveragePrice(double averagePrice) {
            this.averagePrice = averagePrice;
        }
        public String getMostExpensiveItem() {
            return mostExpensiveItem;
        }
        public void setMostExpensiveItem(String mostExpensiveItem) {
            this.mostExpensiveItem = mostExpensiveItem;
        }
    }
    public static class MenuDTO {
        private int menuId;
        private String name;
        private double price;

        // Thay byte[] bằng String để nhận Base64 từ API
        @SerializedName("imageData")
        private String imageDataBase64;

        @SerializedName("imageMimeType")
        private String imageMimeType;

        public MenuDTO() { }

        public int getMenuId() {
            return menuId;
        }
        public void setMenuId(int menuId) {
            this.menuId = menuId;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }
        public void setPrice(double price) {
            this.price = price;
        }

        /**
         * Trả về mảng byte đã decode từ Base64.
         * Nếu imageDataBase64 == null sẽ trả về null.
         */
        public byte[] getImageData() {
            if (imageDataBase64 == null) return null;
            return Base64.decode(imageDataBase64, Base64.DEFAULT);
        }

        /**
         * Khi gửi lên server (nếu bạn cần), encode byte[] thành Base64.
         */
        public void setImageData(byte[] data) {
            if (data != null) {
                this.imageDataBase64 = Base64.encodeToString(data, Base64.NO_WRAP);
            } else {
                this.imageDataBase64 = null;
            }
        }

        public String getImageMimeType() {
            return imageMimeType;
        }
        public void setImageMimeType(String imageMimeType) {
            this.imageMimeType = imageMimeType;
        }
    }


    public class TableDTO {
        private int tableId;
        private String tableNumber;
        private String status;

        public int getTableId() { return tableId; }
        public void setTableId(int tableId) { this.tableId = tableId; }
        public String getTableNumber() { return tableNumber; }
        public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    public static class TableBookingRequest {
        private int tableId;
        private Date checkin;

        public int getTableId() { return tableId; }
        public void setTableId(int tableId) { this.tableId = tableId; }
        public Date getCheckin() { return checkin; }
        public void setCheckin(Date checkin) { this.checkin = checkin; }
    }
    public static class TableStatusUpdateRequest {
        private String newStatus;

        public String getNewStatus() { return newStatus; }
        public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    }
}
