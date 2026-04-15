package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class BotActionBase implements BotAction {

    protected UsersRepository usersRepository;

    @Autowired
    public void setServices(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
}