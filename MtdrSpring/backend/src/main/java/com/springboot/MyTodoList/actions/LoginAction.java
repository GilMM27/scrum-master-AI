package com.springboot.MyTodoList.actions;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class LoginAction extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);


    @Override
    public BotState getState() {
        return BotState.LOGIN;
    }

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage()) {
            return false;
        }
        
        if (update.getMessage().hasContact()) {
            return true;
        }
        
        if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            return messageText.startsWith(BotCommands.LOGIN.getCommand());
        }
        
        return false;
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        long telegramId = update.getMessage().getFrom().getId();
        TelegramClient client = BotHelper.getTelegramClient();
        
        if (update.getMessage().hasContact()) {
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            return processPhoneNumber(phoneNumber, chatId, telegramId, client);
        }
        
        return showContactKeyboard(chatId, client);
    }

    private BotState showContactKeyboard(long chatId, TelegramClient client) {
        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
            .resizeKeyboard(true)
            .oneTimeKeyboard(true)
            .keyboardRow(new KeyboardRow(
                KeyboardButton.builder()
                    .text("📱 Share Phone Number")
                    .requestContact(true)
                    .build()
            ))
            .build();
        
        BotHelper.sendMessageToTelegram(chatId, 
            "Please tap the button below to share your phone number:", client, keyboard);
        return BotState.LOGIN;
    }

    private BotState processPhoneNumber(String phoneNumber, long chatId, long telegramId, TelegramClient client) {
        String cleanPhone = phoneNumber.replaceAll("[^0-9]", "");
        BotHelper.sendMessageToTelegram(chatId, "is this you phone? " + cleanPhone, client);
        if (cleanPhone.length() < 10) {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Invalid phone number. Please try again.", client);
            return BotState.LOGIN;
        }

        Users user = usersRepository.findByCellPhone(cleanPhone).orElse(null);

        if (user != null) {
            user.setTelegramId(telegramId);
            usersRepository.save(user);
            
            String successMessage = "✅ Login successful! Welcome " + user.getUsername() + "!\n\n"
                + "Your Telegram ID has been linked to your account.";

            BotHelper.sendMessageToTelegram(chatId, successMessage, client, null);
        } else {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Phone number not found. Please contact your manager to register your number.", client);
            return BotState.LOGIN;
        }
        
        return BotState.IDLE;
    }
}
