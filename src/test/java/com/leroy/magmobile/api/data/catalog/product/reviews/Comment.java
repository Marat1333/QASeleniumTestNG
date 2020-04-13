package com.leroy.magmobile.api.data.catalog.product.reviews;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class Comment {
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
