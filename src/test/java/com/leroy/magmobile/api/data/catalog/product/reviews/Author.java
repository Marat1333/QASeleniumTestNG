package com.leroy.magmobile.api.data.catalog.product.reviews;

import lombok.Data;

import java.util.List;

@Data
public class Author {
    private String name;
    private String initials;
    private String location;
    private List<AuthorDetails> details;
    private String avatar_url;
    private String occupation;
}
