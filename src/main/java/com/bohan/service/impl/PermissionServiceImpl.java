package com.bohan.service.impl;

import com.bohan.entity.SysPermission;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysPermissionMapper;
import com.bohan.service.PermissionService;
import com.bohan.vo.request.PermissionAddReqVO;
import com.bohan.vo.respose.PermissionRespNodeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private SysPermissionMapper sysPermissionMapper;


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
    public List<PermissionRespNodeVo> selectAllMenuByTree() {
        List<SysPermission> list = sysPermissionMapper.selectAll();
        List<PermissionRespNodeVo> result = new ArrayList<>();
        PermissionRespNodeVo respNodeVo = new PermissionRespNodeVo();
        respNodeVo.setId("0");
        respNodeVo.setTitle("默认顶级菜单");
        respNodeVo.setChildren(getTree(list));
        result.add(respNodeVo);
        System.out.println(result);
        return result;
    }

    private List<PermissionRespNodeVo> getTree(List<SysPermission> all){
        List<PermissionRespNodeVo> list = new ArrayList<>();
        if(all.isEmpty()) return list;

        for(SysPermission sysPermission : all){
            if(sysPermission.getPid().equals("0")){
                PermissionRespNodeVo respNodeVo = new PermissionRespNodeVo();
                BeanUtils.copyProperties(sysPermission, respNodeVo);
                respNodeVo.setTitle(sysPermission.getName());
                respNodeVo.setChildren(getChildExBtn(sysPermission.getId(),all));
                list.add(respNodeVo);
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
        return getTree(sysPermissionMapper.selectAll());
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
}
