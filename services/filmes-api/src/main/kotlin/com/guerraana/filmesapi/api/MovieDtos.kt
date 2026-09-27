package com.guerraana.filmesapi.api

import com.guerraana.filmesapi.domain.Movie
import java.time.LocalDate

data class MovieSummaryResponse(val id: Int, val title: String, val posterUrl: String)

data class MovieDetailResponse(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val overview: String?,
    val releaseDate: LocalDate?,
)

fun Movie.toSummary() = MovieSummaryResponse(id, title, posterUrl)

fun Movie.toDetail() = MovieDetailResponse(id, title, posterUrl, overview, releaseDate)
