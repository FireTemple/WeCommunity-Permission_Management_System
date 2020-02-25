package com.bohan.utils;


import com.bohan.vo.respose.PageVo;
import com.github.pagehelper.Page;

import java.util.List;

public class PageUtil {
    private PageUtil(){}

    public static <T>PageVo<T> getPageVo(List<T> list){
        PageVo<T> result = new PageVo<>();
        if(list instanceof Page){
            Page<T> page = (Page<T>) list;
            result.setTotalPages(page.getPages());
            result.setTotalRow(page.getTotal());
            result.setPageNum(page.getPageNum());
            result.setPageSize(page.size());
            result.setCurPageSize(page.getPageSize());
            result.setList(page.getResult());
        }
        return result;
    }
}
