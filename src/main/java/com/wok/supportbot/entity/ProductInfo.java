package com.wok.supportbot.entity;

import lombok.Data;

@Data
public class ProductInfo {
    private String title;
    private String description;
    private String price;
    private String rating;
    private Integer reviewCount;
    private String brand;
    private String category;
}
