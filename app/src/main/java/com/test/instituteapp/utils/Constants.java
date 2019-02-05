package com.test.instituteapp.utils;

public interface Constants {

    String API_BASE_URL = "http://www.krosscode.com/api/2018/";
    String API_REGISTER = API_BASE_URL + "post/registeredUser/addUser";
    String API_LOGIN = API_BASE_URL + "post/registeredUser/verifyUser";
    String API_GET_ALL_COURSES = API_BASE_URL + "get/registeredUser/getAllCourse/0";
    String API_GET_NOTIFICATION = API_BASE_URL + "get/registeredUser/getAllNotification/0";
    String API_ADD_NOTIFICATION = API_BASE_URL + "post/registeredUser/AddNotification";
    String API_ADD_ATTENDANCE = API_BASE_URL + "post/registeredUser/AddAttendance";
    String API_GET_ATTENDANCE = API_BASE_URL + "get/registeredUser/GetAttendanceByData";
    String API_GET_ALL_MESSAGES = API_BASE_URL + "get/registeredUser/getAllChatMessage/0";
    String API_ADD_MESSAGE = API_BASE_URL + "post/registeredUser/addChatMessage";
    String API_GET_MESSAGES_BY_ID = API_BASE_URL + "get/registeredUser/getChatMessageByChatMessageId";

    String MY_SH_PREF = "my_pref";
    String SH_PREF_KEY_MOBILE_NO = "mobile_no";
    String SH_PREF_KEY_NAME = "name";
    String SH_PREF_KEY_TYPE = "type";
}
