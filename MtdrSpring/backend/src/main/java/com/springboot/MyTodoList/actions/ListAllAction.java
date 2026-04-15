package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListAllAction extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(ListAllAction.class);

    @Override
    public boolean canHandle(String messageText) {
        return messageText.equals("/todolist") 
            || messageText.equals(BotLabels.LIST_ALL_ITEMS.getLabel())
            || messageText.equals(BotLabels.MY_TODO_LIST.getLabel());
    }

    @Override
    public void handle(String messageText, long chatId, TelegramClient client) {
        logger.info("todoSvc: " + todoService);
        List<ToDoItem> allItems = todoService.findAll();
        
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
            .resizeKeyboard(true)
            .oneTimeKeyboard(false)
            .selective(true)
            .build();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow mainScreenRowTop = new KeyboardRow();
        mainScreenRowTop.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreenRowTop);

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(BotLabels.ADD_NEW_ITEM.getLabel());
        keyboard.add(firstRow);

        KeyboardRow myTodoListTitleRow = new KeyboardRow();
        myTodoListTitleRow.add(BotLabels.MY_TODO_LIST.getLabel());
        keyboard.add(myTodoListTitleRow);

        List<ToDoItem> activeItems = allItems.stream()
            .filter(item -> !item.isDone())
            .collect(Collectors.toList());

        for (ToDoItem item : activeItems) {
            KeyboardRow currentRow = new KeyboardRow();
            currentRow.add(item.getDescription());
            currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
            keyboard.add(currentRow);
        }

        List<ToDoItem> doneItems = allItems.stream()
            .filter(item -> item.isDone())
            .collect(Collectors.toList());

        for (ToDoItem item : doneItems) {
            KeyboardRow currentRow = new KeyboardRow();
            currentRow.add(item.getDescription());
            currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.UNDO.getLabel());
            currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
            keyboard.add(currentRow);
        }

        KeyboardRow mainScreenRowBottom = new KeyboardRow();
        mainScreenRowBottom.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreenRowBottom);

        keyboardMarkup.setKeyboard(keyboard);

        BotHelper.sendMessageToTelegram(chatId, BotLabels.MY_TODO_LIST.getLabel(), client, keyboardMarkup);
    }
}