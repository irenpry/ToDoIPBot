package ru.pryadkina.bot.todoipbot.util;

public class UserNotFoundException extends RuntimeException {

    private final String message = "Неизвестный пользователь. Убедитесь, что он зарегистрирован";

    @Override
    public String getMessage() {
        return message;
    }
}
