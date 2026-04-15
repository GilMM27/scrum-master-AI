package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class DeleteAction extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(DeleteAction.class);

    @Override
    public boolean canHandle(String messageText) {
        return messageText.contains(BotLabels.DELETE.getLabel());
    }

    @Override
    public void handle(String messageText, long chatId, TelegramClient client) {
        String delete = messageText.substring(0, messageText.indexOf(BotLabels.DASH.getLabel()));
        Integer id = Integer.valueOf(delete);

        try {
            todoService.deleteToDoItem(id);
            BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DELETED.getMessage(), client);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }
}