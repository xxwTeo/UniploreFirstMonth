package com.xxw.orderprocessingsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String orderId;             //订单Id,唯一标识
    private String userId;              //用户Id
    private long amount;                //订单金额
    private Integer status;             //订单状态，0-未支付，1-已支付，2-已取消，3-已完成
    private LocalDateTime creatTime;    //下单时间

    public String toString(){
        return "Order{orderId='" + orderId + "', userId='" + userId
                + "', amount=" + amount + ", status=" + status + "}";
    }
}



