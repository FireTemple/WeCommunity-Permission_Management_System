package com.bohan.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: UserUpdateDetailInfoReqVo
 * TODO:类文件简单描述
 * @Author: 小霍
 * @UpdateUser: 小霍
 * @Version: 0.0.1
 */
@Data
public class UserUpdateDetailInfoReqVo {
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "性别(1.男 2.女)")
    private Integer sex;
    @ApiModelProperty(value = "真实名称")
    private String realName;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "账户状态(1.正常 2.锁定 )")
    private Integer status;
}
