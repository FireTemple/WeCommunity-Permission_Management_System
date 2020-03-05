package com.bohan.service.impl;

import com.bohan.config.TokenSetting;
import com.bohan.constant.Constant;
import com.bohan.entity.SysRole;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysRoleMapper;
import com.bohan.service.RedisService;
import com.bohan.service.RolePermissionService;
import com.bohan.service.RoleService;
import com.bohan.utils.PageUtil;
import com.bohan.vo.request.AddRoleReqVo;
import com.bohan.vo.request.RolePageReqVo;
import com.bohan.vo.request.RolePermissionOperationReqVo;
import com.bohan.vo.request.RoleUpdateReqVo;
import com.bohan.vo.respose.PageVo;
import com.bohan.vo.respose.PermissionRespNodeVo;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private RolePermissionServiceImpl rolePermissionService;

    @Autowired
    private PermissionServiceImpl permissionService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenSetting tokenSetting;


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

    @Override
    public SysRole detailInfo(String id) {
        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(id);
        if(sysRole == null){
            log.info("id 不合法 {}", id);
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }

        List<PermissionRespNodeVo> permissionNode = permissionService.selectAllTree();

        List<String> permissionByRoleIds = rolePermissionService.getPermissionByRoleId(id);
        Set<String> checkList = new HashSet<>(permissionByRoleIds);
        setChecked(permissionNode,checkList);

        sysRole.setPermissionRespNode(permissionNode);
        return sysRole;
    }

    private void setChecked(List<PermissionRespNodeVo> list, Set<String> checkList){

        for(PermissionRespNodeVo node:list){
            /**
             * 子集选中从它往上到跟目录都被选中，父级选中从它到它所有的叶子节点都会被选中
             * 这样我们就直接遍历最底层及就可以了
             */
            if(checkList.contains(node.getId())&&(node.getChildren()==null||node.getChildren().isEmpty())){
                node.setChecked(true);
            }
            setChecked((List<PermissionRespNodeVo>) node.getChildren(),checkList);

        }
    }

    @Override
    public void updateRole(RoleUpdateReqVo vo) {
        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(vo.getId());
        if(sysRole == null){
            log.error("id 不合法");
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }

        BeanUtils.copyProperties(vo, sysRole);
        sysRole.setUpdateTime(new Date());
        int i = sysRoleMapper.updateByPrimaryKeySelective(sysRole);
        if(i != 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        RolePermissionOperationReqVo reqVo = new RolePermissionOperationReqVo();
        reqVo.setRoleId(vo.getId());
        reqVo.setPermissionIds(vo.getPermissions());
        rolePermissionService.addRolePermission(reqVo);

        //通过角色Id拿到拥有该角色的用户Id
        List<String> userIdsByRoleId = userRoleService.getUserIdsByRoleId(vo.getId());
        if(!userIdsByRoleId.isEmpty()){
            for (String userId: userIdsByRoleId){
                redisService.set(Constant.JWT_REFRESH_KEY + userId, userId,tokenSetting.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
                redisService.delete(Constant.IDENTIFY_CACHE_KEY+ userId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(String roleId) {
        SysRole sysRole = new SysRole();

        sysRole.setId(roleId);
        sysRole.setDeleted(0);
        sysRole.setUpdateTime(new Date());
        int i = sysRoleMapper.updateByPrimaryKeySelective(sysRole);
        if(i != 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        rolePermissionService.removeByRoleId(roleId);

        List<String> userIdsByRoleId = userRoleService.getUserIdsByRoleId(roleId);
        userRoleService.removeUserRoleId(roleId);

        if(!userIdsByRoleId.isEmpty()){
            for(String userId : userIdsByRoleId){
                redisService.set(Constant.JWT_REFRESH_KEY + userId, userId, tokenSetting.getAccessTokenExpireTime().toMillis(),TimeUnit.MILLISECONDS);
                redisService.delete(Constant.IDENTIFY_CACHE_KEY+ userId);

            }
        }

    }

    @Override
    public List<String> getRoleNames(String userId) {
        List<SysRole> sysRoles = getRoleInfoByUserId(userId);
        if(null == sysRoles || sysRoles.isEmpty()) return null;
        List<String> list = new ArrayList<>();
        for (SysRole sysRole : sysRoles){
            list.add(sysRole.getName());
        }
        return list;
    }

    @Override
    public List<SysRole> getRoleInfoByUserId(String userId) {
        List<String> roleIds = userRoleService.getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()){
            return null;
        }
        return sysRoleMapper.getRoleInfoByIds(roleIds);
    }
}
