package ru.pryadkina.bot.todoipbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pryadkina.bot.todoipbot.dto.BaseDataResponse;
import ru.pryadkina.bot.todoipbot.dto.UserDTO;
import ru.pryadkina.bot.todoipbot.model.User;
import ru.pryadkina.bot.todoipbot.repositories.UserRepository;


import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final RestService restService;

    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper, RestService restService) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.restService = restService;
    }

    public User findByChatId(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public String save(String name, User user) {

        if (userRepository.findByChatId(user.getChatId()) == null) {

            Map<String,Object> jsonMap = new HashMap<>();
            jsonMap.put("name",name);
            jsonMap.put("status", 1);

            String endpoint = "/users";

            String response = restService.sendPostRequest(jsonMap,endpoint);

            BaseDataResponse<UserDTO> list = null;
            try {
                list = objectMapper.readValue(response, new TypeReference<BaseDataResponse<UserDTO>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            user.setInternalId(list.getData().getId());

            userRepository.save(user);
            return EmojiParser.parseToUnicode("Вы успешно зарегистрированы :ok_hand: \n\n Ваш id: ") + user.getInternalId();

        } else {
            return EmojiParser.parseToUnicode("Вы были зарегистрированы ранее :face_with_monocle: \n\n Ваш id: ") + userRepository.findByChatId(user.getChatId()).getInternalId();
        }


    }

}
