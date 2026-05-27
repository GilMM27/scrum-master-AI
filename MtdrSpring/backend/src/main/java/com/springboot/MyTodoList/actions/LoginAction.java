package com.springboot.MyTodoList.actions;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class LoginAction extends BotActionBase {



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
                    .text("📱 Compartir Número de Teléfono")
                    .requestContact(true)
                    .build()
            ))
            .build();
        
        BotHelper.sendMessageToTelegram(chatId, 
            "Por favor, toca el botón de abajo para compartir tu número de teléfono:", client, keyboard);
        return BotState.LOGIN;
    }

    private BotState processPhoneNumber(String phoneNumber, long chatId, long telegramId, TelegramClient client) {
        String cleanPhone = phoneNumber.replaceAll("[^0-9]", "");
        BotHelper.sendMessageToTelegram(chatId, "¿es este tu teléfono? " + cleanPhone, client);
        if (cleanPhone.length() < 10) {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Número de teléfono inválido. Por favor intenta de nuevo.", client);
            return BotState.LOGIN;
        }

        Users user = usersRepository.findByCellPhone(cleanPhone).orElse(null);

        if (user != null) {
            user.setTelegramId(telegramId);
            usersRepository.save(user);
            
            String successMessage = "✅ ¡Inicio de sesión exitoso! ¡Bienvenido " + user.getUsername() + "!\n\n"
                + "Tu ID de Telegram ha sido vinculado a tu cuenta.";

            BotHelper.sendMessageToTelegram(chatId, successMessage, client, null);
        } else {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Número de teléfono no encontrado. Por favor contacta a tu manager para registrar tu número.", client);
            return BotState.LOGIN;
        }
        
        return BotState.IDLE;
    }
}
