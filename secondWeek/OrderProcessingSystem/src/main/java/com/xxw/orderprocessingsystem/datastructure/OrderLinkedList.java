package com.xxw.orderprocessingsystem.datastructure;
import com.xxw.orderprocessingsystem.entity.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * 单向链表
 * 用于储存单个用户所有订单信息
 *
 *核心操作：
 * - addOrder：头插法，O(1)
 * - removeOrderById：遍历查找并删除，O(n)
 * - findByOrderId：遍历查找，O(n)
 * - getAllOrders：遍历收集所有节点，O(n)
 */
public class OrderLinkedList {
    /*链表节点*/
    private static class Node{
        Order order;
        Node next;

        Node(Order order){
            this.order = order;
        }
    }

    private Node head;  //头链表
    private int size;   //链表长度

    /**
     * 添加订单(头插法)
     * @param order
     */
    public void addOrder(Order order){
        Node node = new Node(order);
        node.next = head;
        head = node;
        size++;
    }

    /**
     * 根据orderId遍历查找并删除，O(n)
     * @param orderId
     */
    public Order removeOrderById(String orderId){
        if (orderId == null){
            return null;
        }

        //如果删除的是head
        if (head.order.getOrderId().equals(orderId)){
            Order removed = head.order;
            head = head.next;
            size--;
            return removed;
        }

        //遍历查找并删除
        Node prev = head;
        while (prev.next != null){
            if (prev.next.order.getOrderId().equals(orderId)){
                Order removed = prev.next.order;
                prev.next = prev.next.next;
                size--;
                return removed;
            }
            prev = prev.next;
        }

        //遍历完没有找到
        return null;
    }

    /**
     * 按订单ID查找
     * @param orderId
     * @return
     */
    public Order findByOrderId(String orderId){
        if (orderId == null){
            return null;
        }

        //遍历寻找
        Node cur = head;
        while (cur != null){
            if (cur.order.getOrderId().equals(orderId)){
                return cur.order;
            }
        }

        //遍历完没有找到
        return null;
    }

    /**
     * 获取该链表用户所有订单
      * @return
     */
    public List<Order> getAllOrders(){
        List<Order> result = new ArrayList<>(size);

        //遍历添加
        Node curr = head;
        while (curr != null){
            result.add(curr.order);
            curr = curr.next;
        }

        return result;
    }

    /**
     * 返回链表长度
     * @return
     */
    public int size(){
        return size;
    }
}
