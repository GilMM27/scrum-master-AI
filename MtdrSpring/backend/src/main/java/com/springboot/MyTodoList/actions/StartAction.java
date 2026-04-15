package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class StartAction extends BotActionBase {

    @Override
    public boolean canHandle(String messageText) {
        return messageText.equals(BotCommands.START_COMMAND.getCommand()) 
            || messageText.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel());
    }

    @Override
    public void handle(String messageText, long chatId, TelegramClient client) {
        BotHelper.sendMessageToTelegram(chatId, BotMessages.HELLO_MYTODO_BOT.getMessage(), client, 
            ReplyKeyboardMarkup.builder()
                .keyboardRow(new KeyboardRow(BotLabels.LIST_ALL_ITEMS.getLabel(), BotLabels.ADD_NEW_ITEM.getLabel()))
                .keyboardRow(new KeyboardRow(BotLabels.SHOW_MAIN_SCREEN.getLabel(), BotLabels.HIDE_MAIN_SCREEN.getLabel()))
                .build()
        );
    }
}