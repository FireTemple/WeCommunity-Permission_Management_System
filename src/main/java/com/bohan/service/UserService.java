package com.bohan.service;

import com.bohan.entity.SysUser;
import com.bohan.vo.request.LoginReqVo;
import com.bohan.vo.request.UserPageReqVO;
import com.bohan.vo.respose.LoginRespVo;
import com.bohan.vo.respose.PageVo;

import java.util.List;

public interface UserService {

    LoginRespVo login(LoginReqVo vo);

    void logout(String accessToken, String refreshToken);

    PageVo<SysUser> pageInfo(UserPageReqVO vo);

}
