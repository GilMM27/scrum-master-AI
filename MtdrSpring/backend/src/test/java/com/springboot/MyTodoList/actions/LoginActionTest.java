package com.springboot.MyTodoList.actions;

import com.springboot.MyTodoList.model.Users;
import com.springboot.MyTodoList.model.UserRole;
import com.springboot.MyTodoList.model.AccountStatus;
import com.springboot.MyTodoList.states.BotState;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginActionTest {

    @InjectMocks
    private LoginAction loginAction;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private User telegramUser;

    @Mock
    private Contact contact;

    private static final Long CHAT_ID = 12345678L;
    private static final Long TELEGRAM_ID = 87654321L;
    private static final String PHONE_NUMBER = "1234567895";

    @BeforeEach
    void setUp() {
        BotHelper.setTelegramClient(telegramClient);
        
        // Use setter injection for the mock repository
        loginAction.setServices(usersRepository, null, null, null, null, null, null);
        
        lenient().when(update.getMessage()).thenReturn(message);
        lenient().when(message.getChatId()).thenReturn(CHAT_ID);
        lenient().when(message.getFrom()).thenReturn(telegramUser);
        lenient().when(telegramUser.getId()).thenReturn(TELEGRAM_ID);
    }

    @Test
    void testGetState() {
        assertEquals(BotState.LOGIN, loginAction.getState());
    }

    @Test
    void testCanHandle_LoginCommand() {
        when(update.hasMessage()).thenReturn(true);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn(BotCommands.LOGIN.getCommand());
        
        assertTrue(loginAction.canHandle(update));
    }

    @Test
    void testCanHandle_Contact() {
        when(update.hasMessage()).thenReturn(true);
        when(message.hasContact()).thenReturn(true);
        
        assertTrue(loginAction.canHandle(update));
    }

    @Test
    void testHandle_ShowContactKeyboard() throws Exception {
        when(message.hasContact()).thenReturn(false);
        
        BotState nextState = loginAction.handle(update);
        
        assertEquals(BotState.LOGIN, nextState);
        verify(telegramClient, times(1)).execute(any(SendMessage.class));
    }

    @Test
    void testHandle_ProcessValidPhoneNumber() throws Exception {
        when(message.hasContact()).thenReturn(true);
        when(message.getContact()).thenReturn(contact);
        when(contact.getPhoneNumber()).thenReturn(PHONE_NUMBER);
        
        Users mockUser = new Users();
        mockUser.setUserId(UUID.randomUUID());
        mockUser.setUsername("new_admin");
        mockUser.setCellPhone(PHONE_NUMBER);
        mockUser.setUserRole(UserRole.ADMIN);
        mockUser.setAccountStatus(AccountStatus.ACTIVE);
        
        when(usersRepository.findByCellPhone(PHONE_NUMBER)).thenReturn(Optional.of(mockUser));
        
        BotState nextState = loginAction.handle(update);
        
        assertEquals(BotState.IDLE, nextState);
        assertEquals(TELEGRAM_ID, mockUser.getTelegramId());
        verify(usersRepository, times(1)).save(mockUser);
        // Should send "is this your phone?", "Login successful"
        verify(telegramClient, atLeast(2)).execute(any(SendMessage.class));
    }

    @Test
    void testHandle_ProcessUnknownPhoneNumber() throws Exception {
        when(message.hasContact()).thenReturn(true);
        when(message.getContact()).thenReturn(contact);
        when(contact.getPhoneNumber()).thenReturn("9999999999");
        
        when(usersRepository.findByCellPhone("9999999999")).thenReturn(Optional.empty());
        
        BotState nextState = loginAction.handle(update);
        
        assertEquals(BotState.LOGIN, nextState);
        verify(telegramClient, atLeast(2)).execute(any(SendMessage.class));
    }
}
