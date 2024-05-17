package com.icia.Taeumproject.Dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.icia.Taeumproject.Dto.DispatchDto;
import com.icia.Taeumproject.Dto.DriverDto;
import com.icia.Taeumproject.Dto.DrivermanagementDto;
import com.icia.Taeumproject.Dto.Node;

@Mapper
public interface MainDao {

   public void insert (Node node);
   


  public List<Node> selectList();

  public List<Node> endSelectList();
  
  public void updateRide( Long ride, Long id);




  public void updateDelivery(Node node)  ;




  public List<Node> selectRideList(int ride);



  public List<Node> selectLocaldate(String A_DATE);



  public List<DriverDto> selectDriverList(String DR_AREA);



  public List<Node> selectNodeArea(@Param("address")String address, @Param("A_DATE")String A_DATE);



  public void insertDispatch(DispatchDto dispatchDto);



  public List<DispatchDto> GetDriverList(Integer dr_ID);

  public List<DispatchDto> getDispatch(String date);


  public List<DrivermanagementDto> DRMTList(Integer dR_ID);
  
  public List<Node> getNodeList(int M_ID);


  // 신청 내역 취소 시 Node도 삭제
  public void cancelNode(int m_Id, String A_NAME, String a_Date);

  public List<Node> selectNodeListToday(int dR_ID, String formattedDate);


  // 탈퇴 회원 노드 삭제
  public void withDrawal(int m_id);



  public List<DispatchDto> getUpdateDelivery(String selectedTime, int ride);



  public void deleteConfirm(int ride, String selectedTime, int i);



  public void updateConfirm(int ride, String selectedTime, Integer cycle);












  
  
  
}
