package ru.pryadkina.bot.todoipbot.controllers;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pryadkina.bot.todoipbot.model.ReplyMessage;
import ru.pryadkina.bot.todoipbot.model.User;
import ru.pryadkina.bot.todoipbot.service.UserService;

@Controller
public class UserController extends AController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    public SendMessage register(Update update) {
        User user = new User();
        long chatId = update.getMessage().getChatId();
        user.setChatId(chatId);

        ReplyMessage replyMessage = new ReplyMessage(userService.save(update.getMessage().getChat().getFirstName(),user));
        return buildSendMessage(chatId, replyMessage);
    }


}
