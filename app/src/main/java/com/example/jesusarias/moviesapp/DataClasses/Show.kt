package com.example.jesusarias.moviesapp.DataClasses

data class Show(val original_name: String, val genre_ids: IntArray, val name:String,
                val popularity: Float, val origin_country: Array<String>,
                val vote_count: Int, val first_air_date: String, val backdrop_path: String,
                val original_language: String, val id: Int, val vote_average: Float,
                val overview: String, val poster_path: String)

