package com.icia.Taeumproject.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.icia.Taeumproject.Dao.NodeDao;
import com.icia.Taeumproject.Dto.Node;
import com.icia.Taeumproject.Service.base.BaseService;
@Transactional(readOnly = true)
@Service
public class NodeService extends BaseService<Node, Long, NodeDao> {
}
