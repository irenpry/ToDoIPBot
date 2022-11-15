package ru.pryadkina.bot.todoipbot.dto;

import java.util.List;

public class TasksListDTO {
    private int id;

    private String name;

    private int status;

    private List<UserDTO> owners;

    private List<UserDTO> participants;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<UserDTO> getOwners() {
        return owners;
    }

    public void setOwners(List<UserDTO> owners) {
        this.owners = owners;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDTO> participants) {
        this.participants = participants;
    }
}
