package com.bohan.service;

import com.bohan.entity.SysRole;
import com.bohan.vo.request.AddRoleReqVo;
import com.bohan.vo.request.RolePageReqVo;
import com.bohan.vo.respose.PageVo;

public interface RoleService {

    PageVo<SysRole> pageInfo(RolePageReqVo vo);
    SysRole addRole(AddRoleReqVo vo);
}
