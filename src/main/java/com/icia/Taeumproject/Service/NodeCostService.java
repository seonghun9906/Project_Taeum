package com.icia.Taeumproject.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icia.Taeumproject.Dao.NodeCostDao;
import com.icia.Taeumproject.Service.base.BaseService;
import com.icia.Taeumproject.Dto.NodeCost;
@Transactional(readOnly = true)
@Service
public class NodeCostService extends BaseService<NodeCost, Long, NodeCostDao> {
  
}
