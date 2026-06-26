package com.xxw.reflection;

public class Student {
    Integer id;
    String name;

    Student(){
        this.id = 10;
        this.name = "zs";
    }

    private Student(Integer id, String name){
        this.name = name;
        this.id = id;
    }

    void run(){
        System.out.println("跑步");
    }

    void  eat(String food){
        System.out.println("吃" + food);
    }

    @Override
    public String toString() {
        return new String("id = " + id + "," +"name = " + name);
    }
}
