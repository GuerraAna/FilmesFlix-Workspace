package com.guerraana.filmesapi.domain

class MovieNotFoundException(id: Int) : RuntimeException("Movie $id not found")
