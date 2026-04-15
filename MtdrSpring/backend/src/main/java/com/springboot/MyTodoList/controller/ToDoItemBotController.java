package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.actions.BotAction;
import com.springboot.MyTodoList.actions.BotActionRegistry;
import com.springboot.MyTodoList.config.BotProps;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.BotCommands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class ToDoItemBotController implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);

    private final TelegramClient telegramClient;
    private final BotProps botProps;
    private final BotActionRegistry actionRegistry;
    private final UsersRepository usersRepository;

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Override
    public String getBotToken() {
        if (telegramBotToken != null && !telegramBotToken.trim().isEmpty()) {
            return telegramBotToken;
        } else {
            return botProps.getToken();
        }
    }

    public ToDoItemBotController(BotProps bp, BotActionRegistry actionRegistry, UsersRepository usersRepository) {
        this.botProps = bp;
        this.actionRegistry = actionRegistry;
        this.usersRepository = usersRepository;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (messageText.startsWith(BotCommands.LOGIN.getCommand())) {
            BotAction action = actionRegistry.resolve(update);
            action.handle(update, chatId, telegramClient);
            return;
        }

        long telegramId = update.getMessage().getFrom().getId();
        if (usersRepository.findByTelegramId(telegramId).isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, BotMessages.UNAUTHORIZED.getMessage(), telegramClient);
            return;
        }

        BotAction action = actionRegistry.resolve(update);
        if (action != null) {
            action.handle(update, chatId, telegramClient);
        }
		BotHelper.sendMessageToTelegram(chatId, BotMessages.COMMAND_NOT_FOUND.getMessage(), telegramClient);
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getFrom().getId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        for (BotAction action : actionRegistry.getActions()) {
            if (action.canHandleCallback(callbackData)) {
                action.handleCallback(callbackData, chatId, messageId, telegramClient);
                return;
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}