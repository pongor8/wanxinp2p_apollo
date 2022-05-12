package cn.itcast.wanxinp2p.search.service;

import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectQueryParamsDTO;
import cn.itcast.wanxinp2p.common.domain.PageVO;

public interface ProjectIndexService {
    PageVO<ProjectDTO> queryProjectIndex(ProjectQueryParamsDTO projectQueryParamsDTO,
                                         Integer pageNo, Integer pageSize,
                                         String sortBy, String order);
}
