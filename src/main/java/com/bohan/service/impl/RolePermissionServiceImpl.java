package com.bohan.service.impl;


import com.bohan.entity.SysRolePermission;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysRolePermissionMapper;
import com.bohan.service.RolePermissionService;
import com.bohan.vo.request.RolePermissionOperationReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class RolePermissionServiceImpl implements RolePermissionService {
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public void addRolePermission(RolePermissionOperationReqVo vo) {
        if(vo.getPermissionIds().isEmpty() || vo.getPermissionIds() == null) return;

        List<SysRolePermission> list = new ArrayList<>();
         for (String permissionId : vo.getPermissionIds()){
             SysRolePermission rolePermission = new SysRolePermission();
             rolePermission.setId(UUID.randomUUID().toString());
             rolePermission.setCreateTime(new Date());
             rolePermission.setPermissionId(permissionId);
             rolePermission.setRoleId(vo.getRoleId());
             list.add(rolePermission);
         }
        int i = sysRolePermissionMapper.batchInsertRolePermission(list);
         if(i == 0){
             throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
         }





    }
}
