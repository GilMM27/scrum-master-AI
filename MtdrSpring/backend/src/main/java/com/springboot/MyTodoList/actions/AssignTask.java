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
public class AssignTask extends BotActionBase {

    private final TelegramClient telegramClient;
    private final Map<Long, SessionData> userSessions = new ConcurrentHashMap<>();

    AssignTask(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    private enum FlowType {
        SPRINT, USER
    }

    private enum Step {
        SELECT_FLOW,
        SELECT_PROJECT,
        SELECT_TASK,
        SELECT_TARGET // Sprint or User
    }

    private static class SessionData {
        FlowType flowType;
        Step step;
        UUID projectId;
        UUID taskId;
        UserRole userRole;
        UUID userId;
    }

    @Override
    public BotState getState() {
        return BotState.ASSIGN_TASK;
    }

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        return update.getMessage().getText().startsWith(BotCommands.ASSIGN_TASK.getCommand());
    }

    @Override
    public boolean canHandleCallback(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return update.getCallbackQuery().getData().startsWith("asgn_");
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        TelegramClient client = BotHelper.getTelegramClient();
        long telegramId  = update.getMessage().getFrom().getId();

        if (update.getMessage().getText().equalsIgnoreCase("/cancel")) {
            cleanup(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Assignment cancelled.", client);
            return BotState.IDLE;
        }

        return startFlow(chatId, client, telegramId);
    }

    private BotState startFlow(long chatId, TelegramClient client, long telegramId) {
        SessionData session = new SessionData();
        session.step = Step.SELECT_FLOW;


        Users user = usersRepository.findByTelegramId(telegramId).orElse(null);
        if (user == null) {
            BotHelper.sendMessageToTelegram(chatId, "❌ User not found. Please log in.", client);
            cleanup(chatId);
            return BotState.IDLE;
        }
        session.userId = user.getUserId();
        session.userRole = user.getUserRole();

        userSessions.put(chatId, session);

        String text = "📋 What do you want to assign?";
        List<InlineKeyboardRow> rows = new ArrayList<>();
        if(session.userRole != UserRole.DEVELOPER){
            rows.add(new InlineKeyboardRow(
                    InlineKeyboardButton.builder().text("🏃 Assign to Sprint").callbackData("asgn_flow_sprint").build(),
                    InlineKeyboardButton.builder().text("👤 Assign to User").callbackData("asgn_flow_user").build()
            ));
        }else{
            rows.add(new InlineKeyboardRow(
                    InlineKeyboardButton.builder().text("🏃 Assign to Sprint").callbackData("asgn_flow_sprint").build(),
                    InlineKeyboardButton.builder().text("👤 Assign to Yourself").callbackData("asgn_flow_user").build()
            ));
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        BotHelper.sendMessageWithInlineKeyboard(chatId, text, markup, client);
        return BotState.ASSIGN_TASK;
    }

    @Override
    public BotState handleCallback(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        String data = update.getCallbackQuery().getData();
        TelegramClient client = BotHelper.getTelegramClient();

        SessionData session = userSessions.get(chatId);
        if (session == null) return BotState.IDLE;

        if (data.startsWith("asgn_flow_")) {
            return handleFlowSelection(chatId, messageId, data.substring(10), session, client);
        } else if (data.startsWith("asgn_proj_")) {
            return handleProjectSelection(chatId, messageId, data.substring(10), session, client);
        } else if (data.startsWith("asgn_task_")) {
            return handleTaskSelection(chatId, messageId, data.substring(10), session, client);
        } else if (data.startsWith("asgn_target_")) {
            return handleTargetSelection(chatId, messageId, data.substring(12), session, client);
        }

        return BotState.ASSIGN_TASK;
    }

    private BotState handleFlowSelection(long chatId, int messageId, String flowStr, SessionData session, TelegramClient client) {
        session.flowType = flowStr.equals("sprint") ? FlowType.SPRINT : FlowType.USER;
        session.step = Step.SELECT_PROJECT;
        Users user = usersRepository.getReferenceById(session.userId);
        List<ProjectMembers> memberships = projectMembersRepository.findByUserId(user.getUserId());
        List<Projects> projects = memberships.stream()
                .map(m -> projectsRepository.findById(m.getProjectId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (projects.isEmpty()) {
            BotHelper.editMessageText(chatId, messageId, "❌ No projects found for you.", client);
            cleanup(chatId);
            return BotState.IDLE;
        }

        String text = "📁 Select a Project:";
        List<InlineKeyboardRow> rows = projects.stream()
                .map(p -> new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text(p.getName())
                        .callbackData("asgn_proj_" + p.getProjectId().toString())
                        .build()))
                .collect(Collectors.toList());

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        BotHelper.editMessageTextWithKeyboard(chatId, messageId, text, markup, client);
        return BotState.ASSIGN_TASK;
    }

    private BotState handleProjectSelection(long chatId, int messageId, String projIdStr, SessionData session, TelegramClient client) {
        session.projectId = UUID.fromString(projIdStr);
        session.step = Step.SELECT_TASK;

        List<Tasks> tasks = tasksRepository.findByProjectId(session.projectId);

        // If Developer is assigning to user, they assign to themselves, so filter out tasks they already have
        if (session.userRole == UserRole.DEVELOPER && session.flowType == FlowType.USER) {
            Set<UUID> assignedTaskIds = taskAssignmentsRepository.findByUserId(session.userId).stream()
                    .map(TaskAssignments::getTaskId)
                    .collect(Collectors.toSet());
            tasks = tasks.stream()
                    .filter(t -> !assignedTaskIds.contains(t.getTaskId()))
                    .collect(Collectors.toList());
        }

        if (tasks.isEmpty()) {
            BotHelper.editMessageText(chatId, messageId, "❌ No eligible tasks found in this project.", client);
            cleanup(chatId);
            return BotState.IDLE;
        }

        String text = "📝 Select a Task: " + (session.userRole == UserRole.DEVELOPER && session.flowType  == FlowType.USER ? "! THIS TASK WILL BE ASSIGNED TO YOU" : "");
        List<InlineKeyboardRow> rows = tasks.stream()
                .map(t -> new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text(t.getTitle())
                        .callbackData("asgn_task_" + t.getTaskId().toString())
                        .build()))
                .collect(Collectors.toList());

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        BotHelper.editMessageTextWithKeyboard(chatId, messageId, text, markup, client);
        return BotState.ASSIGN_TASK;
    }

    private BotState handleTaskSelection(long chatId, int messageId, String taskIdStr, SessionData session, TelegramClient client) {
        session.taskId = UUID.fromString(taskIdStr);
        session.step = Step.SELECT_TARGET;

        if (session.flowType == FlowType.SPRINT) {
            List<Sprints> sprints = sprintsRepository.findByProjectId(session.projectId);
            if (sprints.isEmpty()) {
                BotHelper.editMessageText(chatId, messageId, "❌ No sprints found for this project.", client);
                cleanup(chatId);
                return BotState.IDLE;
            }

            String text = "🏃 Select a Sprint:";
            List<InlineKeyboardRow> rows = sprints.stream()
                    .map(s -> new InlineKeyboardRow(InlineKeyboardButton.builder()
                            .text(s.getName() + " (" + s.getStatus() + ")")
                            .callbackData("asgn_target_" + s.getSprintId().toString())
                            .build()))
                    .collect(Collectors.toList());

            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
            BotHelper.editMessageTextWithKeyboard(chatId, messageId, text, markup, client);
        } else {
            if (session.userRole == UserRole.DEVELOPER) {
                if (taskAssignmentsRepository.existsByTaskIdAndUserId(session.taskId, session.userId)) {
                    BotHelper.editMessageText(chatId, messageId, "⚠️ Task is already assigned to you.", client);
                } else {
                    TaskAssignments assignment = new TaskAssignments(session.taskId, session.userId);
                    taskAssignmentsRepository.save(assignment);
                    BotHelper.editMessageText(chatId, messageId, "✅ Task successfully assigned to you!", client);
                }
                cleanup(chatId);
                return BotState.IDLE;
            }

            List<ProjectMembers> members = projectMembersRepository.findByProjectId(session.projectId);
            if (members.isEmpty()) {
                BotHelper.editMessageText(chatId, messageId, "❌ No members found in this project.", client);
                cleanup(chatId);
                return BotState.IDLE;
            }

            String text = "👤 Select a User:";
            List<InlineKeyboardRow> rows = members.stream()
                    .map(m -> usersRepository.findById(m.getUserId()).orElse(null))
                    .filter(Objects::nonNull)
                    .map(u -> new InlineKeyboardRow(InlineKeyboardButton.builder()
                            .text(u.getUsername())
                            .callbackData("asgn_target_" + u.getUserId().toString())
                            .build()))
                    .collect(Collectors.toList());

            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
            BotHelper.editMessageTextWithKeyboard(chatId, messageId, text, markup, client);
        }
        return BotState.ASSIGN_TASK;
    }

    private BotState handleTargetSelection(long chatId, int messageId, String targetIdStr, SessionData session, TelegramClient client) {
        UUID targetId = UUID.fromString(targetIdStr);

        if (session.flowType == FlowType.SPRINT) {
            Tasks task = tasksRepository.findById(session.taskId).orElse(null);
            if (task != null) {
                task.setSprintId(targetId);
                tasksRepository.save(task);
                BotHelper.editMessageText(chatId, messageId, "✅ Task successfully assigned to sprint!", client);
            } else {
                BotHelper.editMessageText(chatId, messageId, "❌ Error: Task not found.", client);
            }
        } else {
            // Assign to user: check if already assigned or just add new
            TaskAssignments assignment = new TaskAssignments(session.taskId, targetId);
            taskAssignmentsRepository.save(assignment);
            BotHelper.editMessageText(chatId, messageId, "✅ Task successfully assigned to user!", client);
        }

        cleanup(chatId);
        return BotState.IDLE;
    }

    private void cleanup(long chatId) {
        userSessions.remove(chatId);
    }
}
