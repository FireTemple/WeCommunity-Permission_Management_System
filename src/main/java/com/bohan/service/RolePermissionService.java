package com.bohan.service;


import com.bohan.vo.request.RolePermissionOperationReqVo;
import org.springframework.stereotype.Service;

public interface RolePermissionService {
    void addRolePermission(RolePermissionOperationReqVo vo);

}
