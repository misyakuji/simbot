package com.miko.mapper;

import com.miko.entity.BotTaskModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BotTaskMapper {

    @Select("SELECT `TARGET_TYPE`,`TARGET_ID`,`TYPE`,`CONTENT` FROM `bot_task` WHERE `ACTIVE` = '1'")
    List<BotTaskModel> getAllActive();
}
