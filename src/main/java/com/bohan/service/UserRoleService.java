package com.bohan.service;

import com.bohan.vo.request.UserOwnRoleReqVo;


import java.util.List;

public interface UserRoleService {
    List<String> getRoleIdsByUserId(String userId);

    void addUserRoleInfo(UserOwnRoleReqVo vo);
}
