package com.xxw.coedit.service.impl;

import com.xxw.coedit.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements OnlineUserService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ONLINE_KEY = "online:file:";
    private static final long EXPIRE_SECONDS = 60;

    /**
     * 用户进入编辑
     */
    @Override
    public void enterEdit(Long fileId, Long userId) {
        String key = ONLINE_KEY + fileId;
        redisTemplate.opsForZSet()
                .add(key, userId.toString(), System.currentTimeMillis());
        redisTemplate.expire(key, EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 用户退出编辑
     */
    @Override
    public void exitEdit(Long fileId, Long userId) {
        redisTemplate.opsForSet().remove(ONLINE_KEY + fileId, userId.toString());
    }

    /**
     * 获取当前正在编辑的用户
     */
    @Override
    public Set<Object> getEditors(Long fileId) {
        String key = ONLINE_KEY + fileId;
        // 获取 Set 中所有成员
        Set<Object> members = redisTemplate.opsForSet().members(key);
        // 防止 Redis Key 不存在导致的 NPE
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        // 类型转换：Redis(String) → Java(Long)
        return members.stream()
                .map(o -> Long.valueOf(o.toString()))
                .collect(Collectors.toSet());
    }

    /**
     * 清理指定文件的在线编辑状态
     */
    @Override
    public Boolean clearFileEditors(Long fileId) {
        String key = ONLINE_KEY + fileId;
        if (fileId == 0) {
            return false;
        }
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.FALSE.equals(hasKey)) {
            return false;
        }
        return redisTemplate.delete(key);
    }
}
