package com.guerraana.filmesapi.api

import com.guerraana.filmesapi.domain.MovieService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/movies")
class MovieController(private val service: MovieService) {

    @GetMapping
    suspend fun list(): List<MovieSummaryResponse> = service.list().map { it.toSummary() }

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Int): MovieDetailResponse = service.get(id).toDetail()
}
