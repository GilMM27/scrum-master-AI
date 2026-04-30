package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.service.GeminiTools;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Component
public class GeminiAction extends BotActionBase {
    private static final Logger logger = LoggerFactory.getLogger(GeminiAction.class);
    private final ChatClient chatClient;
    private final UsersRepository usersRepository;

    public GeminiAction(ChatClient.Builder chatClientBuilder, GeminiTools geminiTools, ChatMemory chatMemory, UsersRepository usersRepository) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(geminiTools)
                .defaultSystem("You are a helpful Scrum Master AI. " +
                        "Use the provided tools to help the user manage tasks, projects, and sprints. " +
                        "When asked about 'my' items, refer to the User ID provided in the context.")
                .build();
        this.usersRepository = usersRepository;
    }

    @Override
    public BotState getState() {
        return BotState.GEMINI;
    }

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        return update.getMessage().getText().startsWith(BotCommands.LLM_REQ.getCommand());
    }

    @Override
    public BotState handle(Update update) {
        long chatId = update.getMessage().getChatId();
        long telegramId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();
        String prompt;
        
        if (text.startsWith(BotCommands.LLM_REQ.getCommand())) {
            prompt = text.substring(BotCommands.LLM_REQ.getCommand().length()).trim();
        } else {
            prompt = text;
        }

        if (prompt.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "Por favor, proporciona una consulta para Gemini.", BotHelper.getTelegramClient());
            return BotState.IDLE;
        }

        Optional<Users> userOpt = usersRepository.findByTelegramId(telegramId);
        String finalPrompt = prompt;
        
        // Pass identity as a separate System instruction per-request to avoid polluting the User prompt
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            return executeGeminiRequestWithIdentity(chatId, prompt, user);
        }

        return executeGeminiRequest(chatId, prompt);
    }

    private BotState executeGeminiRequestWithIdentity(long chatId, String prompt, Users user) {
        try {
            String result = chatClient.prompt(prompt)
                    .system(s -> s.text("Current User Context: [Name: " + user.getUsername() + ", ID: " + user.getUserId() + ", Role: " + user.getUserRole() + "]"))
                    .advisors(a -> a
                            .param(CONVERSATION_ID, chatId))
                    .call()
                    .content();
            BotHelper.sendMessageToTelegram(chatId, result, BotHelper.getTelegramClient());
        } catch (Exception e) {
            logger.error("Error calling Gemini service", e);
            BotHelper.sendMessageToTelegram(chatId, "Lo siento, encontré un error al hablar con Gemini.", BotHelper.getTelegramClient());
        }
        return BotState.IDLE;
    }

    public BotState executeGeminiRequest(long chatId, String prompt) {
        try {
            String result = chatClient.prompt(prompt)
                    .advisors(a -> a
                            .param(CONVERSATION_ID, chatId))
                    .call()
                    .content();
            BotHelper.sendMessageToTelegram(chatId, result, BotHelper.getTelegramClient());
        } catch (Exception e) {
            logger.warn("Failed to parse Gemini response: {}", jsonResponse);
            return "Error al analizar la respuesta de Gemini.";
        }
        return BotState.IDLE;
    }
}
