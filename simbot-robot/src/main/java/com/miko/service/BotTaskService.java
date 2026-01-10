package com.miko.service;

import com.miko.entity.BotTaskModel;
import com.miko.mapper.BotTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BotTaskService {

    private final BotTaskMapper botTaskMapper;

    public List<BotTaskModel> getAllActive(){
        return botTaskMapper.getAllActive();
    }
}
