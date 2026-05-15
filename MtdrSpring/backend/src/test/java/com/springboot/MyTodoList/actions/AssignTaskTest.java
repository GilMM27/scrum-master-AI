package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.*;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssignTaskTest {

    private AssignTask assignTask;

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private ProjectMembersRepository projectMembersRepository;
    @Mock
    private ProjectsRepository projectsRepository;
    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private SprintsRepository sprintsRepository;
    @Mock
    private TaskAssignmentsRepository taskAssignmentsRepository;

    @Mock
    private TelegramClient telegramClient;
    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private User telegramUser;
    @Mock
    private CallbackQuery callbackQuery;

    private static final Long CHAT_ID = 12345678L;
    private static final Long TELEGRAM_ID = 87654321L;
    private static final UUID USER_UUID = UUID.fromString("D347F1D4-B427-410C-BDDD-5178079321FC");
    private static final UUID PROJECT_UUID = UUID.fromString("EE18DDE5-AFC7-432A-8C6A-38D83A0121D9");

    @BeforeEach
    void setUp() {
        assignTask = new AssignTask(telegramClient);
        assignTask.setServices(usersRepository, null, taskAssignmentsRepository, sprintsRepository, tasksRepository, projectsRepository, projectMembersRepository);
        BotHelper.setTelegramClient(telegramClient);

        lenient().when(update.getMessage()).thenReturn(message);
        lenient().when(message.getChatId()).thenReturn(CHAT_ID);
        lenient().when(message.getFrom()).thenReturn(telegramUser);
        lenient().when(telegramUser.getId()).thenReturn(TELEGRAM_ID);
    }

    @Test
    void testStartFlow_UserFound() throws Exception {
        when(message.getText()).thenReturn("/assigntask");
        
        Users mockUser = new Users();
        mockUser.setUserId(USER_UUID);
        mockUser.setUserRole(UserRole.ADMIN);
        
        when(usersRepository.findByTelegramId(TELEGRAM_ID)).thenReturn(Optional.of(mockUser));
        
        BotState nextState = assignTask.handle(update);
        
        assertEquals(BotState.ASSIGN_TASK, nextState);
        verify(telegramClient, times(1)).execute(any(SendMessage.class));
    }

    @Test
    void testHandleCallback_FlowSelection() throws Exception {
        // First start the flow to populate session
        testStartFlow_UserFound();

        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getMessageId()).thenReturn(100);
        when(callbackQuery.getData()).thenReturn("asgn_flow_sprint");

        Users mockUser = new Users();
        mockUser.setUserId(USER_UUID);
        when(usersRepository.getReferenceById(USER_UUID)).thenReturn(mockUser);

        ProjectMembers membership = new ProjectMembers(PROJECT_UUID, USER_UUID, null);
        when(projectMembersRepository.findByUserId(USER_UUID)).thenReturn(Collections.singletonList(membership));

        Projects project = new Projects(PROJECT_UUID, "Test Project", "Desc", null);
        when(projectsRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(project));

        BotState nextState = assignTask.handleCallback(update);

        assertEquals(BotState.ASSIGN_TASK, nextState);
        verify(telegramClient, times(1)).execute(any(EditMessageText.class));
    }

    @Test
    void testHandleCallback_NoSession() {
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(callbackQuery.getData()).thenReturn("asgn_flow_sprint");

        BotState nextState = assignTask.handleCallback(update);

        assertEquals(BotState.IDLE, nextState);
    }
}
