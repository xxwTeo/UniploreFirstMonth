package com.xxw.coedit.service;
public interface LockService {
    boolean tryLock(Long fileId, Long userId);
}