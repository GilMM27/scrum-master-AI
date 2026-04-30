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

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class KpiAction extends BotActionBase {

    private final Map<Long, UUID> selectedProject = new ConcurrentHashMap<>();

    @Override
    public BotState getState() {
        return BotState.KPI;
    }

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        return update.getMessage().getText().startsWith(BotCommands.KPI.getCommand());
    }

    @Override
    public boolean canHandleCallback(Update update) {
        if (!update.hasCallbackQuery()) return false;
        return update.getCallbackQuery().getData().startsWith("kpi_proj_");
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        TelegramClient client = BotHelper.getTelegramClient();
        long telegramId = update.getMessage().getFrom().getId();

        if (update.getMessage().getText().equalsIgnoreCase("/cancel")) {
            cleanup(chatId);
            BotHelper.sendMessageToTelegram(chatId, "❌ Solicitud de KPI cancelada.", client);
            return BotState.IDLE;
        }

        return startFlow(chatId, client, telegramId);
    }

    private BotState startFlow(long chatId, TelegramClient client, long telegramId) {
        Users user = usersRepository.findByTelegramId(telegramId).orElse(null);
        if (user == null) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Usuario no encontrado. Por favor inicia sesión primero.", client);
            return BotState.IDLE;
        }

        List<ProjectMembers> memberships = projectMembersRepository.findByUserId(user.getUserId());
        List<Projects> projects = memberships.stream()
                .map(m -> projectsRepository.findById(m.getProjectId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (projects.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "❌ No eres miembro de ningún proyecto.", client);
            return BotState.IDLE;
        }

        String text = "📊 Selecciona un Proyecto para ver sus KPIs:";
        List<InlineKeyboardRow> rows = projects.stream()
                .map(p -> new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text(p.getName())
                        .callbackData("kpi_proj_" + p.getProjectId().toString())
                        .build()))
                .collect(Collectors.toList());

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        BotHelper.sendMessageWithInlineKeyboard(chatId, text, markup, client);
        return BotState.KPI;
    }

    @Override
    public BotState handleCallback(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        String data = update.getCallbackQuery().getData();
        TelegramClient client = BotHelper.getTelegramClient();

        if (data.startsWith("kpi_proj_")) {
            UUID projectId = UUID.fromString(data.substring(9));
            generateKpiReport(chatId, messageId, projectId, client);
        }

        cleanup(chatId);
        return BotState.IDLE;
    }

    private void generateKpiReport(long chatId, int messageId, UUID projectId, TelegramClient client) {
        Projects project = projectsRepository.findById(projectId).orElse(null);
        if (project == null) {
            BotHelper.editMessageText(chatId, messageId, "❌ Proyecto no encontrado.", client);
            return;
        }

        List<Tasks> tasks = tasksRepository.findByProjectId(projectId);
        List<ProjectMembers> members = projectMembersRepository.findByProjectId(projectId);
        List<Sprints> sprints = sprintsRepository.findByProjectId(projectId);

        // Calculations
        int totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        int totalStoryPoints = tasks.stream().mapToInt(Tasks::getStoryPoints).sum();
        int completedStoryPoints = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .mapToInt(Tasks::getStoryPoints)
                .sum();

        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        double spCompletionRate = totalStoryPoints > 0 ? (double) completedStoryPoints / totalStoryPoints * 100 : 0;

        long aiFlaggedCount = tasks.stream().filter(t -> Boolean.TRUE.equals(t.getAiFlagged())).count();
        long blockedCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.BLOCKED).count();

        // Average Lead Time (Created -> Delivered)
        double avgLeadTimeHours = tasks.stream()
                .filter(t -> t.getCreatedAt() != null && t.getDeliveredAt() != null)
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getDeliveredAt()).toHours())
                .average()
                .orElse(0.0);

        // Average Cycle Time (Started -> Delivered)
        double avgCycleTimeHours = tasks.stream()
                .filter(t -> t.getStartedAt() != null && t.getDeliveredAt() != null)
                .mapToLong(t -> Duration.between(t.getStartedAt(), t.getDeliveredAt()).toHours())
                .average()
                .orElse(0.0);

        StringBuilder report = new StringBuilder();
        report.append("📊 *Informe de KPI: ").append(project.getName()).append("*\n\n");
        report.append("👥 *Tamaño del Equipo:* ").append(members.size()).append("\n");
        report.append("🏃 *Total de Sprints:* ").append(sprints.size()).append("\n\n");

        report.append("📝 *Estadísticas de Tareas:*\n");
        report.append("• Total de Tareas: ").append(totalTasks).append("\n");
        report.append("• Tareas Completadas: ").append(completedTasks).append(" (").append(String.format("%.1f", completionRate)).append("%)\n");
        report.append("• Tareas Bloqueadas: ").append(blockedCount).append("\n");
        report.append("• Tareas Marcadas por IA: ").append(aiFlaggedCount).append("\n\n");

        report.append("🎯 *Story Points:*\n");
        report.append("• Total de SP: ").append(totalStoryPoints).append("\n");
        report.append("• SP Completados: ").append(completedStoryPoints).append(" (").append(String.format("%.1f", spCompletionRate)).append("%)\n\n");

        report.append("⏱ *Rendimiento:*\n");
        report.append("• Lead Time Promedio: ").append(String.format("%.1f", avgLeadTimeHours)).append(" horas\n");
        report.append("• Cycle Time Promedio: ").append(String.format("%.1f", avgCycleTimeHours)).append(" horas\n");

        BotHelper.editMessageText(chatId, messageId, report.toString(), client);
    }

    @Override
    public void reset(long chatId) {
        cleanup(chatId);
    }

    private void cleanup(long chatId) {
        selectedProject.remove(chatId);
    }
}
