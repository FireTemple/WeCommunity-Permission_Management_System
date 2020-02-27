package com.bohan.mapper;

import com.bohan.entity.SysRole;
import com.bohan.entity.SysUserRole;
import com.bohan.vo.request.RolePageReqVo;

import java.util.List;

public interface SysUserRoleMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

}