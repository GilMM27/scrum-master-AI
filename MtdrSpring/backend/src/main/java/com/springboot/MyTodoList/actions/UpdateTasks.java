package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.ProjectMembers;
import com.springboot.MyTodoList.model.SprintStatus;
import com.springboot.MyTodoList.model.TaskAssignments;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.TaskStatus;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@Component
public class UpdateTasks extends BotActionBase {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTasks.class);

    private Map<Long, TaskFlowState> userStates = new HashMap<>();
    private Map<Long, UUID> selectedTaskId = new HashMap<>();

    private enum TaskFlowState {
        SELECTING_TASK,
        SELECTING_ACTION,
        SELECTING_SPRINT,
        SELECTING_STATE
    }

    @Override
    public BotState getState() {
        return BotState.TASKS;
    }

    @Override
    public boolean canHandle(Update update) {
        if(!update.getMessage().hasText()){
            return false;
        }
        String messageText = update.getMessage().getText();
        return messageText.equals(BotCommands.TASK_UPDATE.getCommand());
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        long telegramId = update.getMessage().getFrom().getId();
        TelegramClient client = BotHelper.getTelegramClient();

        Users user = usersRepository.findByTelegramId(telegramId).orElse(null);
        if (user == null) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Debes iniciar sesión para usar este comando. Usa /login primero.", client);
            return BotState.IDLE;
        }

        List<Tasks> userTasks = getUserTasksThatArentDone(user.getUserId());
        
        if (userTasks.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "📋 No tienes tareas en sprints activos.", client);
            return BotState.IDLE;
        }

        userStates.put(chatId, TaskFlowState.SELECTING_TASK);
        showTaskSelection(chatId, userTasks, client);
        return BotState.TASKS;
    }

    private List<Tasks> getUserTasksThatArentDone(UUID userId) {
        List<TaskAssignments> assignments = taskAssignmentsRepository.findByUserId(userId);
        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> taskIds = assignments.stream()
                .map(TaskAssignments::getTaskId)
                .collect(Collectors.toList());

        return tasksRepository.findAllById(taskIds).stream().filter(task -> task.getStatus() != TaskStatus.DONE)
                .collect(Collectors.toList());
    }

    private void showTaskSelection(long chatId, List<Tasks> tasks, TelegramClient client) {
        String text = "📋 Selecciona una Tarea:";

        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        for (Tasks task : tasks) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(InlineKeyboardButton.builder()
                    .text(task.getTitle() + " - " + task.getStatus())
                    .callbackData("task_" + task.getTaskId().toString())
                    .build());
            keyboard.add(row);
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();

        BotHelper.sendMessageWithInlineKeyboard(chatId, text, markup, client);
    }

    @Override
    public boolean canHandleCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        return callbackData.startsWith("task_") ||
               callbackData.startsWith("action_") ||
               callbackData.startsWith("spr_") ||
               callbackData.startsWith("sprintstate_") ||
               callbackData.startsWith("state_");
    }

    @Override
    public BotState handleCallback(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        String callbackData = update.getCallbackQuery().getData();
        TelegramClient client = BotHelper.getTelegramClient();
        
        TaskFlowState state = userStates.getOrDefault(chatId, TaskFlowState.SELECTING_TASK);

        try {
            switch (state) {
                case SELECTING_TASK:
                    handleTaskSelection(callbackData, chatId, messageId, client);
                    break;
                case SELECTING_ACTION:
                    handleActionSelection(callbackData, chatId, messageId, client);
                    break;
                case SELECTING_SPRINT:
                    handleSprintSelection(callbackData, chatId, messageId, client);
                    break;
                case SELECTING_STATE:
                    handleStateSelection(callbackData, chatId, messageId, client);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error handling callback: {}", e.getMessage(), e);
            userStates.remove(chatId);
            selectedTaskId.remove(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Ocurrió un error. Por favor intenta de nuevo.", client);
            return BotState.IDLE;
        }
        
        return userStates.containsKey(chatId) ? BotState.TASKS : BotState.IDLE;
    }

    private void handleTaskSelection(String callbackData, long chatId, int messageId, TelegramClient client) {
        if (!callbackData.startsWith("task_")) return;

        String taskIdStr = callbackData.substring(5);
        UUID taskId = UUID.fromString(taskIdStr);
        selectedTaskId.put(chatId, taskId);
        userStates.put(chatId, TaskFlowState.SELECTING_ACTION);

        String text = "⚙️ Selecciona una acción:";

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text("🔄 Cambiar Estado")
                                .callbackData("action_change_state_" + taskIdStr)
                                .build()
                ))
                .build();

        BotHelper.editMessageTextWithKeyboard(chatId, messageId, text, markup, client);
    }

    private void handleActionSelection(String callbackData, long chatId, int messageId, TelegramClient client) {
        if (!callbackData.startsWith("action_")) return;

        UUID taskId = selectedTaskId.get(chatId);
        if (taskId == null) {
            userStates.remove(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Sesión expirada. Por favor usa /utask de nuevo.", client);
            return;
        }


        userStates.put(chatId, TaskFlowState.SELECTING_STATE);
        showStateSelection(chatId, taskId, messageId, client);
        
    }

    private void showSprintSelection(long chatId, UUID taskId, int messageId, TelegramClient client) {


        Optional<Users> usr = usersRepository.findByTelegramId(chatId);
        List<ProjectMembers>prjs = projectMembersRepository.findByUserId(usr.get().getUserId());
        List<UUID> userProjectIds = prjs.stream()
            .map(ProjectMembers::getProjectId)
            .collect(Collectors.toList());
        List<Sprints> activeSprints = sprintsRepository.findByStatus(SprintStatus.ACTIVE).stream()
            .filter(sprint -> userProjectIds.contains(sprint.getProjectId()))
            .collect(Collectors.toList());
        
        String text = "Esta tarea no tiene sprint. Selecciona un Sprint para moverla a EN PROGRESO";

        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        for (Sprints sprint : activeSprints) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            if(("spr_" + sprint.getSprintId().toString() + "$" + taskId.toString()).getBytes(StandardCharsets.UTF_8).length > 64){
                System.out.println("Cannot be larger than x");
            }
            row.add(InlineKeyboardButton.builder()
                    .text(sprint.getName())
                    .callbackData("spr_" + sprint.getSprintId().toString())
                    .build());
            keyboard.add(row);
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
        BotHelper.editMessageText(chatId,messageId, text, client);
        BotHelper.editMessageReplyMarkup(chatId, messageId, markup, client);
        
        
    }

    private void showStateSelection(long chatId, UUID taskId, int messageId, TelegramClient client) {
        Tasks task = tasksRepository.findByTaskId(taskId).orElse(null);
        if (task == null) {
            userStates.remove(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Tarea no encontrada.", client);
            return;
        }

        TaskStatus currentStatus = task.getStatus();
        List<TaskStatus> nextStates = getValidNextStates(currentStatus);

        String text = "🔄 Estado Actual: " + BotHelper.escapeMarkdown(currentStatus.toString()) + "\nSelecciona el nuevo estado:";

        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        for (TaskStatus nextStatus : nextStates) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            String callbackData = "state_" + nextStatus.name() + "$" + taskId.toString();
            
            row.add(InlineKeyboardButton.builder()
                    .text(currentStatus + " → " + nextStatus)
                    .callbackData(callbackData)
                    .build());
            keyboard.add(row);
        }

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();

        BotHelper.editMessageTextWithKeyboard(chatId, messageId, text, markup, client);
    }

    private List<TaskStatus> getValidNextStates(TaskStatus current) {
        if (current == TaskStatus.TO_DO) {
            return List.of(TaskStatus.IN_PROGRESS);
        } else if (current == TaskStatus.IN_PROGRESS) {
            return List.of(TaskStatus.REVIEW, TaskStatus.DONE, TaskStatus.BLOCKED);
        } else if (current == TaskStatus.REVIEW) {
            return List.of(TaskStatus.IN_PROGRESS, TaskStatus.DONE);
        } else if (current == TaskStatus.DONE) {
            return List.of();
        } else if (current == TaskStatus.BLOCKED) {
            return List.of(TaskStatus.IN_PROGRESS, TaskStatus.TO_DO);
        }
        return List.of();
    }

    private void handleSprintSelection(String callbackData, long chatId, int messageId, TelegramClient client) {
        String[] parts = callbackData.substring(4).split("\\$");

        UUID sprintId = UUID.fromString(parts[0]);
        UUID taskId = selectedTaskId.get(chatId);

        Tasks task = tasksRepository.findByTaskId(taskId).orElse(null);
        if (task == null) {
            userStates.remove(chatId);
            selectedTaskId.remove(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Tarea no encontrada.", client);
            return;
        }
        task.setSprintId(sprintId);
        tasksRepository.save(task);
        tasksService.updateStatus(taskId, TaskStatus.IN_PROGRESS);

        Sprints sprint = sprintsRepository.findById(sprintId).orElse(null);
        String sprintName = sprint != null ? sprint.getName() : "Unknown";

        userStates.remove(chatId);
        selectedTaskId.remove(chatId);


        String message = "✅ ¡Sprint asignado y estado cambiado!\n\nTarea: " + BotHelper.escapeMarkdown(task.getTitle()) + "\nSprint: " + BotHelper.escapeMarkdown(sprintName) + "\nNuevo Estado: EN PROGRESO";

        BotHelper.editMessageTextWithKeyboard(chatId, messageId, message, null, client);
    }

    private void handleStateSelection(String callbackData, long chatId, int messageId, TelegramClient client) {

        if (!callbackData.startsWith("state_")) return;

        String[] parts = callbackData.substring(6).split("\\$");
        if (parts.length != 2) {
            BotHelper.sendMessageToTelegram(chatId, "Error al gestionar task_id y estado: " + parts[0]  , client);
            return;
        }

        TaskStatus newStatus = TaskStatus.valueOf(parts[0]);
        UUID taskId = UUID.fromString(parts[1]);

        Tasks task = tasksRepository.findByTaskId(taskId).orElse(null);
        if (task == null) {
            userStates.remove(chatId);
            selectedTaskId.remove(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Tarea no encontrada.", client);
            return;
        }if(task.getSprintId() == null && newStatus == TaskStatus.IN_PROGRESS){
            userStates.put(chatId, TaskFlowState.SELECTING_SPRINT);
            showSprintSelection(chatId, taskId, messageId, client);
            return;
        }

        tasksService.updateStatus(taskId, newStatus);

        userStates.remove(chatId);
        selectedTaskId.remove(chatId);

        BotHelper.editMessageTextWithKeyboard(chatId, messageId, 
                "✅ ¡Estado actualizado!\n\nTarea: " + BotHelper.escapeMarkdown(task.getTitle()) + "\nNuevo Estado: " + BotHelper.escapeMarkdown(newStatus.toString()), 
                null, client);
    }

    @Override
    public void reset(long chatId) {
        cleanup(chatId);
    }

    private void cleanup(long chatId) {
        userStates.remove(chatId);
        selectedTaskId.remove(chatId);
    }
}