package com.bohan.service.impl;

import com.bohan.entity.SysLog;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysLogMapper;
import com.bohan.service.LogService;
import com.bohan.utils.PageUtil;
import com.bohan.vo.request.SysLogPageReqVo;
import com.bohan.vo.respose.PageVo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public PageVo<SysLog> pageInfo(SysLogPageReqVo vo) {
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        return PageUtil.getPageVo(sysLogMapper.selectAll(vo));
    }

    @Override
    public void deleteLog(List<String> logIds) {
        int i = sysLogMapper.batchDeleteLog(logIds);
        if(i == 0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }
}
