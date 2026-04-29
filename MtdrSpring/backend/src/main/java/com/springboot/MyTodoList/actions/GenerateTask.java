package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.*;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class GenerateTask extends BotActionBase {

    private final Map<Long, TaskFlowState> userFlowStates = new ConcurrentHashMap<>();
    private final Map<Long, Tasks> pendingTasks = new ConcurrentHashMap<>();

    private enum TaskFlowState {
        SELECTING_PROJECT,
        ENTERING_TITLE,
        ENTERING_DESCRIPTION,
        SELECTING_PRIORITY,
        ENTERING_STORY_POINTS,
        ENTERING_EXPECTED_HOURS
    }

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
    public boolean canHandleCallback(Update update) {
        if (!update.hasCallbackQuery()) return false;
        String callbackData = update.getCallbackQuery().getData();
        return callbackData.startsWith("gentask_proj_") || callbackData.startsWith("gentask_prio_");
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        TelegramClient client = BotHelper.getTelegramClient();
        String messageText = update.getMessage().getText();

        if (messageText.equalsIgnoreCase("/cancel")) {
            cleanup(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Task creation cancelled.", client);
            return BotState.IDLE;
        }

        TaskFlowState state = userFlowStates.get(chatId);

        if (state == null) {
            return startTaskCreation(chatId, update.getMessage().getFrom().getId(), client);
        }

        switch (state) {
            case ENTERING_TITLE:
                return handleTitle(chatId, messageText, client);
            case ENTERING_DESCRIPTION:
                return handleDescription(chatId, messageText, client);
            case ENTERING_STORY_POINTS:
                return handleStoryPoints(chatId, messageText, client);
            case ENTERING_EXPECTED_HOURS:
                return handleExpectedHours(chatId, messageText, client);
            default:
                cleanup(chatId);
                return BotState.IDLE;
        }
    }

    @Override
    public BotState handleCallback(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        String callbackData = update.getCallbackQuery().getData();
        TelegramClient client = BotHelper.getTelegramClient();

        TaskFlowState state = userFlowStates.get(chatId);
        if (state == null) return BotState.IDLE;

        if (callbackData.startsWith("gentask_proj_")) {
            return handleProjectSelection(chatId, messageId, callbackData.substring(13), client);
        } else if (callbackData.startsWith("gentask_prio_")) {
            return handlePrioritySelection(chatId, messageId, callbackData.substring(13), client);
        }

        return BotState.GENERATE_TASK;
    }

    private BotState startTaskCreation(long chatId, long telegramId, TelegramClient client) {
        Users user = usersRepository.findByTelegramId(telegramId).orElse(null);
        if (user == null) {
            BotHelper.sendMessageToTelegram(chatId, "❌ User not found. Please log in first.", client);
            return BotState.IDLE;
        }

        List<ProjectMembers> memberships = projectMembersRepository.findByUserId(user.getUserId());
        if (memberships.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "❌ You are not a member of any project. Join a project first.", client);
            return BotState.IDLE;
        }

        List<Projects> projects = memberships.stream()
                .map(m -> projectsRepository.findById(m.getProjectId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (projects.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Projects not found.", client);
            return BotState.IDLE;
        }

        String text = "📝 Select a Project for the new task:";
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (Projects project : projects) {
            rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                    .text(project.getName())
                    .callbackData("gentask_proj_" + project.getProjectId().toString())
                    .build()));
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        BotHelper.sendMessageWithInlineKeyboard(chatId, text, markup, client);

        userFlowStates.put(chatId, TaskFlowState.SELECTING_PROJECT);
        pendingTasks.put(chatId, new Tasks());
        return BotState.GENERATE_TASK;
    }

    private BotState handleProjectSelection(long chatId, int messageId, String projectIdStr, TelegramClient client) {
        UUID projectId = UUID.fromString(projectIdStr);
        Tasks task = pendingTasks.get(chatId);
        if (task == null) return BotState.IDLE;

        task.setProjectId(projectId);
        userFlowStates.put(chatId, TaskFlowState.ENTERING_TITLE);

        BotHelper.editMessageText(chatId, messageId, "✅ Project selected. Now, please enter the TITLE of the task:", client);
        return BotState.GENERATE_TASK;
    }

    private BotState handleTitle(long chatId, String title, TelegramClient client) {
        if (title.startsWith("/")) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Invalid title. Please enter text:", client);
            return BotState.GENERATE_TASK;
        }
        Tasks task = pendingTasks.get(chatId);
        if (task == null) return BotState.IDLE;

        task.setTitle(title);
        userFlowStates.put(chatId, TaskFlowState.ENTERING_DESCRIPTION);
        BotHelper.sendMessageToTelegram(chatId, "✅ Title set. Now, please provide a DESCRIPTION for the task:", client);
        return BotState.GENERATE_TASK;
    }

    private BotState handleDescription(long chatId, String description, TelegramClient client) {
        if (description.startsWith("/")) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Invalid description. Please enter text:", client);
            return BotState.GENERATE_TASK;
        }
        Tasks task = pendingTasks.get(chatId);
        if (task == null) return BotState.IDLE;

        task.setDescription(description);
        userFlowStates.put(chatId, TaskFlowState.SELECTING_PRIORITY);

        String text = "✅ Description set. Select the PRIORITY:";
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (TaskPriority priority : TaskPriority.values()) {
            rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                    .text(priority.name())
                    .callbackData("gentask_prio_" + priority.name())
                    .build()));
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        BotHelper.sendMessageWithInlineKeyboard(chatId, text, markup, client);

        return BotState.GENERATE_TASK;
    }

    private BotState handlePrioritySelection(long chatId, int messageId, String priorityStr, TelegramClient client) {
        TaskPriority priority = TaskPriority.valueOf(priorityStr);
        Tasks task = pendingTasks.get(chatId);
        if (task == null) return BotState.IDLE;

        task.setPriority(priority);
        userFlowStates.put(chatId, TaskFlowState.ENTERING_STORY_POINTS);

        BotHelper.editMessageText(chatId, messageId, "✅ Priority set to " + priority + ". How many STORY POINTS will it have? (Enter a number between 1 and 13):", client);
        return BotState.GENERATE_TASK;
    }

    private BotState handleStoryPoints(long chatId, String spText, TelegramClient client) {
        int storyPoints;
        try {
            storyPoints = Integer.parseInt(spText.trim());
        } catch (NumberFormatException e) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Please enter a valid number for story points (e.g., 2):", client);
            return BotState.GENERATE_TASK;
        }

        if (storyPoints <= 0 || storyPoints > 13) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Story points should be between 1 and 13. Please try again:", client);
            return BotState.GENERATE_TASK;
        }

        Tasks task = pendingTasks.get(chatId);
        if (task == null) return BotState.IDLE;

        task.setStoryPoints(storyPoints);
        userFlowStates.put(chatId, TaskFlowState.ENTERING_EXPECTED_HOURS);
        BotHelper.sendMessageToTelegram(chatId, "✅ Story points set. Now, how many EXPECTED HOURS will it take? (Enter a number between 1 and 4):", client);
        return BotState.GENERATE_TASK;
    }

    private BotState handleExpectedHours(long chatId, String hoursText, TelegramClient client) {
        int hours;
        try {
            hours = Integer.parseInt(hoursText.trim());
        } catch (NumberFormatException e) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Please enter a valid number for expected hours (e.g., 2):", client);
            return BotState.GENERATE_TASK;
        }

        if (hours <= 0 || hours > 4) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Expected hours should be between 1 and 4. Please try again:", client);
            return BotState.GENERATE_TASK;
        }

        Tasks task = pendingTasks.get(chatId);
        if (task == null) return BotState.IDLE;

        task.setExpectedHours(hours);
        task.setStatus(TaskStatus.TO_DO);

        BotHelper.sendMessageToTelegram(chatId, "⏳ Saving task...", client);
        tasksService.createTask(mapToCreateRequest(task));

        String successMessage = "✅ Task Created Successfully!\n\n"
                + "Project ID: " + task.getProjectId() + "\n"
                + "Title: " + task.getTitle() + "\n"
                + "Priority: " + task.getPriority() + "\n"
                + "Story Points: " + task.getStoryPoints() + "\n"
                + "Expected Time: " + task.getExpectedHours() + " hr";

        BotHelper.sendMessageToTelegram(chatId, successMessage, client);
        cleanup(chatId);
        return BotState.IDLE;
    }

    private com.springboot.MyTodoList.dto.CreateTaskRequest mapToCreateRequest(Tasks task) {
        com.springboot.MyTodoList.dto.CreateTaskRequest request = new com.springboot.MyTodoList.dto.CreateTaskRequest();
        request.setProjectId(task.getProjectId());
        request.setTitle(task.getTitle());
        request.setDescription(task.getDescription());
        request.setStatus(task.getStatus());
        request.setPriority(task.getPriority());
        request.setStoryPoints(task.getStoryPoints());
        request.setExpectedHours(task.getExpectedHours());
        return request;
    }

    @Override
    public void reset(long chatId) {
        cleanup(chatId);
    }

    private void cleanup(long chatId) {
        userFlowStates.remove(chatId);
        pendingTasks.remove(chatId);
    }
}
