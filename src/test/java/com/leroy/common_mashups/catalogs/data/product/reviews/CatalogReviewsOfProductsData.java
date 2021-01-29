package com.leroy.common_mashups.catalogs.data.product.reviews;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class CatalogReviewsOfProductsData {
    private Boolean recommended;
    private List<RatingDetails> rating_details;
    private String origin;
    private String id;
    private Author author;
    private List<Object> photos;
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


    @Data
    private static class RatingDetails {
        private Integer value;
        private String name;
        private String label;
    }

    @Data
    private static class Comment {
        private Author author;
        private Integer likes;
        private String id;
        private String author_avatar_url;
        private String created_at;
        private String author_id;
        private String author_name;
        private String text;
        private String author_type;
        private Integer dislikes;
        private JsonNode files;
        private String body;
        private String updated_at;
        private String author_occupation;
        private Boolean syndicated;
    }
}
