package com.xxw.coedit.service.impl;

import com.xxw.coedit.service.LockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LockServiceImpl implements LockService {
    private final RedissonClient redisson;
    private final String PREFIX = "file:lock:";

    /**
     * 尝试加锁（非阻塞）
     * @param fileId 文件ID
     * @param userId 当前用户ID（Redisson 内部通过线程ID保证归属）
     */
    @Override
    public boolean tryLock(Long fileId, Long userId) {
        RLock lock = redisson.getLock(PREFIX + fileId);
        try {
            return lock.tryLock(0, 30, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
