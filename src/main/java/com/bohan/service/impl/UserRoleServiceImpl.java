package com.bohan.service.impl;

import com.bohan.entity.SysUserRole;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysUserRoleMapper;
import com.bohan.service.UserRoleService;
import com.bohan.vo.request.UserOwnRoleReqVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<String> getRoleIdsByUserId(String userId) {
        return sysUserRoleMapper.getRoleIdsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserRoleInfo(UserOwnRoleReqVo vo) {

        sysUserRoleMapper.removeRoleByUserId(vo.getUserId());
        if(vo.getRoleIds() == null || vo.getRoleIds().isEmpty()){
            return;
        }

        List<SysUserRole> list = new ArrayList<>();
        for (String roleId : vo.getRoleIds()){
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(vo.getUserId());
            sysUserRole.setCreateTime(new Date());
            sysUserRole.setId(UUID.randomUUID().toString());
            sysUserRole.setRoleId(roleId);
            list.add(sysUserRole);
        }
        int i = sysUserRoleMapper.batchInsertUserRole(list);
        if(i == 0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }

    @Override
    public List<String> getUserIdsByRoleIds(List<String> roleIds) {
        return sysUserRoleMapper.getUserIdsByRoleIds(roleIds);
    }

    @Override
    public List<String> getUserIdsByRoleId(String roleId) {
        return sysUserRoleMapper.getUserIdsByRoleId(roleId);
    }

    @Override
    public int removeUserRoleId(String roleId) {
        return sysUserRoleMapper.removeUserRoleId(roleId);
    }
}
