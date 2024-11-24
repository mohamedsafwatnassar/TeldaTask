package com.telda.movieApp.di

import android.content.Context
import androidx.room.Room
import com.telda.movieApp.BuildConfig
import com.telda.movieApp.data.api.ApiService
import com.telda.movieApp.data.local.dao.MovieDao
import com.telda.movieApp.data.local.database.MovieDatabase
import com.telda.movieApp.data.repository.MovieRepositoryImpl
import com.telda.movieApp.data.repository.MovieRepositoryImplLocal
import com.telda.movieApp.domain.repository.LocalMovieRepository
import com.telda.movieApp.domain.repository.MovieRepository
import com.telda.movieApp.domain.usecase.FetchPopularMoviesUseCase
import com.telda.movieApp.domain.usecase.MarkMoviesWithWatchlistStatusUseCase
import com.telda.movieApp.domain.usecase.SearchMoviesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Interceptor to add the private-key header
        val headerInterceptor = Interceptor { chain ->
            val original: Request = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.ACCESS_TOKEN}")

            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)  // Logging interceptor for debugging
            .addInterceptor(headerInterceptor)   // Add the private-key header
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideMoviesRepository(apiService: ApiService): MovieRepository {
        return MovieRepositoryImpl(apiService)
    }


    // Provide the application context as a dependency
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }


    @Provides
    @Singleton
    fun provideMovieDatabase(appContext: Context): MovieDatabase {
        return Room.databaseBuilder(
            appContext,
            MovieDatabase::class.java,
            "movie_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDatabase): MovieDao {
        return database.movieDao()
    }


    @Provides
    @Singleton
    fun provideLocalMoviesRepository(dao: MovieDao): LocalMovieRepository {
        return MovieRepositoryImplLocal(dao)
    }

    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }


    @Provides
    @Singleton
    fun provideMarkMoviesWithWatchlistStatusUseCase(
        localRepository: LocalMovieRepository
    ): MarkMoviesWithWatchlistStatusUseCase {
        return MarkMoviesWithWatchlistStatusUseCase(localRepository)
    }

    @Provides
    @Singleton
    fun provideSearchMoviesUseCase(
        movieRepository: MovieRepository,
        markMoviesWithWatchlistStatusUseCase: MarkMoviesWithWatchlistStatusUseCase,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO // Provide default dispatcher
    ): SearchMoviesUseCase {
        return SearchMoviesUseCase(
            repository = movieRepository,
            markMoviesWithWatchlistStatusUseCase = markMoviesWithWatchlistStatusUseCase,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideFetchPopularMoviesUseCase(
        movieRepository: MovieRepository,
        markMoviesWithWatchlistStatusUseCase: MarkMoviesWithWatchlistStatusUseCase,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO // Provide default dispatcher
    ): FetchPopularMoviesUseCase {
        return FetchPopularMoviesUseCase(
            repository = movieRepository,
            markMoviesWithWatchlistStatusUseCase = markMoviesWithWatchlistStatusUseCase,
            ioDispatcher = ioDispatcher
        )
    }


}
