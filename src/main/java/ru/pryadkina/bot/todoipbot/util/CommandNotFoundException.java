package ru.pryadkina.bot.todoipbot.util;

public class CommandNotFoundException extends RuntimeException {

    private final String message = "Неизвестная команда";

    @Override
    public String getMessage() {
        return message;
    }


}
