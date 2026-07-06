package com.xxw.orderprocessingsystem.algorithm;


import com.xxw.orderprocessingsystem.entity.Order;

/**
 * 快速排序 —— 按订单金额排序
 *
 * 场景：金额区间查询前，先对所有订单按金额排序
 */
public class QuickSortUtil {

    /**
     * 对外接口
     */
    public static void quickSort(Order[] orders, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(orders, left, right);
            quickSort(orders, pivotIndex + 1, right);
            quickSort(orders, left, pivotIndex - 1);
        }
    }


    /**
     * 分区函数（挖坑法）
     * 选择最右元素作为基准，将数组分为两部分：
     * 左边都 <= pivot，右边都 > pivot
     */
    private static int  partition(Order[] orders, int left, int right) {
        long privot = orders[right].getAmount();
        int i = left;   // i 指向"小于等于pivot区域"的末尾

        //遍历分区
        for (int j = left; j < right; j++) {
            if (orders[j].getAmount() <= privot) {
                swap(orders, i, j);
                i++;
            }
        }

        //将privot放于中间
        swap(orders, i, right);
        return i;
    }

    //将 order 交换位置
    private static void swap(Order[] orders, int a, int b) {
        Order temp = orders[a];
        orders[a] = orders[b];
        orders[b] = temp;
    }

}
