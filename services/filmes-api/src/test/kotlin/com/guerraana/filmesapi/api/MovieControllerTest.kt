package com.guerraana.filmesapi.api

import com.guerraana.filmesapi.domain.Movie
import com.guerraana.filmesapi.domain.MovieNotFoundException
import com.guerraana.filmesapi.domain.MovieService
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import kotlin.test.Test

@WebFluxTest(MovieController::class)
@Import(MovieControllerTest.Config::class, ApiExceptionHandler::class)
class MovieControllerTest(@Autowired private val client: WebTestClient, @Autowired private val service: MovieService) {

    class Config {
        @Bean
        fun movieService(): MovieService = mock()
    }

    private val movie = Movie(1, "Interstellar", "http://poster", "overview", LocalDate.of(2014, 11, 6))

    @Test
    fun `GET v1 movies returns summaries`() = runTest {
        whenever(service.list()).thenReturn(listOf(movie))

        client.get().uri("/v1/movies").exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(1)
            .jsonPath("$[0].title").isEqualTo("Interstellar")
            .jsonPath("$[0].posterUrl").isEqualTo("http://poster")
            .jsonPath("$[0].overview").doesNotExist()
    }

    @Test
    fun `GET v1 movies id returns detail`() = runTest {
        whenever(service.get(1)).thenReturn(movie)

        client.get().uri("/v1/movies/1").exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.overview").isEqualTo("overview")
            .jsonPath("$.releaseDate").isEqualTo("2014-11-06")
    }

    @Test
    fun `GET v1 movies unknown id returns problem 404`() = runTest {
        whenever(service.get(99)).thenThrow(MovieNotFoundException(99))

        client.get().uri("/v1/movies/99").exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType("application/problem+json")
            .expectBody().jsonPath("$.title").isEqualTo("Movie not found")
    }
}
