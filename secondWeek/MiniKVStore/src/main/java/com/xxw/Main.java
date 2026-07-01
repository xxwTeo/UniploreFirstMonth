package com.xxw;

import com.xxw.engine.KVEngine;
import com.xxw.engine.KVEngineImpl;

import java.util.Map;


public class Main {
    public static void main(String[] args) {
        KVEngine engine = new KVEngineImpl(3);

        // === put三个内容 ===
        engine.put("apple", "10");
        engine.put("banana", "20");
        engine.put("cherry", "30");

        System.out.println("get apple = " + engine.get("apple"));

        // === 触发淘汰 ===
        engine.put("date", "40");  // 淘汰最久没用的（banana）

        System.out.println("get banana = " + engine.get("banana"));
        System.out.println("get cherry = " + engine.get("cherry"));
        System.out.println("get date = " + engine.get("date"));

        // === 有序输出全部keys）===
        System.out.println("========================");
        System.out.println("有序输出全部keys");
        System.out.println("keys = " + engine.keys());

        // === 范围查询 ===
        engine.put("apricot", "15");
        engine.put("blueberry", "25");
        Map<String, String> range = engine.rangeQuery("a", "c");
        System.out.println("========================");
        System.out.println("范围查询a-c");
        System.out.println("range [a, c] = " + range);

        // === size ===
        System.out.println("========================");
        System.out.println("查询size");
        System.out.println("size = " + engine.size());

        // === 删除 ===
        engine.remove("date");
        System.out.println("========================");
        System.out.println("删除cherry后, size = " + engine.size());
        System.out.println("删除后，keys = " + engine.keys());
    }
}