package com.example.Sliderbackend;

public class GraphQLRequest {
    private String query;

    public GraphQLRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
