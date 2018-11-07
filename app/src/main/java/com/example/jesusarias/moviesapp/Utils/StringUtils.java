package com.example.jesusarias.moviesapp.Utils;

public class StringUtils {

    public String formatDescription(String description){
        int numberWords = 10;
        String[] words = description.split("\\s+");
        String formatDescription = "";

        if(words.length > numberWords){
            for(int i = 0; i < numberWords; i++){
                formatDescription = formatDescription + words[i] + " ";
            }
            formatDescription = formatDescription + "...";
            return formatDescription;
        } else
            return description;
    }

}
