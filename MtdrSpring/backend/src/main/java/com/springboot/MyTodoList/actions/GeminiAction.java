package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.service.GeminiService;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GeminiAction extends BotActionBase {
    private static final Logger logger = LoggerFactory.getLogger(GeminiAction.class);
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public GeminiAction(GeminiService geminiService, ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    @Override
    public BotState getState() {
        return BotState.GEMINI;
    }

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        return update.getMessage().getText().startsWith(BotCommands.LLM_REQ.getCommand());
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String prompt;
        
        if (text.startsWith(BotCommands.LLM_REQ.getCommand())) {
            prompt = text.substring(BotCommands.LLM_REQ.getCommand().length()).trim();
        } else {
            prompt = text;
        }

        if (prompt.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "Please provide a prompt for Gemini.", BotHelper.getTelegramClient());
            return BotState.IDLE;
        }

        return executeGeminiRequest(chatId, prompt);
    }

    public BotState executeGeminiRequest(long chatId, String prompt) {
        try {
            String response = geminiService.generateText(prompt);
            String result = parseGeminiResponse(response);
            BotHelper.sendMessageToTelegram(chatId, result, BotHelper.getTelegramClient());
        } catch (Exception e) {
            logger.error("Error calling Gemini service", e);
            BotHelper.sendMessageToTelegram(chatId, "Sorry, I encountered an error while talking to Gemini.", BotHelper.getTelegramClient());
        }
        return BotState.IDLE;
    }

    private String parseGeminiResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            logger.warn("Failed to parse Gemini response: {}", jsonResponse);
            return "Error parsing response from Gemini.";
        }
    }
}
