package org.wpk.chat.controller;

import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.wpk.chat.ali.TongYiChat;
import org.wpk.chat.entity.AiChatQwenRecord;
import org.wpk.chat.service.IAiChatQwenRecordService;
import org.wpk.chat.vo.AiChatParamVo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping("/ai/chat")
public class AiChatController {
    @Autowired
    private TongYiChat tongYiChat;
    @Autowired
    private IAiChatQwenRecordService aiChatQwenRecordService;

    @PostMapping("/tyqw")
    public String tyqw(@RequestBody AiChatParamVo paramVo) throws IOException {
        String chatId = paramVo.getChatId();
        String content = paramVo.getContent();
        if (StringUtils.isBlank(content)) {
            return null;
        }
        // entity存储
        List<AiChatQwenRecord> recordList = new ArrayList<>();
        // 消息api请求
        List<Message> messages = new ArrayList<>();

        // 查询之前的记录
        List<AiChatQwenRecord> list = aiChatQwenRecordService.lambdaQuery()
                .eq(AiChatQwenRecord::getChatId, paramVo.getChatId())
                .list();
        AtomicLong seq = new AtomicLong(getSeq(list));
        if (CollectionUtils.isEmpty(list)) {
            // todo 首次确定system
            Message systemMessage = new Message();
            systemMessage.setRole(Role.SYSTEM.getValue());
            systemMessage.setContent("Java开发人员");
            messages.add(systemMessage);

            // 会话第一次没有消息, 添加system
            AiChatQwenRecord systemChatRecord = aiChatQwenRecordService.buildChatRecord(chatId, "", Role.SYSTEM, "You are a helpful assistant.", seq.getAndIncrement());
            recordList.add(systemChatRecord);
        } else {
            list.forEach(t -> {
                Message message = new Message();
                message.setRole(t.getRole());
                message.setContent(paramVo.getContent());
                messages.add(message);
            });
        }

        // api调用
        String result = tongYiChat.tyqwHttp(messages);

        CompletableFuture.runAsync(() -> {
            // 添加本次提问和答复的消息
            AiChatQwenRecord userChatRecord = aiChatQwenRecordService.buildChatRecord(chatId, "", Role.USER, paramVo.getContent(), seq.getAndIncrement());
            AiChatQwenRecord aiChatRecord = aiChatQwenRecordService.buildChatRecord(chatId, "", Role.ASSISTANT, result, seq.getAndIncrement());
            recordList.add(userChatRecord);
            recordList.add(aiChatRecord);
            aiChatQwenRecordService.saveBatch(recordList);
        });
        return result;
    }

    private long getSeq(List<AiChatQwenRecord> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 1;
        } else {
            AiChatQwenRecord aiChatQwenRecord = list.get(0);
            if (Objects.nonNull(aiChatQwenRecord)) {
                return aiChatQwenRecord.getSeq() + 1;
            }
        }
        throw new RuntimeException("消息异常");
    }


}
