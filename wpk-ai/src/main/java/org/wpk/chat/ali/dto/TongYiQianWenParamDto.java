package org.wpk.chat.ali.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 通义千问api请求参数
 */
public class TongYiQianWenParamDto {

    // 请求模型
    private String model;
    // 角色和内容
    private List<Message> messages = new ArrayList<>();
    // 是否流处理
    private boolean stream;

    public TongYiQianWenParamDto() {
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public void addMessage(String role, String content) {
        Message message = new Message();
        message.setRole(role);
        message.setContent(content);
        messages.add(message);
    }

    private static class Message {
        /*
        role
        system: 初始化模型, 确定基本行为或对话背景
        user: 用户消息, 代表真实用户的提问
        assistant: 助手消息, AI助手的回答或反馈
         */
        private String role;
        // 具体内容, 与role对应
        private String content;

        public Message() {
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
