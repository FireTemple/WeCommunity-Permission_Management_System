package com.bohan.service;

import com.bohan.entity.SysDept;
import com.bohan.vo.request.DeptAddReqVo;
import com.bohan.vo.request.DeptUpdateReqVo;
import com.bohan.vo.respose.DeptRespNodeVo;

import java.util.List;

public interface DeptService {

    List<SysDept> selectAll();
    List<DeptRespNodeVo> deptTreeList(String deptId);

    SysDept addDept(DeptAddReqVo vo);

    void updateDept(DeptUpdateReqVo vo);

    void deleteDept(String deptIds);
}
