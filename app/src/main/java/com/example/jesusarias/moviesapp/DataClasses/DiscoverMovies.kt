package com.example.jesusarias.moviesapp.DataClasses

import com.example.jesusarias.moviesapp.DataClasses.Movie

data class DiscoverMovies(val page:Int, val total_results:Int, val total_pages:Int,
                          val results: ArrayList<Movie>)