package com.guerraana.filmesapi.infra

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MockMovieRepositoryTest {

    private val objectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    private val repository = MockMovieRepository(objectMapper)

    @Test
    fun `findAll returns movies from the bundled mock`() = runTest {
        val movies = repository.findAll()

        assertTrue(movies.isNotEmpty())
        assertTrue(movies.all { it.id > 0 && it.title.isNotBlank() && it.posterUrl.isNotBlank() })
    }

    @Test
    fun `findById returns the matching movie`() = runTest {
        val first = repository.findAll().first()

        assertEquals(first, repository.findById(first.id))
        assertNotNull(repository.findById(first.id)?.releaseDate)
    }

    @Test
    fun `findById returns null for unknown id`() = runTest {
        assertNull(repository.findById(Int.MIN_VALUE))
    }
}
