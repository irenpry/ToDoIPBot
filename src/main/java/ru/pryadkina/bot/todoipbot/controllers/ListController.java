package ru.pryadkina.bot.todoipbot.controllers;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pryadkina.bot.todoipbot.model.ReplyMessage;
import ru.pryadkina.bot.todoipbot.service.ListService;

@Controller
public class ListController extends AController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    public SendMessage list(Update update) {
        long chatId = update.getMessage().getChatId();
        String mes = update.getMessage().getText().split(" ",2)[1];
        listService.save(update.getMessage().getChatId(),mes);
        ReplyMessage replyMessage = new ReplyMessage(EmojiParser.parseToUnicode("Список добавлен :avocado:"));
        return buildSendMessage(chatId, replyMessage);
    }

    public SendMessage addParticipant(Update update) {
        long chatId = update.getMessage().getChatId();
        String[] params = update.getMessage().getText().split(" ",3);
        String result = listService.addParticipant(update.getMessage().getChatId(),Integer.parseInt(params[1]),Integer.parseInt(params[2]));
        ReplyMessage replyMessage = new ReplyMessage(result);
        return buildSendMessage(chatId, replyMessage);
    }

    public SendMessage deleteParticipant(Update update) {
        long chatId = update.getMessage().getChatId();
        String[] params = update.getMessage().getText().split(" ",3);
        String result = listService.deleteParticipant(update.getMessage().getChatId(),Integer.parseInt(params[1]),Integer.parseInt(params[2]));
        ReplyMessage replyMessage = new ReplyMessage(result);
        return buildSendMessage(chatId, replyMessage);
    }

    public SendMessage getLists(Update update) {
        long chatId = update.getMessage().getChatId();
        ReplyMessage replyMessage = new ReplyMessage(listService.get(update.getMessage().getChatId(),null));
        return buildSendMessage(chatId, replyMessage);
    }

}
