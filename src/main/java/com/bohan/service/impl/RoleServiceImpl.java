package com.bohan.service.impl;

import com.bohan.entity.SysRole;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysRoleMapper;
import com.bohan.service.RolePermissionService;
import com.bohan.service.RoleService;
import com.bohan.utils.PageUtil;
import com.bohan.vo.request.AddRoleReqVo;
import com.bohan.vo.request.RolePageReqVo;
import com.bohan.vo.request.RolePermissionOperationReqVo;
import com.bohan.vo.respose.PageVo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public PageVo<SysRole> pageInfo(RolePageReqVo vo) {
        PageHelper.offsetPage(vo.getPageNum(), vo.getPageSize());
        List<SysRole> sysRoles = sysRoleMapper.selectAll(vo);
        return PageUtil.getPageVo(sysRoles);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole addRole(AddRoleReqVo vo) {

        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(vo, sysRole);
        sysRole.setId(UUID.randomUUID().toString());
        sysRole.setCreateTime(new Date());
        int i = sysRoleMapper.insertSelective(sysRole);
        if(i != 1){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        if(!vo.getPermissions().isEmpty() && vo.getPermissions() != null){
            RolePermissionOperationReqVo operationReqVo = new RolePermissionOperationReqVo();
            operationReqVo.setRoleId(sysRole.getId());
            operationReqVo.setPermissionIds(vo.getPermissions());
            rolePermissionService.addRolePermission(operationReqVo);
        }

        return sysRole;
    }

    @Override
    public List<SysRole> selectAll() {
        /**
         * 这里传一个空是为了跳过所有mapper层里面的判断
         */
       return sysRoleMapper.selectAll(new RolePageReqVo());
    }
}
