package com.bohan.mapper;

import com.bohan.entity.SysRolePermission;

import java.util.List;

public interface  SysRolePermissionMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysRolePermission record);

    int insertSelective(SysRolePermission record);

    SysRolePermission selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysRolePermission record);

    int updateByPrimaryKey(SysRolePermission record);

    int batchInsertRolePermission(List<SysRolePermission> list);

    List<String> getRoleIdByPermissionId(String permissionId);

    int removeByPermissionId(String permissionId);

    List<String> getPermissionIdsByRoleId(String roleId);

    int removeByRoleId(String roleId);

    List<String> getPermissionIdsByRoles(List<String> roleIds);
}