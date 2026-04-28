package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.actions.BotAction;
import com.springboot.MyTodoList.actions.BotActionRegistry;
import com.springboot.MyTodoList.config.BotProps;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotMessages;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class ScrumMasterBotController implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {


    private final TelegramClient telegramClient;
    private final BotProps botProps;
    private final BotActionRegistry actionRegistry;
    private final UsersRepository usersRepository;
    private final ConcurrentHashMap<Long, BotState> userStates = new ConcurrentHashMap<>();

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

    public ScrumMasterBotController(BotProps bp, BotActionRegistry actionRegistry, UsersRepository usersRepository) {
        this.botProps = bp;
        this.actionRegistry = actionRegistry;
        this.usersRepository = usersRepository;
        telegramClient = new OkHttpTelegramClient(getBotToken());
        BotHelper.setTelegramClient(telegramClient);
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

        if (!update.hasMessage() ) {
            return;
        }

        long chatId = update.getMessage().getChatId();



        long telegramId = update.getMessage().getFrom().getId();
        boolean authentificated = !usersRepository.findByTelegramId(telegramId).isEmpty();



        BotAction action = actionRegistry.resolve(update);
        if(userStates.containsKey(chatId) && userStates.get(chatId) != BotState.IDLE){
            action = actionRegistry.getActionByState(userStates.get(chatId));
           
        }
                
        if (update.getMessage().hasText() && update.getMessage().getText().startsWith(BotCommands.EXIT_TRANSACTION.getCommand())) {
            if (userStates.containsKey(chatId)) {
                BotAction currentAction = actionRegistry.getActionByState(userStates.get(chatId));
                if (currentAction != null) {
                    currentAction.reset(chatId);
                }
                userStates.remove(chatId);
            }
            BotHelper.sendMessageToTelegram(chatId, "Transaction cancelled.", telegramClient);
            return;
        }

        if(action == null){
            action = actionRegistry.getActionByState(BotState.GEMINI);
        }

        if(action == null){
            BotHelper.sendMessageToTelegram(chatId, BotMessages.COMMAND_NOT_FOUND.getMessage(), telegramClient);
            return;
        }
        if (!authentificated && !actionRegistry.getUnregisteredUserActions().contains(action)) {
            BotHelper.sendMessageToTelegram(chatId, BotMessages.UNAUTHORIZED.getMessage(), telegramClient);
            return;
        }

        BotState newState = action.handle(update);
      
        if(newState == BotState.IDLE){
            userStates.remove(chatId);
        }else{
            userStates.put(chatId, newState);
        }
        return;
        
    }

    private void handleCallbackQuery(Update update) {
        for (BotAction action : actionRegistry.getActions()) {
            if (action.canHandleCallback(update)) {
                BotState newState = action.handleCallback(update);
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                userStates.put(chatId, newState);
                return;
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}