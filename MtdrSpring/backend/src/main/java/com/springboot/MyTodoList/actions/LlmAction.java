package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class LlmAction extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(LlmAction.class);

    @Override
    public boolean canHandle(String messageText) {
        return messageText.contains(BotCommands.LLM_REQ.getCommand());
    }

    @Override
    public void handle(String messageText, long chatId, TelegramClient client) {
        logger.info("Calling LLM");
        String prompt = "Dame los datos del clima en mty";
        String out = "<empty>";
        try {
            out = geminiService.generateText(prompt);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }

        BotHelper.sendMessageToTelegram(chatId, "LLM: " + out, client, null);
    }
}