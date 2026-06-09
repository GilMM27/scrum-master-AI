package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.Projects;
import com.springboot.MyTodoList.model.Tasks;
import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.model.ProjectMembers;
import com.springboot.MyTodoList.repository.ProjectsRepository;
import com.springboot.MyTodoList.repository.TasksRepository;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.repository.ProjectMembersRepository;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReportAction extends BotActionBase {
    private static final Logger logger = LoggerFactory.getLogger(ReportAction.class);
    private final ChatClient chatClient;

    public ReportAction(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("You are a professional Scrum Master assistant. " +
                        "Your goal is to paraphrase raw task activity data into a concise, professional daily status report. " +
                        "Focus on what was created, started, or delivered in the last 24 hours. " +
                        "IMPORTANT: Use only basic Telegram-compatible Markdown (Legacy) like *bold* and _italic_. " +
                        "Ensure all formatting tags are closed correctly. Avoid using characters like [, ], (, ) unless they are part of a link or properly escaped if they could be interpreted as Markdown.")
                .build();
    }

    @Override
    public BotState getState() {
        return BotState.REPORT;
    }

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        return update.getMessage().getText().equals(BotCommands.REPORT.getCommand());
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        long telegramId = update.getMessage().getFrom().getId();

        Optional<Users> userOpt = usersRepository.findByTelegramId(telegramId);
        if (userOpt.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "❌ Debes iniciar sesión para usar este comando. Usa /login primero.", BotHelper.getTelegramClient());
            return BotState.IDLE;
        }

        Users user = userOpt.get();
        List<ProjectMembers> memberships = projectMembersRepository.findByUserId(user.getUserId());

        if (memberships.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "📋 No tienes proyectos asignados.", BotHelper.getTelegramClient());
            return BotState.IDLE;
        }

        OffsetDateTime since = OffsetDateTime.now().minusDays(1);
        StringBuilder rawData = new StringBuilder();
        rawData.append("User: ").append(user.getUsername()).append("\n");

        for (ProjectMembers membership : memberships) {
            UUID projectId = membership.getProjectId();
            Optional<Projects> projectOpt = projectsRepository.findById(projectId);
            String projectName = projectOpt.map(Projects::getName).orElse("Unknown Project");
            
            List<Tasks> recentTasks = tasksRepository.findRecentActivityByProjectId(projectId, since);
            
            if (!recentTasks.isEmpty()) {
                rawData.append("\nProject: ").append(projectName).append("\n");
                for (Tasks task : recentTasks) {
                    rawData.append("- Task: ").append(task.getTitle())
                            .append(" (Status: ").append(task.getStatus())
                            .append(", Priority: ").append(task.getPriority())
                            .append(")\n");
                    if (task.getCreatedAt() != null && task.getCreatedAt().isAfter(since)) {
                        rawData.append("  * Created recently\n");
                    }
                    if (task.getStartedAt() != null && task.getStartedAt().isAfter(since)) {
                        rawData.append("  * Started recently\n");
                    }
                    if (task.getDeliveredAt() != null && task.getDeliveredAt().isAfter(since)) {
                        rawData.append("  * Delivered recently\n");
                    }
                }
            }
        }

        if (rawData.length() < 30) { // Only contains User info, no tasks
            BotHelper.sendMessageToTelegram(chatId, "📭 No se encontró actividad en tus proyectos en las últimas 24 horas.", BotHelper.getTelegramClient());
            return BotState.IDLE;
        }

        return generateAndSendReport(chatId, rawData.toString());
    }

    private BotState generateAndSendReport(long chatId, String rawData) {
        try {
            String prompt = "Please paraphrase the following raw task activity into a professional Daily Scrum report:\n\n" + rawData;
            String result = chatClient.prompt(prompt).call().content();
            BotHelper.sendMessageToTelegram(chatId, result, BotHelper.getTelegramClient());
        } catch (Exception e) {
            logger.error("Error generating report with Gemini", e);
            BotHelper.sendMessageToTelegram(chatId, "Lo siento, encontré un error al generar el reporte con Gemini.", BotHelper.getTelegramClient());
        }
        return BotState.IDLE;
    }
}
