package org.wpk.chat.ali;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.tokenizers.Tokenization;
import com.alibaba.dashscope.tokenizers.TokenizationResult;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wpk.chat.ali.dto.TongYiQianWenParamDto;
import org.wpk.chat.config.TongYiProperties;
import org.wpk.utils.HttpClientUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class TongYiChat {
    @Autowired
    private HttpClientUtil httpClientUtil;
    @Autowired
    private TongYiProperties tongYiProperties;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public String tyqwHttp(List<Message> messages) {
        // 业务逻辑
        log.info("通义千问调用");

        TongYiQianWenParamDto paramDto = new TongYiQianWenParamDto();
        paramDto.setModel(Tokenization.Models.QWEN_TURBO);
        paramDto.setStream(false);
        messages.forEach(t -> {
            paramDto.addMessage(t.getRole(), t.getContent());
        });
        String dataJson = JSONObject.toJSONString(paramDto, JSONWriter.Feature.WriteMapNullValue);
//        String dataJson = JSONObject.toJSONString(paramDto, JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + tongYiProperties.getApikey());
        headerMap.put("Content-Type", "application/json");
        String result = httpClientUtil.postString(tongYiProperties.getUrl(), dataJson, headerMap);
        JSONObject jsonObject = JSONObject.parseObject(result);


        CompletableFuture.runAsync(() -> {
            // todo tokens记录
            TokenizationResult tokenizer = tokenizer(messages);
            log.info("tokens记录:{}", JsonUtils.toJson(tokenizer));
        }, threadPoolExecutor);
        return parseResult(result);
    }

    public String tyqwSdk(List<Message> messages) {
        GenerationParam param = createGenerationParam(messages, true);
        try {
            GenerationResult result = callWithMessages(param);
            Message message = parseResult(result);
            return message.getContent();
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (InputRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    private TokenizationResult tokenizer(List<Message> messages) {
        Tokenization tokenizer = new Tokenization();
        GenerationParam param = GenerationParam.builder()
                .model(Tokenization.Models.QWEN_TURBO)
                .messages(messages)
                .apiKey(tongYiProperties.getApikey())
                .build();
        TokenizationResult tokenResult = null;
        try {
            tokenResult = tokenizer.call(param);
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (InputRequiredException e) {
            throw new RuntimeException(e);
        }
        return tokenResult;
    }

    private String parseResult(String result) {
        return result;
    }

    private Message parseResult(GenerationResult result) {
        return result.getOutput().getChoices().get(0).getMessage();
    }

    public static Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }

    private static GenerationParam createGenerationParam(List<Message> messages, boolean isStream) {
        GenerationParam.GenerationParamBuilder<?, ?> paramBuilder = GenerationParam.builder()
                .model(Tokenization.Models.QWEN_TURBO)
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8);
        paramBuilder.incrementalOutput(isStream);
        return paramBuilder
                .build();
    }

    private static GenerationResult callWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

    private static String streamCallWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Flowable<GenerationResult> result = gen.streamCall(param);
        StringBuilder fullContent = new StringBuilder();
        result.blockingForEach(t -> handleGenerationResult(t, fullContent));
        return fullContent.toString();
    }

    private static String streamCallWithCallback(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException, InterruptedException {
        Generation gen = new Generation();
        StringBuilder fullContent = new StringBuilder();
        Semaphore semaphore = new Semaphore(0);
        gen.streamCall(param, new ResultCallback<GenerationResult>() {
            @Override
            public void onEvent(GenerationResult message) {
                // 流处理事件
                handleGenerationResult(message, fullContent);
            }

            @Override
            public void onComplete() {
                log.info("Completed");
                semaphore.release();
            }

            @Override
            public void onError(Exception e) {
                log.error("Exception occurred: {}", e.getMessage());
                semaphore.release();
            }
        });
        semaphore.acquire();
        return fullContent.toString();
    }

    private static void handleGenerationResult(GenerationResult message, StringBuilder fullContent) {
        fullContent.append(message.getOutput().getChoices().get(0).getMessage().getContent());
    }
}
