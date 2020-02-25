package com.bohan.service.impl;

import com.alibaba.fastjson.JSON;
import com.bohan.entity.SysUser;
import com.bohan.mapper.SysUserMapper;
import com.bohan.service.HomeService;
import com.bohan.service.PermissionService;
import com.bohan.vo.respose.HomeRespVo;
import com.bohan.vo.respose.PermissionRespNodeVo;
import com.bohan.vo.respose.UserInfoRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PermissionServiceImpl permissionService;

    @Override
    public HomeRespVo getHome(String userid) {
        HomeRespVo homeRespVo = new HomeRespVo();

        /**
         * mock 数据
         */

//        String home="[{\"children\":[{\"children\":[{\"children\":[{\"children\":[{\"children\": [],\"id\":\"6\",\"title\":\"五级类目5-6\",\"url\":\"string\"}],\"id\":\"5\",\"title\":\"四级类目4- 5\",\"url\":\"string\"}],\"id\":\"4\",\"title\":\"三级类目3- 4\",\"url\":\"string\"}],\"id\":\"3\",\"title\":\"二级类目2- 3\",\"url\":\"string\"}],\"id\":\"1\",\"title\":\"类目1\",\"url\":\"string\"},{\"children\": [],\"id\":\"2\",\"title\":\"类目2\",\"url\":\"string\"}]";
//        String home="[\n" +
//                "    {\n" +
//                "        \"children\": [\n" +
//                "            {\n" +
//                "                \"children\": [],\n" +
//                "                \"id\": \"3\",\n" +
//                "                \"title\": \"菜单权限管理\",\n" +
//                "                \"url\": \"/index/menus\"\n" +
//                "            }\n" +
//                "        ],\n" +
//                "        \"id\": \"1\",\n" +
//                "        \"title\": \"组织管理\",\n" +
//                "        \"url\": \"string\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"children\": [],\n" +
//                "        \"id\": \"2\",\n" +
//                "        \"title\": \"类目2\",\n" +
//                "        \"url\": \"string\"\n" +
//                "    }\n" +
//                "]";
        List<PermissionRespNodeVo> list = permissionService.permissionTreeList(userid);
        homeRespVo.setMenus(list);
        SysUser user = sysUserMapper.selectByPrimaryKey(userid);
        UserInfoRespVo vo = new UserInfoRespVo();
        if(user != null){
            BeanUtils.copyProperties(user, vo);
            vo.setDeptName("bohan company");
        }
        homeRespVo.setUserInfoVo(vo);
        return homeRespVo;
    }
}
