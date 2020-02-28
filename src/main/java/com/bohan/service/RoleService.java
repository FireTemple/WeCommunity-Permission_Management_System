package com.bohan.service;

import com.bohan.entity.SysRole;
import com.bohan.vo.request.AddRoleReqVo;
import com.bohan.vo.request.RolePageReqVo;
import com.bohan.vo.respose.PageVo;

import java.util.List;

public interface RoleService {

    PageVo<SysRole> pageInfo(RolePageReqVo vo);
    SysRole addRole(AddRoleReqVo vo);
    List<SysRole> selectAll();
}
