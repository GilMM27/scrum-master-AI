package com.springboot.MyTodoList.actions;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface BotAction {
    boolean canHandle(Update update);
    void handle(Update update, long chatId, TelegramClient client);
    
    default boolean canHandleCallback(String callbackData) {
        return false;
    }
    
    default void handleCallback(String callbackData, long chatId, int messageId, TelegramClient client) {
        // Default: do nothing
    }
}