package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.time.OffsetDateTime;

@Component
public class ElseAction extends BotActionBase {

    @Override
    public boolean canHandle(String messageText) {
        return true;
    }

    @Override
    public void handle(String messageText, long chatId, TelegramClient client) {


        BotHelper.sendMessageToTelegram(chatId, BotMessages.COMMAND_NOT_FOUND.getMessage(), client, null);
    }
}