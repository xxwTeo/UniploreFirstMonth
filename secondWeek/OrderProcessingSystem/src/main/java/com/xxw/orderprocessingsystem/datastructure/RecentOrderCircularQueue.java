package com.xxw.orderprocessingsystem.datastructure;
import com.xxw.orderprocessingsystem.entity.Order;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 循环队列 —— 最近N笔订单的滑动窗口
 *
 * 核心思想：
 * - 用数组存储元素
 * - head指针指向队首（最老的元素）
 * - tail指针指向队尾的下一个空位
 * - 当 (tail + 1) % capacity == head 时，队列满
 * - 满了之后不再拒绝新元素，而是覆盖最老的（head前移）
 */
public class RecentOrderCircularQueue {

    private Order[] buffer;
    private int head;       // 队首索引
    private int tail;       // 队尾下一个位置(%capacity)
    @Getter
    private int count;      // 当前元素个数
    private int capacity;   // 最大容量

    //有参构造(capacity)
    @SuppressWarnings("unchecked")
    public RecentOrderCircularQueue(int capacity){
        this.capacity = capacity;
        this.buffer = new Order[capacity];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
    }

    /**
     * 入队 —— 满了就覆盖最老的
     * @param order
     */
    public void offer(Order order){
        buffer[tail] = order;
        tail = (tail + 1) % capacity;

        if (count < capacity){
            count++;
        }else {
            //队列满了，head也要移动
            head = (head + 1) % capacity;
        }
    }

    /**
     * 出队 —— 取出最老的订单
     * @return 队首订单，队列为空返回null
     */
    public Order poll(){
        if (count == 0) return null;

        Order order = buffer[head];
        buffer[head] = null;
        head = (head + 1) % capacity;
        count--;
        return order;
    }

    /**
     * 获取所有最近的订单（按时间从老到新排列）
     * @return
     */
    public List<Order> getAllRecent(){
        List<Order> result = new ArrayList<>(count);
        //循环遍历队列并添加到result
        for (int i = 0; i < count; i++) {
            int index = (head + i) % capacity;
            result.add(buffer[index]);
        }
        return result;
    }

    //判断队列是否为空
    public boolean isEmpty() {
        return count == 0;
    }

    //判断队列容量是否为空
    public boolean isFull() {
        return count == capacity;
    }

}
