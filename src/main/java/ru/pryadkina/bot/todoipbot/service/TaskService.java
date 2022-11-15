package ru.pryadkina.bot.todoipbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import ru.pryadkina.bot.todoipbot.dto.BaseDataResponse;
import ru.pryadkina.bot.todoipbot.dto.TaskDTO;
import ru.pryadkina.bot.todoipbot.dto.TasksListDTO;
import ru.pryadkina.bot.todoipbot.dto.UserDTO;
import ru.pryadkina.bot.todoipbot.model.User;
import ru.pryadkina.bot.todoipbot.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final RestService restService;


    public TaskService(ObjectMapper objectMapper, UserRepository userRepository, RestService restService) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.restService = restService;
    }

    public String get(long chatId, Map<String,String> params) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {

            List<TaskDTO> list = findAll(chatId,params);

            StringBuilder message = new StringBuilder();

            if (list.size() == 0) {
                return "Список пуст или не существует";
            }

            if (list.get(0).getParentList() != null) {
                message.append(EmojiParser.parseToUnicode(":ledger: "))
                        .append(list.get(0).getParentList().getName())
                        .append("\n\n")
                        .append("Владелец: ")
                        .append(list.get(0).getParentList().getOwners().get(0).getName());
                if (list.get(0).getParentList().getParticipants().size() > 0) {
                    message.append("\nУчастники: ");
                    for (UserDTO participant : list.get(0).getParentList().getParticipants()) {
                        message.append(participant.getName())
                                .append(" ");
                    }
                }
                message.append("\n\n");
            } else {
                message.append(EmojiParser.parseToUnicode(":ledger: "))
                        .append("дефолтный список")
                        .append("\n\n");
            }

            for (TaskDTO taskDTO : list) {

                if (taskDTO.getStatus() == 0) {
                    message.append(EmojiParser.parseToUnicode(":white_medium_square: "));
                } else if (taskDTO.getStatus() == 1) {
                    message.append(EmojiParser.parseToUnicode(":heavy_check_mark: "));
                }
                message.append(taskDTO.getId() + " ").append(taskDTO.getText());
                if (taskDTO.getImportant() != null && taskDTO.getImportant() == true) {
                    message.append(EmojiParser.parseToUnicode(" :fire:"));
                }
                if (taskDTO.getExecutor() != null) {
                    message.append(EmojiParser.parseToUnicode(" - "))
                            .append(taskDTO.getExecutor().getName());
                }
                message.append("\n");
            }

            return message.toString();

        } else {
            System.out.println("Пользователя не нашли в БД");
            return "Ты кто?";
        }
    }

    public List<TaskDTO> findAll(long chatId, Map<String,String> params) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {
            Map<String, Object> param = new HashMap<>();

            String endpoint = null;
            if (!params.isEmpty()) {
                param.put("listId", Integer.valueOf(params.get("listId")));
                endpoint = "/tasks?listId={listId}";
            } else {
                param.put("defaultList", true);
                param.put("author", String.valueOf(user.getInternalId()));
                endpoint = "/tasks?defaultList={defaultList}&author={author}";
            }

            String response = restService.sendGetRequest(param, endpoint);
            BaseDataResponse<List<TaskDTO>> list = null;
            try {
                list = objectMapper.readValue(response, new TypeReference<BaseDataResponse<List<TaskDTO>>>() {
                });
                return list.getData();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public TaskDTO findOne(long chatId, int taskId) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {
            Map<String, Object> param = new HashMap<>();

            String endpoint = "/tasks/" + taskId;

            String response = restService.sendGetRequest(param, endpoint);
            BaseDataResponse<TaskDTO> dataResponse = null;
            try {
                dataResponse = objectMapper.readValue(response, new TypeReference<BaseDataResponse<TaskDTO>>() {
                });
                return dataResponse.getData();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public void save(long chatId, Map<String,String> params) {

        User user = userRepository.findByChatId(chatId);
        if (user != null) {

            Map<String,Object> jsonMap = new HashMap<>();
            jsonMap.put("status", 0);
            UserDTO author = new UserDTO();
            author.setId(user.getInternalId());
            jsonMap.put("author", author);

            try {
                for (String s : params.keySet()) {
                    switch (s) {
                        case "message":
                            jsonMap.put("text", params.get(s));
                            break;
                        case "important":
                            jsonMap.put("isImportant", Boolean.valueOf(params.get(s)));
                            break;
                        case "executor":
                            UserDTO executor = new UserDTO();
                            executor.setId(Integer.valueOf(params.get(s)));
                            jsonMap.put("executor", executor);
                            break;
                        case "list":
                            TasksListDTO parentList = new TasksListDTO();
                            parentList.setId(Integer.parseInt(params.get(s)));
                            jsonMap.put("parentList", parentList);
                            break;
                        default:
                            System.out.println(s + " - неизвестный параметр");
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            String endpoint = "/tasks";

            String response = restService.sendPostRequest(jsonMap,endpoint);
            BaseDataResponse<TaskDTO> list = null;
            try {
                list = objectMapper.readValue(response, new TypeReference<BaseDataResponse<TaskDTO>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("Пользователя не нашли в БД");
        }
    }


    public String done(long chatId, int taskId) {

        User user = userRepository.findByChatId(chatId);
        if (user != null) {

            TaskDTO task = findOne(chatId, taskId);

            if (task == null) {
                return "Задача не найдена";
            }

            if (task.getParentList() == null && task.getAuthor().getId() != user.getInternalId()) {
                return "Задача не найдена среди ваших задач";
            }

            Map<String,Object> jsonMap = new HashMap<>();
            if (task.getStatus() == 0) {
                jsonMap.put("status", 1);
            } else {
                jsonMap.put("status", 0);
            }

            String endpoint = "/tasks/" + taskId;

            String response = restService.sendPatchRequest(jsonMap,endpoint);
            BaseDataResponse<TaskDTO> dataResponse = null;
            try {
                dataResponse = objectMapper.readValue(response, new TypeReference<BaseDataResponse<TaskDTO>>() {});
                return EmojiParser.parseToUnicode("Статус задачи изменен :dizzy:");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("Пользователя не нашли в БД");
        }

        return "Что-то пошло не так";
    }

}
