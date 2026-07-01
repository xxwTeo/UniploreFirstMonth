package com.xxw.collection;


public class MyLruCache {
    private static class Node{
        String key;
        String value;
        Node prev, next;

        Node(String key, String value){
            this.key = key;
            this.value = value;
        }
    }

    private final MyHashMap<String, Node> hashMap;
    private final MySkipList skipList;

    private final int capacity;
    private final Node tail;
    private final Node head;

    public MyLruCache(int capacity, MySkipList skipList) {
        this.capacity = capacity;
        this.hashMap = new MyHashMap<>();
        this.skipList = skipList;

        this.tail = new  Node(null, null);
        this.head  = new  Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    //读取的同时将node置于链表头
    public String get(String key){
        Node node = hashMap.get(key);
        if (node == null)
            return null;

        moveToHead(node);
        return node.value;
    }

    public void put(String key, String value){
        if (key == null) return;

        Node node = hashMap.get(key);

        if (node != null){
            //已存在->更改value,同步跳表,移到头部
            node.value = value;
            skipList.put(key, value);
            moveToHead(node);
        }else{
            //不存在 -> 创建新节点，移到头部，更新HashMap与跳表
            Node newNode = new Node(key, value);
            addToHead(newNode);
            hashMap.put(key, newNode);
            skipList.put(key, value);

            //超容量 -> 淘汰
            if (hashMap.size() > capacity){
                removeTail();
            }
        }
    }

    public void remove(String key){
        Node node = hashMap.get(key);
        if (node != null){
            removeNode(node);
            hashMap.remove(key);
        }
        skipList.remove(key);
    }

    public int size(){
        return hashMap.size();
    }


    // ===== 内部链表操作=====
    private void addToHead(Node node){
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node){
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }

    private void moveToHead(Node node){
        removeNode(node);
        addToHead(node);
    }

    //淘汰最久未使用
    private void removeTail(){
        Node oldNode = tail.prev;
        removeNode(oldNode);
        hashMap.remove(oldNode.key);
        skipList.remove(oldNode.key);
    }
}
