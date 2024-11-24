package com.telda.movieApp.data.api

import com.google.gson.JsonObject
import com.telda.movieApp.data.model.MovieCreditResponse
import com.telda.movieApp.data.model.MovieDetailsResponse
import com.telda.movieApp.data.model.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun fetchAllCategories(): Response<JsonObject?>


    @GET("movie/popular")
    suspend fun fetchPopularMovies(@Query("page") page: Int): Response<MoviesResponse?>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
    ): Response<MoviesResponse?>

    @GET("movie/{movie_id}")
    suspend fun fetchMovieDetailsById(
        @Path("movie_id") movieId: Int): Response<MovieDetailsResponse?>

    @GET("movie/{movie_id}/similar")
    suspend fun fetchSimilarMoviesById(
        @Path("movie_id") movieId: Int,
    ): Response<MoviesResponse?>

    @GET("movie/{movie_id}/credits")
    suspend fun fetchMovieCreditsById(
        @Path("movie_id") movieId: Int,
    ): Response<MovieCreditResponse?>
}
