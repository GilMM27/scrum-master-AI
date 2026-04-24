package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Component
public class StartAction extends BotActionBase {

    @Override
    public BotState getState() {
        return BotState.START;
    }

    @Override
    public boolean canHandle(Update update) {
        if(!update.getMessage().hasText()){
            return false;
        }
        String messageText = update.getMessage().getText();
        return messageText.equals(BotCommands.START_COMMAND.getCommand());
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        TelegramClient client = BotHelper.getTelegramClient();
        
        String welcomeMessage = BotMessages.HELLO_MYTODO_BOT.getMessage();
        BotHelper.sendMessageToTelegram(chatId, welcomeMessage, client);
        return BotState.IDLE;
    }
}