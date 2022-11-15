package ru.pryadkina.bot.todoipbot.util;

import java.util.ArrayList;
import java.util.List;

public enum ToDoBotCommand {
    START("/start", "начать работу"),
    REGISTER("/register", "зарегистрироваться"),
    NEW_LIST("/new_list", " {название нового списка}"),
    GET_LISTS("/get_lists", "получить списки"),
    ADD_PARTICIPANT("/add_person", " {id участника} {id списка}"),
    DELETE_PARTICIPANT("/delete_person", " {id участника} {id списка}"),
    NEW_TASK("/new_task", " {текст задачи} / executor={id} list={id}"),
    GET_TASKS("/get_tasks", " {id списка}"),
    DONE("/done", " {id задачи}"),
    TEST("/test", "/test {сообщение} / param1={} param2={}");

    private String textCommand;
    private String description;

    ToDoBotCommand(String textCommand, String description) {
        this.textCommand = textCommand;
        this.description = description;
    }

    public String getTextCommand() {
        return textCommand;
    }

    public String getDescription() {
        return description;
    }

    public static List<ToDoBotCommand> getList() {
        List<ToDoBotCommand> list = new ArrayList<>();
        list.add(START);
        list.add(REGISTER);
        list.add(NEW_LIST);
        list.add(GET_LISTS);
        list.add(ADD_PARTICIPANT);
        list.add(DELETE_PARTICIPANT);
        list.add(NEW_TASK);
        list.add(DONE);
        list.add(GET_TASKS);
        return list;
    }

    public static ToDoBotCommand getBotCommand(String text) {
        for (ToDoBotCommand command : getList()) {
            if (command.getTextCommand().equals(text)) {
                return command;
            }
        }
        return null;
    }
}
