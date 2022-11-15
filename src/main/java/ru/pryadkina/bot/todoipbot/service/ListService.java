package ru.pryadkina.bot.todoipbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import ru.pryadkina.bot.todoipbot.dto.BaseDataResponse;
import ru.pryadkina.bot.todoipbot.dto.TasksListDTO;
import ru.pryadkina.bot.todoipbot.dto.UserDTO;
import ru.pryadkina.bot.todoipbot.model.User;
import ru.pryadkina.bot.todoipbot.repositories.UserRepository;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListService {

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final RestService restService;


    public ListService(ObjectMapper objectMapper, UserRepository userRepository, RestService restService) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.restService = restService;
    }

    public String get(long chatId, Map<String, String> params) {
        List<TasksListDTO> list = getList(chatId, params);

        StringBuilder message = new StringBuilder();
        message.append(EmojiParser.parseToUnicode(":ledger: ")).append("дефолтный список").append("\n");

        for (TasksListDTO tasksListDTO : list) {
            if (tasksListDTO.getStatus() != 1) {
                continue;
            }
            message.append(EmojiParser.parseToUnicode(":ledger: "))
                    .append(tasksListDTO.getId())
                    .append(" ")
                    .append(tasksListDTO.getName());
            if (tasksListDTO.getOwners() != null) {
                message.append(" - владелец: ").append(tasksListDTO.getOwners().get(0).getName());
            }
            if (tasksListDTO.getParticipants().size() > 0) {
                message.append(" - участники: ");
                for (UserDTO participant : tasksListDTO.getParticipants()) {
                    message.append(participant.getName()).append(" ");
                }
            }
            message.append("\n");
        }

        return message.toString();

    }

    public List<TasksListDTO> getList(long chatId, Map<String, String> params) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {

            Map<String, Object> param = new HashMap<>();
            param.put("userId", String.valueOf(user.getInternalId()));

            String endpoint = "/lists?userId={userId}";

            String response = restService.sendGetRequest(param, endpoint);
            BaseDataResponse<List<TasksListDTO>> list = null;
            try {
                list = objectMapper.readValue(response, new TypeReference<BaseDataResponse<List<TasksListDTO>>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return list.getData();

        } else {
            System.out.println("Пользователя не нашли в БД");
            return null;
        }
    }

    public void save(long chatId, String message) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {

            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("name", message);
            jsonMap.put("status", 1);

            Map<String, Object> owner = new HashMap<>();
            owner.put("id", user.getInternalId());
            Object[] owners = new Object[]{owner};
            jsonMap.put("owners", owners);

            String endpoint = "/lists";

            String response = restService.sendPostRequest(jsonMap, endpoint);
            BaseDataResponse<TasksListDTO> list = null;
            try {
                list = objectMapper.readValue(response, new TypeReference<BaseDataResponse<TasksListDTO>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        } else {
            System.out.println("Пользователя не нашли в БД");
        }
    }

    public TasksListDTO findOneList(long chatId, Map<String,String> params) {
        if (params.isEmpty()) {
            System.out.println("нужен id для поиска списка");
        }

        if (!params.containsKey("id")) {
            System.out.println("нужен id для поиска списка");
        }

        int id = Integer.parseInt(params.get("id"));

        List<TasksListDTO> usersList = getList(chatId, null);

        return usersList.stream().filter(tasksListDTO -> tasksListDTO.getId() == id).findAny().orElse(null);
    }

    public String addParticipant(long chatId, Integer participantId, Integer listId) {
        if (listId == 0) {
            return "Не указан id списка";
        }

        if (participantId == 0) {
            return "Не указан id участника";
        }

        int userId = userRepository.findByChatId(chatId).getInternalId();

        Map<String,String> params = new HashMap<>();
        params.put("id", String.valueOf(listId));

        TasksListDTO tasksListDTO = findOneList(chatId, params);

        if (tasksListDTO == null) {
            return "У вас нет такого списка";
        }

        if (tasksListDTO.getOwners().stream().filter(owner -> owner.getId() == participantId).count() > 0
            || tasksListDTO.getParticipants().stream().filter(participant -> participant.getId() == participantId).count() > 0) {
            return "Участник уже в списке";
        }

        if (tasksListDTO.getOwners().stream().filter(owner -> owner.getId() == userId).count() == 0) {
            return "Для добавления участника обратитесь к владельцу списка";
        }

        List<Integer> participantsList = tasksListDTO.getParticipants().stream().map(par -> par.getId()).distinct().collect(Collectors.toList());
        participantsList.add(participantId);

        Map<String, Object> jsonMap = new HashMap<>();
        Object[] participants = participantsList.toArray();
        jsonMap.put("participants", participants);

        String endpoint = "/lists/" + listId;

        String response = restService.sendPatchRequest(jsonMap, endpoint);

        BaseDataResponse<TasksListDTO> list = null;
        try {
            list = objectMapper.readValue(response, new TypeReference<BaseDataResponse<TasksListDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (list.getCode() == 0) {
            return EmojiParser.parseToUnicode("Участник добавлен :hatching_chick:");
        }

        return EmojiParser.parseToUnicode("Что-то пошло не так");

    }

    public String deleteParticipant(long chatId, Integer participantId, Integer listId) {
        if (listId == 0) {
            return "Не указан id списка";
        }

        if (participantId == 0) {
            return "Не указан id участника";
        }

        int userId = userRepository.findByChatId(chatId).getInternalId();

        Map<String,String> params = new HashMap<>();
        params.put("id", String.valueOf(listId));

        TasksListDTO tasksListDTO = findOneList(chatId, params);

        if (tasksListDTO == null) {
            return "У вас нет такого списка";
        }

        if (tasksListDTO.getOwners().stream().filter(owner -> owner.getId() == participantId).count() == 0
                && tasksListDTO.getParticipants().stream().filter(participant -> participant.getId() == participantId).count() == 0) {
            return "Участника нет в списке";
        }

        if (tasksListDTO.getOwners().stream().filter(owner -> owner.getId() == userId).count() == 0
            && userId != participantId) {
            return "Для удаления участника обратитесь к владельцу списка";
        }

        List<Integer> participantsList = tasksListDTO.getParticipants().stream().map(par -> par.getId()).distinct().collect(Collectors.toList());
        participantsList.remove(participantId);

        Map<String, Object> jsonMap = new HashMap<>();
        Object[] participants = participantsList.toArray();
        jsonMap.put("participants", participants);

        String endpoint = "/lists/" + listId;

        String response = restService.sendPatchRequest(jsonMap, endpoint);

        BaseDataResponse<TasksListDTO> list = null;
        try {
            list = objectMapper.readValue(response, new TypeReference<BaseDataResponse<TasksListDTO>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (list.getCode() == 0) {
            return EmojiParser.parseToUnicode("Участник удален :ghost:");
        }

        return EmojiParser.parseToUnicode("Что-то пошло не так");

    }

}
