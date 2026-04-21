package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
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
        
        String welcomeMessage = "👋 *Welcome to Scrum Master AI!*\n\n"
            + "I'm your Scrum Assistant bot. Here's what I can do for you:\n\n"
            + "*Available Commands:*\n\n"
            + "• `/start` - Show this welcome message\n"
            + "• `/login <phone>` - Link your Telegram ID with your account\n"
            + "• `/tasks` - View and manage your tasks\n"
            + "• `/gtask` <name> <hr>- Generate new tasks \n\n"
            + "_Note: Use /login first to link your account and access all the different functions._";

        

        BotHelper.sendMessageToTelegram(chatId, welcomeMessage, client);
        return BotState.IDLE;
    }
}