package org.wpk.chat.service.impl;

import com.alibaba.dashscope.common.Role;
import org.wpk.chat.entity.AiChatQwenRecord;
import org.wpk.chat.mapper.AiChatQwenRecordMapper;
import org.wpk.chat.service.IAiChatQwenRecordService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Objects;

/**
 * @Description: 通义千问记录
 * @Author:
 * @Date: 2024-08-23 17:16:05
 * @Version: V1.0
 */
@Service
public class AiChatQwenRecordServiceImpl extends ServiceImpl<AiChatQwenRecordMapper, AiChatQwenRecord> implements IAiChatQwenRecordService {

    @Override
    public AiChatQwenRecord buildChatRecord(String chatId, String userId, Role role, String content, long seq) {
        if (Objects.isNull(role)) {
            throw new RuntimeException();
        }
        int type = -1;
        switch (role) {
            case USER:
                type = 1;
                break;
            case BOT:
            case SYSTEM:
            case ASSISTANT:
            case ATTACHMENT:
                type = 2;
                break;
        }
        AiChatQwenRecord aiChatRecord = new AiChatQwenRecord()
                .setUserId(userId)
                .setChatId(chatId)
                .setRole(role.getValue())
                .setContent(content)
                .setType(type)
                .setSeq(seq);
        return aiChatRecord;
    }
}
