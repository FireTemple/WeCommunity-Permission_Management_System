package com.bohan.vo.respose;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class HomeRespVo {

    @ApiModelProperty(value = "user information")
    private UserInfoRespVo userInfoVo;

    @ApiModelProperty(value = "navbar information")
    private List<PermissionRespNodeVo> menus;
}
