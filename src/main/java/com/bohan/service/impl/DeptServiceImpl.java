package com.bohan.service.impl;


import com.bohan.constant.Constant;
import com.bohan.entity.SysDept;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysDeptMapper;
import com.bohan.service.DeptService;
import com.bohan.service.RedisService;
import com.bohan.utils.CodeUtil;
import com.bohan.vo.request.DeptAddReqVo;
import com.bohan.vo.respose.DeptRespNodeVo;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DeptServiceImpl implements DeptService {

    @Autowired
    private SysDeptMapper sysDeptMapper;


    @Autowired
    private RedisService redisService;

    @Override
    public List<SysDept> selectAll() {

        /**
         * 查询到所有的 父级名称 并且设置
         */
        List<SysDept> list = sysDeptMapper.selectAll();
        for (SysDept sysDept : list){
            SysDept parent = sysDeptMapper.selectByPrimaryKey(sysDept.getPid());
            if(parent != null){
                sysDept.setPidName(parent.getName());
            }

        }
        return list;
    }


    @Override
    public List<DeptRespNodeVo> deptTreeList() {
        List<SysDept> list=sysDeptMapper.selectAll();
//        //我要想去掉这个部门的叶子节点，直接在数据源移除这个部门就可以了
//        if(!StringUtils.isEmpty(deptId)&&!list.isEmpty()){
//            for (SysDept s: list) {
//                if(s.getId().equals(deptId)){
//                    list.remove(s);
//                    break;
//                }
//            }
//        }
        DeptRespNodeVo respNodeVO=new DeptRespNodeVo();
        respNodeVO.setId("0");
        respNodeVO.setTitle("默认顶级部门");
        respNodeVO.setChildren(getTree(list));
        List<DeptRespNodeVo> result=new ArrayList<>();
        result.add(respNodeVO);
        return result;
    }

    private List<DeptRespNodeVo> getTree(List<SysDept> all){
        List<DeptRespNodeVo> list=new ArrayList<>();
        for (SysDept s:
                all) {
            if(s.getPid().equals("0")){
                DeptRespNodeVo respNodeVO=new DeptRespNodeVo();
                respNodeVO.setId(s.getId());
                respNodeVO.setTitle(s.getName());
                respNodeVO.setChildren(getChild(s.getId(),all));
                list.add(respNodeVO);
            }
        }
        return list;
    }

    private List<DeptRespNodeVo> getChild(String id,List<SysDept> all){
        List<DeptRespNodeVo> list=new ArrayList<>();
        for (SysDept s :
                all) {
            if(s.getPid().equals(id)){
                DeptRespNodeVo deptRespNodeVO=new DeptRespNodeVo();
                deptRespNodeVO.setId(s.getId());
                deptRespNodeVO.setTitle(s.getName());
                deptRespNodeVO.setChildren(getChild(s.getId(),all));
                list.add(deptRespNodeVO);

            }
        }
        return list;
    }


    @Override
    public SysDept addDept(DeptAddReqVo vo) {

        String relationCode;
        long deptCount = redisService.incrby(Constant.DEPT_CODE_KEY, 1);
        String deptCode = CodeUtil.deptCode(String.valueOf(deptCount), 7, "0");
        SysDept parent = sysDeptMapper.selectByPrimaryKey(vo.getPid());
        if(vo.getPid().equals("0")){
            relationCode = deptCode;
        }else if(parent == null){
            log.info("父级不存在 {}", vo.getPid());
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }else {
            relationCode = parent.getRelationCode() + deptCode;
        }

        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(vo, sysDept);
        sysDept.setId(UUID.randomUUID().toString());
        sysDept.setCreateTime(new Date());
        sysDept.setRelationCode(relationCode);
        int i = sysDeptMapper.insertSelective(sysDept);
        if(i != 1) throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        return sysDept;
    }
}
