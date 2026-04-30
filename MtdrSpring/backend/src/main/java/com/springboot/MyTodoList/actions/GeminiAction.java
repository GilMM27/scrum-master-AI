package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.service.GeminiTools;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GeminiAction extends BotActionBase {
    private static final Logger logger = LoggerFactory.getLogger(GeminiAction.class);
    private final ChatClient chatClient;

    public GeminiAction(ChatClient.Builder chatClientBuilder, GeminiTools geminiTools) {
        this.chatClient = chatClientBuilder
                .defaultTools(geminiTools)
                .build();
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
            String result = chatClient.prompt(prompt).call().content();
            BotHelper.sendMessageToTelegram(chatId, result, BotHelper.getTelegramClient());
        } catch (Exception e) {
            logger.error("Error calling Gemini service via Spring AI", e);
            BotHelper.sendMessageToTelegram(chatId, "Sorry, I encountered an error while talking to Gemini.", BotHelper.getTelegramClient());
        }
        return BotState.IDLE;
    }
}
