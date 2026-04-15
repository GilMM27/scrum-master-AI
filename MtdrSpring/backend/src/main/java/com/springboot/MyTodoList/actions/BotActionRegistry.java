package com.springboot.MyTodoList.actions;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BotActionRegistry {

    private final List<BotAction> actions;
    private final ElseAction elseAction;

    public BotActionRegistry(List<BotAction> actions, ElseAction elseAction) {
        this.actions = actions;
        this.elseAction = elseAction;
    }

    public BotAction resolve(String messageText) {
        return actions.stream()
            .filter(a -> a.canHandle(messageText))
            .filter(a -> !(a instanceof ElseAction)) 
            .findFirst()
            .orElse(elseAction);
    }
}