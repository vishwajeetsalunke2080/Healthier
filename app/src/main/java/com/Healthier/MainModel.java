package com.Healthier;

public class MainModel {

    String meal, dish, imgSrc ,calories;

    MainModel() {
    }

    public MainModel(String meal, String dish, String imgSrc, String calories) {
        this.meal = meal;
        this.dish = dish;
        this.imgSrc = imgSrc;
        this.calories = calories;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}