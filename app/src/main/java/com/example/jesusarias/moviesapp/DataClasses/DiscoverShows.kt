package com.example.jesusarias.moviesapp.DataClasses

import com.example.jesusarias.moviesapp.DataClasses.Show

data class DiscoverShows(val page:Int, val total_results:Int, val total_pages:Int,
                         val results: ArrayList<Show>)