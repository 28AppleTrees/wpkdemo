package org.wpk.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Description: 通义千问记录
 * @Author:
 * @Date: 2024-08-23 17:16:05
 * @Version: V1.0
 */
@Data
@TableName("ai_chat_qwen_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AiChatQwenRecord {

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**用户id*/
	private String userId;
	private String chatId;
	/**角色*/
	private String role;
	/**内容*/
	private String content;
    /**消息序号*/
	private Long seq;
	/**消息类型,1-用户消息;2-ai答复消息*/
	private Integer type;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime msgTime;
}
