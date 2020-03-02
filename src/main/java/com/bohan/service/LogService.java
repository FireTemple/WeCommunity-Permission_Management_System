package com.bohan.service;


import com.bohan.entity.SysLog;
import com.bohan.vo.request.SysLogPageReqVo;
import com.bohan.vo.respose.PageVo;

import java.util.List;

public interface LogService {

    PageVo<SysLog> pageInfo(SysLogPageReqVo vo);

    void deleteLog(List<String> logIds);
}
