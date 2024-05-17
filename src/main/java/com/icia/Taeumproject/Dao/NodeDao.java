package com.icia.Taeumproject.Dao;
import org.apache.ibatis.annotations.Mapper;

import com.icia.Taeumproject.Dao.base.BaseDao;
import com.icia.Taeumproject.Dto.Node;
@Mapper
public interface NodeDao extends BaseDao<Node, Long> {
}
