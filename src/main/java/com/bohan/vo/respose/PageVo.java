package com.bohan.vo.respose;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageVo<T>{


    @ApiModelProperty(value = "总记录数")
    private Long totalRow;

    @ApiModelProperty(value = "总页数")
    private Integer totalPages;

    @ApiModelProperty(value = "当前页数")
    private Integer pageNum;

    @ApiModelProperty(value = "每页的记录数")
    private Integer pageSize;

    @ApiModelProperty(value = "当前页记录数")
    private Integer curPageSize;

    @ApiModelProperty(value = "数据列表")
    private List<T> list;
}
