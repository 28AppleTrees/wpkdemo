package org.wpk.chat.service;

import com.alibaba.dashscope.common.Role;
import org.springframework.stereotype.Service;
import org.wpk.chat.entity.AiChatQwenRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 通义千问记录
 * @Author:
 * @Date: 2024-08-23 17:16:05
 * @Version: V1.0
 */
public interface IAiChatQwenRecordService extends IService<AiChatQwenRecord> {

    AiChatQwenRecord buildChatRecord(String chatId, String userId, Role role, String content, long seq);
}
