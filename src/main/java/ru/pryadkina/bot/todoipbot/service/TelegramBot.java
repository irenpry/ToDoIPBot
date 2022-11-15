package ru.pryadkina.bot.todoipbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pryadkina.bot.todoipbot.config.BotConfig;
import ru.pryadkina.bot.todoipbot.controllers.BotController;
import ru.pryadkina.bot.todoipbot.util.ToDoBotCommand;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final BotController botController;

    @Autowired
    public TelegramBot(BotConfig botConfig, BotController botController) {
        this.botConfig = botConfig;
        this.botController = botController;
        List<BotCommand> commands = new ArrayList<>();
        for (ToDoBotCommand command : ToDoBotCommand.getList()) {
            commands.add(new BotCommand(command.getTextCommand(), command.getDescription()));
        }
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String commandString = update.getMessage().getText().split(" ")[0];
            ToDoBotCommand command = ToDoBotCommand.getBotCommand(commandString);
            try {
                execute(botController.handle(command,update));
            } catch (TelegramApiException e) {

            }
        }
        if (update.hasCallbackQuery()) {
            try {
                execute(botController.handle(null,update));
            } catch (TelegramApiException e) {

            }
        }

    }

}
