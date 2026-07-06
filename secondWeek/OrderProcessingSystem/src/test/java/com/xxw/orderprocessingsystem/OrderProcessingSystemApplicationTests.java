package com.xxw.orderprocessingsystem;

import com.xxw.orderprocessingsystem.datastructure.OrderCache;
import com.xxw.orderprocessingsystem.datastructure.OrderLinkedList;
import com.xxw.orderprocessingsystem.datastructure.RecentOrderCircularQueue;
import com.xxw.orderprocessingsystem.entity.Order;
import com.xxw.orderprocessingsystem.service.OrderSystem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class OrderProcessingSystemApplicationTests {

    @Test
    void contextLoads() {
        // 1. 创建缓存（容量设为 5）
        OrderCache<String, Order> orderCache = new OrderCache<>(5);

        // 2. 创建用户订单索引（HashMap 实现）
        Map<String, OrderLinkedList> userOrderIndex = new HashMap<>();

        // 3. 创建最近订单滑动窗口（容量为 5）
        RecentOrderCircularQueue recentOrders = new RecentOrderCircularQueue(5);

        // ✅ 正确实例化 OrderSystem
        OrderSystem system = new OrderSystem(orderCache, userOrderIndex, recentOrders);
        // 模拟下单
        system.createOrder(new Order("ORD001", "USER_A", 29900, 1, LocalDateTime.now()));
        system.createOrder(new Order("ORD002", "USER_B", 15000, 1, LocalDateTime.now()));
        system.createOrder(new Order("ORD003", "USER_A", 89900, 1, LocalDateTime.now()));
        system.createOrder(new Order("ORD004", "USER_C", 45000, 1, LocalDateTime.now()));
        system.createOrder(new Order("ORD005", "USER_B", 20000, 1, LocalDateTime.now()));

        // 测试查询
        System.out.println("ORD003: " + system.getOrderByOrderId("ORD003"));
        System.out.println("USER_A的订单: " + system.getOrdersByUserId("USER_A"));
        System.out.println("最近订单: " + system.getRecentOrders());

        // 测试金额区间查询
        System.out.println("10000~30000元的订单: " + system.getOrdersByAmountRange(10000, 30000));

        // 测试取消订单
        system.cancelOrder("ORD001");
        System.out.println("取消后USER_A的订单: " + system.getOrdersByUserId("USER_A"));
    }

}
