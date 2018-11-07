package com.example.jesusarias.moviesapp.DataClasses

import android.content.res.Resources
import com.bumptech.glide.load.engine.Resource

data class Movie(val vote_count: Int, val id: Int, val video:Boolean, val vote_average:Float,
                 val title: String, val popularity:Double, val poster_path: String,
                 val original_language:String, val original_title:String,
                 val genre_ids: IntArray, val backdrop_path: String, val adult:Boolean,
                 val overview: String, val release_date: String)

            /*
            Movie structure from API
            {
                "vote_count": 1510,
                "id": 335983,
                "video": false,
                "vote_average": 6.6,
                "title": "Venom",
                "popularity": 401.758,
                "poster_path": "/2uNW4WbgBXL25BAbXGLnLqX71Sw.jpg",
                "original_language": "en",
                "original_title": "Venom",
                "genre_ids": [
                878,
                28,
                80,
                28,
                27
                ],
                "backdrop_path": "/VuukZLgaCrho2Ar8Scl9HtV3yD.jpg",
                "adult": false,
                "overview": "When Eddie Brock acquires the powers of a symbiote, he will have to release his alter-ego \"Venom\" to save his life.",
                "release_date": "2018-10-03"
            }
            */
