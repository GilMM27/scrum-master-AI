package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.states.BotState;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotAction {
    BotState getState();
    boolean canHandle(Update update);
    BotState handle(Update update);
    
    default boolean canHandleCallback(Update update) {
        return false;
    }
    
    default BotState handleCallback(Update update) {
        return BotState.IDLE;
    }

    default void reset(long chatId) {
        // Default implementation does nothing
    }

    default boolean canHandleWithMessageCheck(Update update) {
        if (!update.hasMessage()) {
            return false;
        }
        return canHandle(update);
    }
}