package com.springboot.MyTodoList.actions;
import com.springboot.MyTodoList.model.TaskAssignments;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.repository.TaskAssignmentsRepository;
import com.springboot.MyTodoList.util.BotHelper;

import okhttp3.internal.concurrent.Task;

import com.springboot.MyTodoList.util.BotCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class GenerateTask extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(GenerateTask.class);


    @Override
    public boolean canHandle(Update update) {

        String messageText = update.getMessage().getText();
        return messageText.startsWith(BotCommands.GENERATE_TASK.getCommand());
    }

    @Override
    public void handle(Update update, long chatId, TelegramClient client) {
        String messageText = update.getMessage().getText();
        String[] parts = messageText.split(" ", 3);
        
        if (parts.length < 3) {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Please provide the name of your Task in a single argument and the time just in integert hr\n "+
                     "/gtask <nombre_sin_espacios> <tiempo en hr>", client);
            return;
        }

        int time = Integer.parseInt(parts[2]);

        if (time> 4) {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ Due to oracle restrictions (advices), the tasks should not be larger than 4hr", client);
            return;
        }

        Users user = usersRepository.findByTelegramId(update.getMessage().getFrom().getId()).orElse(null);

        if (user != null) {
            Tasks tsk = new Tasks();
            tsk.setTitle(parts[1]);
            tsk.setStoryPoints(time);
            tsk.setStatus(TaskStatus.TO_DO);
            BotHelper.sendMessageToTelegram(chatId, "Innitating save. "+ parts[1] + " " + time, client);
            tsk = tasksRepository.save(tsk);
            BotHelper.sendMessageToTelegram(chatId, "✅ Task Saved Successfully.", client);

            TaskAssignments n = new TaskAssignments(tsk.getTaskId(), user.getUserId());
            taskAssignmentsRepository.save(n);


            BotHelper.sendMessageToTelegram(chatId, "✅ Automatically assigned to yourself", client);
            
            String successMessage = "✅ Creation successful! Task " + tsk.getTitle() + "!\n\n"
                + "Now you need to activate it with /tasks.";

            BotHelper.sendMessageToTelegram(chatId, successMessage, client);
        } else {
            BotHelper.sendMessageToTelegram(chatId, 
                "❌ An error occurred during the creating of the new task. verify you have login with /login", client);
        }
    }
}