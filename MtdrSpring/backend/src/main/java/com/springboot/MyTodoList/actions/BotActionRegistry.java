package com.springboot.MyTodoList.actions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.springboot.MyTodoList.states.BotState;

@Component
public class BotActionRegistry {

    private final Map<BotState,BotAction> actions;

    public BotActionRegistry(List<BotAction> actions) {
        this.actions = actions.stream()
            .collect(Collectors.toMap(BotAction::getState, a -> a));
    }

    private static final java.util.Set<BotState> UNREGISTERED_USER_STATES = java.util.Set.of(
        BotState.START,
        BotState.LOGIN
    );

    public BotAction getActionByState(BotState bs){
        return actions.get(bs);
    }

    public List<BotAction> getActions() {
        return actions.values().stream().collect(Collectors.toList());
    }

    public List<BotAction> getUnregisteredUserActions() {
        return actions.values().stream()
            .filter(a -> UNREGISTERED_USER_STATES.contains(a.getState()))
            .collect(Collectors.toList());
    }


    public BotAction resolve(Update update) {
        return actions.values().stream()
            .filter(a -> a.canHandleWithMessageCheck(update))
            .findFirst()
            .orElse(null);
    }

    public BotAction resolveCallback(Update update) {
        return actions.values().stream()
            .filter(a -> a.canHandleCallback(update))
            .findFirst()
            .orElse(null);
    }
}