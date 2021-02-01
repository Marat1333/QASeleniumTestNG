package com.leroy.common_mashups.catalogs.data.product.reviews;

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

    @Data
    private static class AuthorDetails {
        private String value;
        private String name;
        private String label;
    }
}
