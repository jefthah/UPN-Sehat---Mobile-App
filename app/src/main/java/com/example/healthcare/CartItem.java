package com.example.healthcare;

public class CartItem {
    private String product;
    private String price;
    private String email;
    private String otype;
    private String username;

    // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
    public CartItem() {
    }

    // Constructor with all parameters
    public CartItem(String product, String price, String email, String otype, String username) {
        this.product = product;
        this.price = price;
        this.email = email;
        this.otype = otype;
        this.username = username;
    }

    // Constructor with product and price only
    public CartItem(String product, String price) {
        this.product = product;
        this.price = price;
    }

    // Getters and setters
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtype() {
        return otype;
    }

    public void setOtype(String otype) {
        this.otype = otype;
    }

    public String getUsername() {
        return username;
    }
}