package com.bohan.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TestVo {


    @ApiModelProperty(value = "name")
    @NotBlank(message = "name can not be blank")
    private String name;

    @ApiModelProperty(value = "age")
    @NotNull(message = "age can not be null")
    private Integer age;

    @ApiModelProperty(value = "id list")
    @NotEmpty(message = "id list can not be empty")
    private List<Integer> ids;
}
