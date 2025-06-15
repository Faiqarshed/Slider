package com.example.Sliderbackend;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GraphQLApi {
    @POST("/graphql")
    Call<GraphQLResponse> executeQuery(
            @Body GraphQLRequest request,
            @Header("Authorization") String token

    );
}
