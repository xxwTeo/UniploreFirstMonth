package com.xxw.orderprocessingsystem.service;
import com.xxw.orderprocessingsystem.algorithm.BinarySearchUtil;
import com.xxw.orderprocessingsystem.algorithm.QuickSortUtil;
import com.xxw.orderprocessingsystem.datastructure.OrderCache;
import com.xxw.orderprocessingsystem.datastructure.OrderLinkedList;
import com.xxw.orderprocessingsystem.datastructure.RecentOrderCircularQueue;
import com.xxw.orderprocessingsystem.entity.Order;

import java.util.*;

/**
 * 电商订单实时处理系统 —— 集成模块
 */
public class OrderSystem {

    // 订单主缓存（订单ID → Order）
    private final OrderCache<String, Order> orderCache;     //增删改查

    // 用户订单索引（userId → OrderLinkedList）
    private final Map<String, OrderLinkedList> userOrderIndex;  //增删改查

    // 最近订单循环队列
    private final RecentOrderCircularQueue recentOrders;    //入栈出栈，返回所有

    // 最近一次排序后的订单数组（用于金额区间查询）
    private Order[] sortedOrders;
    private boolean sorted = false;

    public OrderSystem(OrderCache<String, Order> orderCache, Map<String, OrderLinkedList> userOrderIndex, RecentOrderCircularQueue recentOrders) {
        this.orderCache = orderCache;
        this.userOrderIndex = userOrderIndex;
        this.recentOrders = recentOrders;
    }


    // ==================== 核心API ====================

    /**
     * 添加订单
     * @param order
     */
    public void createOrder(Order order) {
        // 1.写入主缓存
        orderCache.put(order.getOrderId(), order);
        // 2.维护用户订单索引
        userOrderIndex.computeIfAbsent(order.getUserId(), k -> new OrderLinkedList())
                .addOrder(order);
        // 3.入栈订单窗口
        recentOrders.offer(order);
        // 4.标记排序失效
        sorted = false;
    }

    /**
     * 根据 orderId 查询订单
     * @param orderId
     * @return
     */
    public Order getOrderByOrderId(String orderId) {
        return orderCache.get(orderId);
    }

    /**
     * 删除订单
     * @param orderId
     * @return
     */
    public boolean cancelOrder(String orderId) {
        // 1.写入主缓存
        Order removed = orderCache.remove(orderId);

        // 2.维护用户订单索引
        if (removed != null) {
            OrderLinkedList orderLinkedList = userOrderIndex.get(removed.getUserId());
            if (orderLinkedList != null) {
                Order order = orderLinkedList.removeOrderById(orderId);
                if (order != null) {
                    // 3.标记排序失效
                    sorted = false;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据 userId 查询该用户所有订单
     * @param userId
     * @return
     */
    public List<Order> getOrdersByUserId(String userId) {
        OrderLinkedList list = userOrderIndex.get(userId);

        //没有订单数据，返回空列表
        if (list == null) {
            return Collections.emptyList();
        }

        return list.getAllOrders();
    }

    /**
     * 获取近期所有订单
     * @return
     */
    public List<Order> getRecentOrders() {
        return recentOrders.getAllRecent();
    }

    /**
     * 按金额区间筛选订单
     * @param minAmount
     * @param maxAmount
     * @return
     */
    public List<Order> getOrdersByAmountRange(long minAmount, long maxAmount) {
        ensureSorted();

        //定义一个 amounts 数组存放订单金额
        long[] amounts = new long[sortedOrders.length];
        for (int i = 0; i < sortedOrders.length; i++) {
            amounts[i] = sortedOrders[i].getAmount();
        }

        //找到金额区间的下标
        int left = BinarySearchUtil.lowerBound(amounts, minAmount);
        int right = BinarySearchUtil.upperBound(amounts, maxAmount);

        //封装返回
        List<Order> result = new ArrayList<>(right - left);
        for (int i = left; i < right; i++) {
            result.add(sortedOrders[i]);
        }
        return result;
    }

        /**
         * 确保订单数组已排序
         */
    private void ensureSorted() {
        if (!sorted && orderCache.size() > 0) {
            //从缓存中取出所有订单
            Collection<Order> orders = getAllOrdersFromCache();
            sortedOrders = orders.toArray(new Order[0]);
            QuickSortUtil.quickSort(sortedOrders, 0, sortedOrders.length - 1);
            sorted = true;
        }
    }

    /**
     * 私有方法,缓存所有订单
     * @return
     */
    private Collection<Order> getAllOrdersFromCache() {
        List<Order> all = new ArrayList<>();
        for (OrderLinkedList list : userOrderIndex.values()) {
            all.addAll(list.getAllOrders());
        }
        return all;
    }
}
