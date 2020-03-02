package com.bohan.mapper;

import com.bohan.entity.SysLog;
import com.bohan.vo.request.SysLogPageReqVo;

import java.util.List;

public interface SysLogMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

    List<SysLog> selectAll(SysLogPageReqVo vo);

    int batchDeleteLog(List<String> logIds);

}