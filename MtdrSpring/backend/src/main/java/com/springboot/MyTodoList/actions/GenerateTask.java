package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.TaskAssignments;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GenerateTask extends BotActionBase {

    private final Map<Long, Integer> userSteps = new ConcurrentHashMap<>();
    private final Map<Long, Tasks> pendingTasks = new ConcurrentHashMap<>();

    @Override
    public BotState getState() {
        return BotState.GENERATE_TASK;
    }

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        String messageText = update.getMessage().getText();
        return messageText.startsWith(BotCommands.GENERATE_TASK.getCommand());
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        TelegramClient client = BotHelper.getTelegramClient();
        String messageText = update.getMessage().getText();
        long telegramId = update.getMessage().getFrom().getId();

        if (messageText.equalsIgnoreCase("/cancel")) {
            cleanup(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Task creation cancelled.", client);
            return BotState.IDLE;
        }

        int step = userSteps.getOrDefault(chatId, 0);

        switch (step) {
            case 0:
                return startTaskCreation(chatId, client);
            case 1:
                return handleTitle(chatId, messageText, client);
            case 2:
                return handleDescription(chatId, messageText, client);
            case 3:
                return handleHours(chatId, messageText, telegramId, client);
            default:
                cleanup(chatId);
                return BotState.IDLE;
        }
    }

    private BotState startTaskCreation(long chatId, TelegramClient client) {
        BotHelper.sendMessageToTelegram(chatId, "📝 Let's create a new task. Please enter the TITLE of the task:\n(Type /cancel to abort)", client);
        userSteps.put(chatId, 1);
        pendingTasks.put(chatId, new Tasks());
        return BotState.GENERATE_TASK;
    }

    private BotState handleTitle(long chatId, String title, TelegramClient client) {
        if (title.startsWith("/")) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Invalid title. Please enter a valid text for the title:", client);
            return BotState.GENERATE_TASK;
        }
        Tasks task = pendingTasks.get(chatId);
        if (task == null) {
            return startTaskCreation(chatId, client);
        }
        task.setTitle(title);
        BotHelper.sendMessageToTelegram(chatId, "✅ Title set. Now, please provide a DESCRIPTION for the task:", client);
        userSteps.put(chatId, 2);
        return BotState.GENERATE_TASK;
    }

    private BotState handleDescription(long chatId, String description, TelegramClient client) {
        if (description.startsWith("/")) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Invalid description. Please enter valid text or /cancel:", client);
            return BotState.GENERATE_TASK;
        }
        Tasks task = pendingTasks.get(chatId);
        if (task == null) {
            return startTaskCreation(chatId, client);
        }
        task.setDescription(description);
        BotHelper.sendMessageToTelegram(chatId, "✅ Description set. How many HOURS will it take? (Enter a number between 1 and 4):", client);
        userSteps.put(chatId, 3);
        return BotState.GENERATE_TASK;
    }

    private BotState handleHours(long chatId, String hoursText, long telegramId, TelegramClient client) {
        int hours;
        try {
            hours = Integer.parseInt(hoursText.trim());
        } catch (NumberFormatException e) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Please enter a valid number for hours (e.g., 2):", client);
            return BotState.GENERATE_TASK;
        }

        if (hours <= 0 || hours > 4) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Due to Oracle restrictions (advices), tasks should not be larger than 4hr. Please enter a value between 1 and 4:", client);
            return BotState.GENERATE_TASK;
        }

        Tasks task = pendingTasks.get(chatId);
        if (task == null) {
            return startTaskCreation(chatId, client);
        }
        task.setStoryPoints(hours);
        task.setStatus(TaskStatus.TO_DO);

        Users user = usersRepository.findByTelegramId(telegramId).orElse(null);

        if (user != null) {
            BotHelper.sendMessageToTelegram(chatId, "⏳ Saving task...", client);
            task = tasksRepository.save(task);
            
            TaskAssignments assignment = new TaskAssignments(task.getTaskId(), user.getUserId());
            taskAssignmentsRepository.save(assignment);

            String successMessage = "✅ Task Created and Assigned Successfully!\n\n"
                + "Title: " + task.getTitle() + "\n"
                + "Description: " + task.getDescription() + "\n"
                + "Expected Time: " + task.getStoryPoints() + " hr\n\n"
                + "Now you need to activate it with /tasks.";

            BotHelper.sendMessageToTelegram(chatId, successMessage, client);
            cleanup(chatId);
            return BotState.IDLE;
        } else {
            BotHelper.sendMessageToTelegram(chatId, "❌ Error: User not found. Verify you have logged in with /login", client);
            cleanup(chatId);
            return BotState.IDLE;
        }
    }

    private void cleanup(long chatId) {
        userSteps.remove(chatId);
        pendingTasks.remove(chatId);
    }
}
