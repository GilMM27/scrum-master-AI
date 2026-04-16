package com.springboot.MyTodoList.actions;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BotActionRegistry {

    private final List<BotAction> actions;

    public BotActionRegistry(List<BotAction> actions) {
        this.actions = actions;
    }

    public List<BotAction> getActions() {
        return actions;
    }

    public BotAction resolve(Update update) {
        return actions.stream()
            .filter(a -> a.canHandle(update))
            .findFirst()
            .orElse(null);
    }

    public BotAction resolveCallback(String callbackData) {
        return actions.stream()
            .filter(a -> a.canHandleCallback(callbackData))
            .findFirst()
            .orElse(null);
    }
}