package com.icia.Taeumproject.Dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.icia.Taeumproject.Dto.NotificationDto;
import com.icia.Taeumproject.Dto.SearchDto;



@Mapper
public interface NotificationDao {

  void insertNotification(NotificationDto notification);

  List<NotificationDto> selectNotificationList(SearchDto sdto);

  void deleteNotification(int NOTIFICATION_ID);

  void deleteAllNotifications();

 
}
