package com.guerraana.filmesapi.domain

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class MovieServiceTest {
    private val movie = Movie(1, "Interstellar", "http://poster", "overview", LocalDate.of(2014, 11, 6))
    private val service = MovieService(object : MovieRepository {
        override suspend fun findAll() = listOf(movie)
        override suspend fun findById(id: Int) = movie.takeIf { it.id == id }
    })

    @Test
    fun `list returns all movies`() = runTest {
        assertEquals(listOf(movie), service.list())
    }

    @Test
    fun `get returns movie by id`() = runTest {
        assertEquals(movie, service.get(1))
    }

    @Test
    fun `get throws when movie is missing`() = runTest {
        assertThrows<MovieNotFoundException> { service.get(99) }
    }
}
