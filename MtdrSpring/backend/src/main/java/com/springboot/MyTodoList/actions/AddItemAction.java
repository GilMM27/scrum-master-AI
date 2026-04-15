package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class AddItemAction extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(AddItemAction.class);

    @Override
    public boolean canHandle(String messageText) {
        return messageText.contains(BotCommands.ADD_ITEM.getCommand())
            || messageText.contains(BotLabels.ADD_NEW_ITEM.getLabel());
    }

    @Override
    public void handle(String messageText, long chatId, TelegramClient client) {
        logger.info("Adding item");
        BotHelper.sendMessageToTelegram(chatId, BotMessages.TYPE_NEW_TODO_ITEM.getMessage(), client);
    }
}