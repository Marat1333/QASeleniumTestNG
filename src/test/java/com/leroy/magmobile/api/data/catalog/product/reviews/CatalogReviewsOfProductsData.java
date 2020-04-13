package com.leroy.magmobile.api.data.catalog.product.reviews;

import lombok.Data;

import java.util.List;

@Data
public class CatalogReviewsOfProductsData {
    private Boolean recommended;
    private List<RatingDetails> rating_details;
    private String origin;
    private String id;
    private Author author;
    private List<String> photos;
    private String syndication_source;
    private Boolean syndicated;
    private Boolean noindex;
    private String pros;
    private Integer rating;
    private Integer likes;
    private Integer dislikes;
    private String cons;
    private String order_number;
    private String dimensions;
    private String published_at;
    private String created_at;
    private Boolean verified;
    private List<Comment> comments;
    private String body;
    private String context_type;
    private String updated_at;
    private String headline;
}
