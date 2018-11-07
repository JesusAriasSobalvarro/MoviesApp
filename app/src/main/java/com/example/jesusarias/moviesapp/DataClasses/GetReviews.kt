package com.example.jesusarias.moviesapp.DataClasses

data class GetReviews(val id: Int, val page: Int, val results: ArrayList<Review>, val total_pages: Int,
                      val total_results: Int)