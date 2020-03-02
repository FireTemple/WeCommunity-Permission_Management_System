package com.bohan.service;

import com.bohan.entity.SysUser;
import com.bohan.vo.request.*;
import com.bohan.vo.respose.LoginRespVo;
import com.bohan.vo.respose.PageVo;
import com.bohan.vo.respose.UserOwnRoleRespVo;

import java.util.List;

public interface UserService {

    LoginRespVo login(LoginReqVo vo);

    void logout(String accessToken, String refreshToken);

    PageVo<SysUser> pageInfo(UserPageReqVO vo);

    void addUser(UserAddReqVo vo);

    UserOwnRoleRespVo getUserOwnRole(String userId);

    void setUserOwnRole(UserOwnRoleReqVo vo);

    String refreshToken(String refreshToken);

    void updateUserInfo(UserUpdateReqVo vo, String operatorId);

    void deleteUsers(List<String> list, String operationId);

    List<SysUser> selectUserInfoByDeptIds(List<String> deptIds);
}
