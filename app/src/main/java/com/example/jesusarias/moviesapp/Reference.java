package com.example.jesusarias.moviesapp;

public class Reference<T> {
    private T referent;


    public Reference(){
    }
    public Reference(T initialValue){
        referent = initialValue;
    }
    public void set(T newVal) {
        referent = newVal;
    }

    public T get() {
        return referent;
    }
}
