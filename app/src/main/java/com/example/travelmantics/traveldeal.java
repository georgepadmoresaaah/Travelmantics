package com.example.travelmantics;

public class traveldeal {


    private String title;
    private String  price;
    private String description;
    private String imageUrl;

    public traveldeal(String title, String price, String description, String imageUrl) {

        this.setTitle(title);
        this.setPrice(price);
        this.setDescription(description);
        this.setImageUrl(imageUrl);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
