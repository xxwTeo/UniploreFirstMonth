package com.xxw.coedit.service.impl;
import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.common.exceptions.BizException;
import com.xxw.coedit.service.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * 文件编辑分布式锁服务实现
 */
@Service
@RequiredArgsConstructor
public class LockServiceImpl implements LockService {

    private RedisTemplate<String, Object> redisTemplate;

    // 锁默认过期时间
    private static final long LOCK_TTL = 30;
    // Redis Key 前缀，用于区分不同业务的锁
    private static final String LOCK_PREFIX = "lock:file:";

    /**
     * 尝试获取文件编辑锁（互斥锁）
     *
     * @param fileId 文件ID
     * @param userId 当前操作用户ID
     * @return true：成功获取锁
     * @throws BizException 若文件已被其他用户锁定，则抛出无编辑权限异常
     */
    @Override
    public boolean tryLock(Long fileId, Long userId) {
        String key = LOCK_PREFIX + fileId;
        // 尝试在 Redis 中设置锁
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, userId.toString(), LOCK_TTL, TimeUnit.SECONDS);
        // Boolean.TRUE.equals 可安全处理 null，避免 NPE
        if (Boolean.TRUE.equals(success)) {
            // 成功获取锁
            return true;
        }
        // 已经有人持有锁
        throw new BizException(ErrorCode.NO_EDIT_PERMISSION);
    }

    /**
     * 释放文件编辑锁
     *
     * @param fileId 文件ID
     * @param userId 当前操作用户ID
     */
    @Override
    public void releaseLock(Long fileId, Long userId) {
        String key = LOCK_PREFIX + fileId;
        // 查询当前锁的持有者
        Object holder = redisTemplate.opsForValue().get(key);
        // 校验锁是否属于当前用户
        if (holder != null && holder.equals(userId.toString())) {
            // 释放锁
            redisTemplate.delete(key);
        }
    }
}