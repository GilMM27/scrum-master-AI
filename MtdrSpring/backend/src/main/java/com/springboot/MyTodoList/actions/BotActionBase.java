package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.service.GeminiService;
import com.springboot.MyTodoList.service.ToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class BotActionBase implements BotAction {

    protected ToDoItemService todoService;
    protected GeminiService geminiService;

    @Autowired
    public void setServices(ToDoItemService todoService, GeminiService geminiService) {
        this.todoService = todoService;
        this.geminiService = geminiService;
    }
}