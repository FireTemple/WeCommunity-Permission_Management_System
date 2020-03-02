package com.bohan.mapper;

import com.bohan.entity.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    List<SysDept> selectAll();

    int updateRelationCode(@Param("oldStr") String oldStr, @Param("newStr")String newStr, @Param("relationCode") String relationCode);

    List<String> selectChildIds(String relationCode);

    int deleteDepts(Date updateTime, @Param("list") List<String> list);
}