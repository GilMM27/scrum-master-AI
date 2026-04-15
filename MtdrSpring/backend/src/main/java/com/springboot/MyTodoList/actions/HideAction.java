package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class HideAction extends BotActionBase {

    @Override
    public boolean canHandle(String messageText) {
        return messageText.equals(BotCommands.HIDE_COMMAND.getCommand()) 
            || messageText.equals(BotLabels.HIDE_MAIN_SCREEN.getLabel());
    }

    @Override
    public void handle(String messageText, long chatId, TelegramClient client) {
        BotHelper.sendMessageToTelegram(chatId, BotMessages.BYE.getMessage(), client);
    }
}