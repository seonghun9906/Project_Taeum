package com.icia.Taeumproject.Dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.icia.Taeumproject.Dto.ApplyDto;
import com.icia.Taeumproject.Dto.DispatchDto;
import com.icia.Taeumproject.Dto.DriverDto;
import com.icia.Taeumproject.Dto.DriverFileDto;

@Mapper
public interface DriverDao {

	int getTodayTraffic(int m_id);

	int getMonthTraffic(int m_id);

	int getTotalTraffic(int m_id);

	List<DriverDto> getPassengerList(int m_id);

	List<ApplyDto> getRouteList(int m_id);

	String getDriverImage(@Param("M_ID") int M_ID);
	
	DriverDto getDriverInfo(int m_id);

	DriverDto getInfo(int m_id);

	void insertFile(DriverFileDto dfd);

	void insertDriver(DriverDto driver);

	void updateDriver(DriverDto driver);

	void updateDriverProfile(int mid);

	void updateConfirm(DispatchDto dispatch);
	
	void updateNodeConfirm(DispatchDto dispatch);

	void updateCancle(DispatchDto dispatch);

	void updateNodeCancle(DispatchDto dispatch);

	void insertStwork(int m_id);

	void updateEndwork(int m_id);

	void insertCommute(int m_id, int dr_id);

	void updateCommute(int dr_id);

  void deleteDriverImage(Integer m_ID, String imageNum);

  void deleteDispatch(int dr_id);

  void deleteCommute(int dr_id);










}
