package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class LoginAction extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        String messageText = update.getMessage().getText();
        return messageText.startsWith(BotCommands.LOGIN.getCommand());
    }

    @Override
    public void handle(Update update, long chatId, TelegramClient client) {
        String messageText = update.getMessage().getText();
        String[] parts = messageText.split(" ", 2);
        
        if (parts.length < 2) {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Please provide your phone number.\nUsage: /login <phone>\nExample: /login 6181234567", client);
            return;
        }

        String phoneNumber = parts[1].trim();

        if (phoneNumber.length() < 10) {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Invalid phone number. Please enter a 10-digit number without country code.", client);
            return;
        }

        Users user = usersRepository.findByCellPhone(phoneNumber).orElse(null);

        if (user != null) {
            user.setTelegramId(update.getMessage().getFrom().getId());
            usersRepository.save(user);
            
            String successMessage = "✅ Login successful! Welcome " + user.getUsername() + "!\n\n"
                + "Your Telegram ID has been linked to your account.";

            BotHelper.sendMessageToTelegram(chatId, successMessage, client);
        } else {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Phone number not found. Please contact your manager to register your number with your user.", client);
        }
    }
}