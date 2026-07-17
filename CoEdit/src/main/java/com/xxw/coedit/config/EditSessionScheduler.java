package com.xxw.coedit.config;
import com.xxw.coedit.service.EditSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class EditSessionScheduler {

    private final EditSessionService editSessionService;

    /**
     * 每15秒清理一次过期编辑会话
     * 兜底处理心跳超时/异常断线的僵尸会话，释放文件编辑权
     */
    @Scheduled(fixedRate = 15000)
    public void cleanExpiredSessions() {
        editSessionService.cleanExpiredSessions();
    }
}