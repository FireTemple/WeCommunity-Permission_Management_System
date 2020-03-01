package com.bohan.service;


import com.bohan.vo.request.RolePermissionOperationReqVo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RolePermissionService {
    void addRolePermission(RolePermissionOperationReqVo vo);
    List<String> getRoleIdsByPermissionId(String permissionId);
    int removeRoleByPermissionId(String permissionId);

    List<String> getPermissionByRoleId(String roleId);

    int removeByRoleId(String roleId);
}
