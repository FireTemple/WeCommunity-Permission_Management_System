package com.bohan.service;

import com.bohan.entity.SysPermission;
import com.bohan.vo.request.PermissionAddReqVO;
import com.bohan.vo.respose.PermissionRespNodeVo;

import java.util.List;

public interface PermissionService {
    List<SysPermission> selectAll();
    List<PermissionRespNodeVo> selectAllMenuByTree();
    SysPermission addPermission(PermissionAddReqVO vo);
    List<PermissionRespNodeVo> permissionTreeList(String userid);
    List<PermissionRespNodeVo> selectAllTree();

}
