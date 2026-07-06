package com.xxw.orderprocessingsystem.datastructure;

/**
 * 简化版HashMap —— 订单缓存
 *
 * 核心特性：
 * - 链地址法解决哈希冲突
 * - 容量始终为2的幂次方
 * - 负载因子0.75自动扩容
 * - 支持null key
 */
public class OrderCache<K, V> {
    /** 哈希桶中的节点 */
    private static class Entry<K ,V>{
        K key;
        V value;
        Entry<K, V> next;
        int hash;

        Entry(K key, V value, int hash, Entry<K, V> next){
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }
    }

    private Entry<K, V>[] table;
    private int size;                //现有元素个数
    private int capacity;            //容量
    private float loadFactor;        //负载因子
    private int threshold;           // 扩容阈值 = capacity * loadFactor

    private static final int DEAULT_CAPACITY = 16;  //默认容量
    private static final float DEFAULT_LOAD_FACTOR = 0.75F; //默认负载因子


    //无参构造
    @SuppressWarnings("unchecked")
    public OrderCache(){
        this.capacity = DEAULT_CAPACITY;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (DEAULT_CAPACITY * DEFAULT_LOAD_FACTOR);
        this.table = (Entry<K, V>[]) new Entry[DEAULT_CAPACITY];
        this.size = 0;
    }

    //有参构造
    @SuppressWarnings("unchecked")
    public OrderCache(int initialCapacity){
        this.capacity = tableSizeFor(initialCapacity);
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (this.capacity * DEFAULT_LOAD_FACTOR);
        this.table = (Entry<K, V>[]) new Entry[this.capacity];
        this.size = 0;
    }

    // ==================== 核心方法 ====================

    /**
     * 存入键值对
     */
    public void put(K key, V value){
        //处理 null key, 统一放到table[0]
        if (key == null) {
            putForNullKey(value);
            return;
        }

        int hash = key.hashCode();
        int index = indexFor(hash, capacity);

        //检查是否已经有相应结点，有就覆盖
        Entry<K, V> e = table[index];
        while (e != null){
            if (e.key.equals(key)) {
                e.value = value;
                return;
            }
            e = e.next;
        }

        //没相应结点,新建一个并头插法
        table[index] = new Entry<>(key, value, hash, table[index]);
        size++;

        //检查是否需要扩容
        if (size > threshold) {
            resize(capacity * 2);
        }
    }

    /**
     * 按key查询
     */
    public V get(K key){
        // null key
        if (key == null) {
            return getForNullKey();
        }

        int hash = key.hashCode();
        int index = indexFor(hash, capacity);

        //检查是否已经有相应结点,有就返回
        Entry<K, V> e = table[index];
        while (e != null) {
            if (e.key.hashCode() == hash && (e.key.equals(key))) {
                return e.value;
            }
            e = e.next;
        }

        //没有
        return null;
    }

    /**
     * 按key删除
     */
    public V remove(K key){
        if (key == null) {
            return removeForNullKey();
        }

        int hash = key.hashCode();
        int index = indexFor(hash, capacity);

        Entry<K, V> e = table[index];
        Entry<K, V> prev = null;

        //检查是否已经有相应结点,有就删除
        while (e != null) {
            if (e.key.hashCode() == hash && (e.key.equals(key))) {
                if (prev == null) {
                    table[index] = e.next;  //删除头节点
                } else {
                    prev.next = e.next;
                }
                size--;
                return e.value;
            }

            prev = e;
            e = e.next;
        }

        return null;
    }

    public int size() {
        return size;
    }



    // ==================== 私有辅助方法 ====================

    /** 处理null key */
    private void putForNullKey(V value){
        Entry<K, V> e = table[0];
        while (e != null) {
            //找到key == null 的节点，修改返回
            if (e.key == null) {
                e.value = value;
                return;
            }
            e = e.next;
        }

        //没找到，新建一个节点头插
        table[0] = new Entry<>(null,value,0, table[0]);
        size++;

        //判断是否需要扩容
        if (size > threshold){
            resize(2 * capacity);
        }
    }


    /** 获取null key */
    private V getForNullKey() {
        Entry<K, V> e = table[0];
        while (e != null) {
            //找到了
            if (e.key == null) {
                return e.value;
            }
            e = e.next;
        }
        //没找到
        return null;
    }

    /** 删除null key */
    private V removeForNullKey() {
        Entry<K, V> prev = table[0];
        //table[0] 为空
        if (prev == null) {
            return null;
        }

        //头节点就是null key
        if (prev.key == null) {
            table[0] = prev.next;
            size--;
            return prev.value;
        }

        //头节点不是null key
        while (prev.next != null) {
            //找到了，保存返回
            if (prev.next.key == null) {
                Entry<K, V> removed = prev.next;
                prev.next = prev.next.next;
                return removed.value;
            }
            prev = prev.next;
        }
        //没找到
        return null;
    }


    /** 返回大于等于cap的最小的2的幂次方 */
    private static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>>1;
        n |= n >>>2;
        n |= n >>>4;
        n |= n >>>8;
        n |= n >>>16;
        return (n < 0) ? 1 : (n > Integer.MAX_VALUE) ? Integer.MAX_VALUE : n + 1;
    }


    /** 计算数组下标 */
    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }


    /** 扩容 + rehash */
    private void resize(int newCapacity){
        //复制老table
        Entry<K, V>[] oldTable = table;
        int oldCapacity = capacity;

        //创建table
        capacity = newCapacity;
        table = (Entry<K, V>[]) new Entry[capacity];
        threshold = (int) (newCapacity * loadFactor);
        size = 0;

        //重新散列所有元素
        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> e = oldTable[i];
            while (e != null) {
                Entry<K, V> next = e.next;
                int newIndex = (e.key == null) ? 0 : indexFor(e.key.hashCode(), newCapacity);
                //头插法
                e.next = table[newIndex];
                table[newIndex] = e;
                e = next;
                size++;
            }
        }
    }
}
