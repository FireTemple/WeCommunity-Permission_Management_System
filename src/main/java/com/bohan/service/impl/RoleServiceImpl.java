package com.bohan.service.impl;

import com.bohan.entity.SysRole;
import com.bohan.mapper.SysRoleMapper;
import com.bohan.service.RoleService;
import com.bohan.utils.PageUtil;
import com.bohan.vo.request.RolePageReqVo;
import com.bohan.vo.respose.PageVo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public PageVo<SysRole> pageInfo(RolePageReqVo vo) {
        PageHelper.offsetPage(vo.getPageNum(), vo.getPageSize());
        List<SysRole> sysRoles = sysRoleMapper.selectAll(vo);
        return PageUtil.getPageVo(sysRoles);
    }
}
