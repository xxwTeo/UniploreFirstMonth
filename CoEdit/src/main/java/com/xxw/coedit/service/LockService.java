package com.xxw.coedit.service;

public interface LockService {
    // 对目标进行加锁
    boolean tryLock(Long fileId, Long userId);
    // 对目标进行释放锁
    void releaseLock(Long fileId, Long userId);
}
