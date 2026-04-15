package com.springboot.MyTodoList.actions;

import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface BotAction {
    boolean canHandle(String messageText);
    void handle(String messageText, long chatId, TelegramClient client);
}