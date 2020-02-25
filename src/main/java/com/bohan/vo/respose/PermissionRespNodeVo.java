package com.bohan.vo.respose;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PermissionRespNodeVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "redirect url ")
    private String url;

    @ApiModelProperty(value = "permission title")
    private String title;

    @ApiModelProperty(value = "permission list")
    private List<?> children;

    @ApiModelProperty(value = "默认开启展开")
    private boolean spread = true;
}
