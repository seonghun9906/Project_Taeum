package com.icia.Taeumproject.Dao;
import org.apache.ibatis.annotations.Mapper;

import com.icia.Taeumproject.Dao.base.BaseDao;
import com.icia.Taeumproject.Dto.NodeCost;
@Mapper
public interface NodeCostDao extends BaseDao<NodeCost, Long> {
}
