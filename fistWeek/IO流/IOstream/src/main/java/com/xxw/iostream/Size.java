package com.xxw.iostream;

public enum Size {

    SMALL("S"),
    LARGE("X"),
    EXTRA_LARGE("XL");

    final String code;

    Size(String code){
        this.code = code;
    }

    String getSize(){
        return this.code;
    }
}
