package com.xxw.orderprocessingsystem.algorithm;

/**
 * 二分查找 —— 在有序数组中查找目标值的索引
 *
 * 前提：数组必须是有序的（升序）
 * 场景：金额区间查询时，快速定位起始和结束位置
 */
public class BinarySearchUtil {
    /**
     * 标准二分查找
     * @param sortedAmounts 已排序的金额数组（升序）
     * @param target 目标金额
     * @return 找到返回索引，未找到返回-1
     */
    public static int binarySearch(long[] sortedAmounts, long target) {
        int left = 0;
        int right = sortedAmounts.length - 1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);     //等价于% 2, 防止溢出
            if (target == sortedAmounts[mid]) {
                return mid;
            } else if (target < sortedAmounts[mid]) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        //没找到
        return -1;
    }

    /**
     * 查找第一个 >= target 的位置（用于区间左端点）
     */
    public static int lowerBound(long[] sortedAmounts, long target) {
        int left = 0;
        int right = sortedAmounts.length ;

        while (left < right) {
            int mid = left + ((right - left) >> 1);
            if (target < sortedAmounts[mid]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    /**
     * 查找第一个 > target 的位置（用于区间右端点）
     */
    public static int upperBound(long[] sortedAmounts, long target) {
        int left = 0, right = sortedAmounts.length;
        while (left < right) {
            int mid = left + ((right - left) >> 1);
            if (sortedAmounts[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
}
