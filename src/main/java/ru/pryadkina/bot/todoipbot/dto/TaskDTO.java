package ru.pryadkina.bot.todoipbot.dto;


public class TaskDTO {

    private int id;

    private String text;

    private Boolean isImportant;

    private Integer status;

    private UserDTO executor;

    private UserDTO author;

    private TasksListDTO parentList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getImportant() {
        return isImportant;
    }

    public void setImportant(Boolean important) {
        isImportant = important;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UserDTO getExecutor() {
        return executor;
    }

    public void setExecutor(UserDTO executor) {
        this.executor = executor;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public TasksListDTO getParentList() {
        return parentList;
    }

    public void setParentList(TasksListDTO parentList) {
        this.parentList = parentList;
    }

}
