package com.miko.napcat.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetFriendsWithCategoryResponse implements Serializable {
    private String status;
    private Integer retcode;
    private List<FriendCategory> data;
    private String message;
    private String wording;
    private Object echo;
    private String stream;

    @Data
    public static class FriendCategory implements Serializable {
        private Integer categoryId;
        private Integer categorySortId;
        private String categoryName;
        private Integer categoryMbCount;
        private Integer onlineCount;
        private List<Friend> buddyList;
    }

    @Data
    public static class Friend implements Serializable {
        private Integer birthday_year;
        private Integer birthday_month;
        private Integer birthday_day;
        private Long user_id;
        private Integer age;
        private String phone_num;
        private String email;
        private Integer category_id;
        private String nickname;
        private String remark;
        private String sex;
        private Integer level;
    }
}