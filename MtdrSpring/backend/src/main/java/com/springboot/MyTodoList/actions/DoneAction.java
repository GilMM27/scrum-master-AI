package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class DoneAction extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(DoneAction.class);

    @Override
    public boolean canHandle(String messageText) {
        return messageText.contains(BotLabels.DONE.getLabel());
    }

    @Override
    public void handle(String messageText, long chatId, TelegramClient client) {
        String done = messageText.substring(0, messageText.indexOf(BotLabels.DASH.getLabel()));
        Integer id = Integer.valueOf(done);

        try {
            ToDoItem item = todoService.getToDoItemById(id);
            item.setDone(true);
            todoService.updateToDoItem(id, item);
            BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DONE.getMessage(), client);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }
}