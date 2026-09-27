package com.guerraana.filmesapi.domain

import org.springframework.stereotype.Service

@Service
class MovieService(private val repository: MovieRepository) {
    suspend fun list(): List<Movie> = repository.findAll()

    suspend fun get(id: Int): Movie = repository.findById(id) ?: throw MovieNotFoundException(id)
}
