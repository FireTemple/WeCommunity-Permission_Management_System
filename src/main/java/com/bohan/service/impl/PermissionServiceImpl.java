package com.bohan.service.impl;

import com.bohan.config.TokenSetting;
import com.bohan.constant.Constant;
import com.bohan.entity.SysPermission;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysPermissionMapper;
import com.bohan.service.PermissionService;
import com.bohan.service.RedisService;
import com.bohan.vo.request.PermissionAddReqVO;
import com.bohan.vo.request.PermissionUpdateReqVo;
import com.bohan.vo.respose.PermissionRespNodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private RolePermissionServiceImpl rolePermissionService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TokenSetting tokenSetting;

    @Override
    public List<SysPermission> selectAll() {
        List<SysPermission> sysPermissions = sysPermissionMapper.selectAll();
        if(!sysPermissions.isEmpty()){
            for (SysPermission sysPermission: sysPermissions) {
                SysPermission parent = sysPermissionMapper.selectByPrimaryKey(sysPermission.getPid());
                if(parent != null){
                    sysPermission.setPidName(parent.getName());

                }
            }
        }
        return sysPermissionMapper.selectAll();
    }

    @Override
    public List<PermissionRespNodeVo> selectAllTree() {
        return getTree(selectAll(),false);
    }

    @Override
    public List<PermissionRespNodeVo> selectAllMenuByTree() {
        List<SysPermission> list = sysPermissionMapper.selectAll();
        List<PermissionRespNodeVo> result = new ArrayList<>();
        PermissionRespNodeVo respNodeVo = new PermissionRespNodeVo();
        respNodeVo.setId("0");
        respNodeVo.setTitle("默认顶级菜单");
        respNodeVo.setChildren(getTree(list, true));
        result.add(respNodeVo);
        System.out.println(result);
        return result;
    }

    /**
     * flag == true 递归到菜单 反之递归到按钮
     * @param all
     * @param flag
     * @return
     */
    private List<PermissionRespNodeVo> getTree(List<SysPermission> all, boolean flag){
        List<PermissionRespNodeVo> list = new ArrayList<>();
        if(all == null || all.isEmpty()) return list;

        for(SysPermission sysPermission : all){
            if(sysPermission.getPid().equals("0")){
                PermissionRespNodeVo respNodeVo = new PermissionRespNodeVo();
                BeanUtils.copyProperties(sysPermission, respNodeVo);
                respNodeVo.setTitle(sysPermission.getName());
                if (flag == true) {
                    respNodeVo.setChildren(getChildExBtn(sysPermission.getId(),all));
                }else{
                    respNodeVo.setChildren(getChild(sysPermission.getId(),all));
                }
                list.add(respNodeVo);
            }
        }
        return list;
    }

    /**
     * 递归遍历所有数据
     * @param id
     * @param all
     * @return
     */
    private List<PermissionRespNodeVo> getChild(String id,List<SysPermission> all){

        List<PermissionRespNodeVo> list=new ArrayList<>();
        for (SysPermission s:
                all) {
            if(s.getPid().equals(id)){
                PermissionRespNodeVo respNodeVO=new PermissionRespNodeVo();
                BeanUtils.copyProperties(s,respNodeVO);
                respNodeVO.setTitle(s.getName());
                respNodeVO.setChildren(getChild(s.getId(),all));
                list.add(respNodeVO);
            }
        }
        return list;
    }

    private List<PermissionRespNodeVo> getChildExBtn(String id, List<SysPermission> all){
        List<PermissionRespNodeVo> list = new ArrayList<>();
        for(SysPermission sysPermission : all){
            if(sysPermission.getPid().equals(id) && sysPermission.getType() != 3){
                PermissionRespNodeVo respNodeVo = new PermissionRespNodeVo();
                BeanUtils.copyProperties(sysPermission, respNodeVo);
                respNodeVo.setTitle(sysPermission.getName());
                respNodeVo.setChildren(getChildExBtn(sysPermission.getId(),all));
                list.add(respNodeVo);
            }
        }
        return list;
    }

    @Override
    public SysPermission addPermission(PermissionAddReqVO vo) {
        SysPermission sysPermission=new SysPermission();
        BeanUtils.copyProperties(vo,sysPermission);
        verifiedParentType(sysPermission);
        sysPermission.setId(UUID.randomUUID().toString());
        sysPermission.setCreateTime(new Date());
        int insert = sysPermissionMapper.insertSelective(sysPermission);
        if(insert!=1){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        return sysPermission;
    }

    @Override
    public List<PermissionRespNodeVo> permissionTreeList(String userid) {
        List<SysPermission> list = getPermission(userid);
        return getTree(list, true);
    }

    private void verifiedParentType(SysPermission sysPermission){
        SysPermission parent = sysPermissionMapper.selectByPrimaryKey(sysPermission.getPid());
        switch (sysPermission.getType()){
            case 1:
                if(parent!=null){
                    if(parent.getType()!=1){
                        throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR);
                    }
                }else if (!sysPermission.getPid().equals("0")){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_CATALOG_ERROR);
                }
                break;
            case 2:
                if(parent==null||parent.getType()!=1){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_MENU_ERROR);
                }
                if(StringUtils.isEmpty(sysPermission.getUrl())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_NOT_NULL);
                }
                break;
            case 3:
                if(parent==null||parent.getType()!=2){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_BTN_ERROR);
                }
                if(StringUtils.isEmpty(sysPermission.getPerms())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_PERMS_NULL);
                }
                if(StringUtils.isEmpty(sysPermission.getUrl())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_NOT_NULL);
                }
                if(StringUtils.isEmpty(sysPermission.getMethod())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_METHOD_NULL);
                }
                if(StringUtils.isEmpty(sysPermission.getCode())){
                    throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_URL_CODE_NULL);
                }
                break;
        }


    }

    @Override
    public void updatePermission(PermissionUpdateReqVo vo) {
        SysPermission update = new SysPermission();
        BeanUtils.copyProperties(vo, update);

        verifiedParentType(update);

        SysPermission sysPermission = sysPermissionMapper.selectByPrimaryKey(vo.getPid());
        if(sysPermission == null){
            log.info("传入的id在数据库中不存在");
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }
        if(!sysPermission.getPid().equals(vo.getPid())){
            List<SysPermission> sysPermissions = sysPermissionMapper.selectChild(vo.getId());
            if(!sysPermissions.isEmpty()){
                throw new BusinessException(BaseResponseCode.OPERATION_MENU_PERMISSION_UPDATE);
            }
        }

        update.setUpdateTime(new Date());

        int i = sysPermissionMapper.updateByPrimaryKeySelective(update);
        if(i == 0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        if(!sysPermission.getPerms().equals(vo.getPerms())){
            List<String> roleIdsByPermissionIds =  rolePermissionService.getRoleIdsByPermissionId(vo.getPerms());
            if(!roleIdsByPermissionIds.isEmpty()){
                List<String> userIds = userRoleService.getUserIdsByRoleIds(roleIdsByPermissionIds);
                if(!userIds.isEmpty()){
                    for (String userId : userIds) {
                        redisService.set(Constant.JWT_REFRESH_KEY + userId, userId, tokenSetting.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
                        redisService.delete(Constant.IDENTIFY_CACHE_KEY+userId);

                    }
                }
            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(String permissionId) {
        List<SysPermission> sysPermissions = sysPermissionMapper.selectChild(permissionId);
        if(!sysPermissions.isEmpty()){
            throw new BusinessException(BaseResponseCode.ROLE_PERMISSION_RELATION);
        }

        rolePermissionService.removeRoleByPermissionId(permissionId);

        SysPermission sysPermission = new SysPermission();
        sysPermission.setUpdateTime(new Date());
        sysPermission.setDeleted(0);
        sysPermission.setId(permissionId);
        int i = sysPermissionMapper.updateByPrimaryKeySelective(sysPermission);
        System.out.println(i);
        if(i == 0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }

        List<String> roleIdsByPermissionIds =  rolePermissionService.getRoleIdsByPermissionId(permissionId);
        if(!roleIdsByPermissionIds.isEmpty()){
            List<String> userIds = userRoleService.getUserIdsByRoleIds(roleIdsByPermissionIds);
            if(!userIds.isEmpty()){
                for (String userid : userIds) {
                    redisService.set(Constant.JWT_REFRESH_KEY + userid, userid, tokenSetting.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
                    redisService.delete(Constant.IDENTIFY_CACHE_KEY+ userid);
                }
            }
        }
    }

    @Override
    public Set<String> getPermissionByUserId(String userId) {

        List<SysPermission> list = getPermission(userId);

        Set<String> permissions=new HashSet<>();
        if (null==list||list.isEmpty()){
            return null;
        }

        for (SysPermission sysPermission:list) {
            if (!StringUtils.isEmpty(sysPermission.getPerms())) {
                permissions.add(sysPermission.getPerms());
            }
        }
        return permissions;
    }

    @Override
    public List<SysPermission> getPermission(String userId) {
        List<String> roleIds = userRoleService.getRoleIdsByUserId(userId);
        if(roleIds.isEmpty()){
            return null;
        }
        List<String> permissionIds= rolePermissionService.getPermissionIdsByRoles(roleIds);
        if (permissionIds.isEmpty()){
            return null;
        }
        List<SysPermission> result=sysPermissionMapper.selectInfoByIds(permissionIds);
        return result;
    }
}
