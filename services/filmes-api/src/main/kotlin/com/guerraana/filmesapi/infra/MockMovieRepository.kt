package com.guerraana.filmesapi.infra

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.guerraana.filmesapi.domain.Movie
import com.guerraana.filmesapi.domain.MovieRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnProperty("filmes.source", havingValue = "mock", matchIfMissing = true)
class MockMovieRepository(objectMapper: ObjectMapper) : MovieRepository {
    private val movies: List<Movie> =
        ClassPathResource("mock/movies.json").inputStream.use { objectMapper.readValue(it) }

    override suspend fun findAll(): List<Movie> = movies

    override suspend fun findById(id: Int): Movie? = movies.firstOrNull { it.id == id }
}
