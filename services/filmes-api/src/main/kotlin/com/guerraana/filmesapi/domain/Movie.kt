package com.guerraana.filmesapi.domain

import java.time.LocalDate

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val overview: String?,
    val releaseDate: LocalDate?,
)
