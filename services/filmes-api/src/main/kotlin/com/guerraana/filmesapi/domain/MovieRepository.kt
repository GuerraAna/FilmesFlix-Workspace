package com.guerraana.filmesapi.domain

interface MovieRepository {
    suspend fun findAll(): List<Movie>
    suspend fun findById(id: Int): Movie?
}
