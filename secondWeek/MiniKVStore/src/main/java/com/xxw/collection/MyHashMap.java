package com.xxw.collection;

public class MyHashMap<K, V> {
    private static class Node<K, V>{
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value){
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V>[] table;
    private int size;
    private int capacity;
    private static final float LOAD_FACTOR = 0.75f;

    //无参
    @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.capacity = 16;
        this.size = 0;
        table = (Node<K, V>[]) new Node[capacity];
    }

    //有参
    @SuppressWarnings("unchecked")
    public MyHashMap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        table = (Node<K, V>[]) new Node[capacity];
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(K key, V value) {
        //检查是否需要扩容
        if (size >= capacity * LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        Node curr = table[index];

        while (curr != null) {
            //找到key
            if (curr.key.equals(key)) {
                curr.value = value;
                return;
            }

            curr = curr.next;
        }

        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
    }

    public V get(K key) {
        int index = hash(key);
        Node<K, V> curr = table[index];

        while (curr != null) {
            //找到key
            if (curr.key.equals(key)) {
                return curr.value;
            }
            curr = curr.next;
        }

        return null;
    }

    public void remove(K key){
        int index = hash(key);
        Node<K, V> curr = table[index];
        Node<K, V> prev = null;

        while (curr != null){
            //找到key
            if(curr.key.equals(key)){
                if(prev == null){
                    table[index] = curr.next;
                }else{
                    prev.next = curr.next;
                }

                size--;
                return;
            }

            prev = curr;
            curr = curr.next;
        }
    }

    public int size(){
        return size;
    }

    @SuppressWarnings("unchecked")
    private void resize(){
        int oldCapacity = capacity;
        capacity *= 2;
        Node<K, V>[] oldTable = table;
        table = (Node<K, V>[]) new Node[capacity];

        for (int i = 0; i < oldCapacity; i++){
            Node<K, V> curr = oldTable[i];
            while (curr != null){
                Node<K, V> next = curr.next;
                int newIndex = hash(curr.key);
                curr.next = table[newIndex];
                table[newIndex] = curr;
                curr = next;
            }
        }
    }
}
