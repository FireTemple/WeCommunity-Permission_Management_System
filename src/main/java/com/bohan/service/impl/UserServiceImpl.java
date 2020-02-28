package com.bohan.service.impl;

import com.bohan.constant.Constant;
import com.bohan.entity.SysDept;
import com.bohan.entity.SysUser;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysDeptMapper;
import com.bohan.mapper.SysUserMapper;
import com.bohan.service.RedisService;
import com.bohan.service.UserService;
import com.bohan.utils.JwtTokenUtil;
import com.bohan.utils.PageUtil;
import com.bohan.utils.PasswordUtils;
import com.bohan.vo.request.LoginReqVo;
import com.bohan.vo.request.UserAddReqVo;
import com.bohan.vo.request.UserPageReqVO;
import com.bohan.vo.respose.LoginRespVo;
import com.bohan.vo.respose.PageVo;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {



    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    /**
     * 用一个vo登录里面有账号，密码，用户类型
     * 返回的是一个经过验证的账号拥有所有用户信息以及两个Token
     * @param vo
     * @return
     */
    @Override
    public LoginRespVo login(LoginReqVo vo) {
        // user是 真正找到的user
        SysUser user = sysUserMapper.getUserInfoByName(vo.getUsername());

        if(user == null){
            throw new BusinessException(BaseResponseCode.ACCOUNT_ERROR);
        }

        if(user.getStatus() == 2){
            throw new BusinessException(BaseResponseCode.ACCOUNT_LOCK);

        }

        if(!PasswordUtils.matches(user.getSalt(), vo.getPassword(), user.getPassword())){
            throw new BusinessException(BaseResponseCode.ACCOUNT_PASSWORD_ERROR);
        }


        LoginRespVo loginRespVo = new LoginRespVo();
        BeanUtils.copyProperties(user,loginRespVo);
        // 下面这个claims是为了创建Token来创建的 里面保存了部分user的信息
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constant.JWT_USER_NAME,loginRespVo.getUsername());
        claims.put(Constant.JWT_PERMISSIONS_KEY,getPermissionByUserId(loginRespVo.getId()));
        claims.put(Constant.JWT_ROLES_KEY,getRolesByUserId(loginRespVo.getId()));
        //创建token
        String access_token = JwtTokenUtil.getAccessToken(user.getId(), claims);
        String refresh_token = null;
        //这里对用户使用的平台进行判断
        if(vo.getType().equals("1")){
            refresh_token = JwtTokenUtil.getRefreshToken(user.getId(), claims);
        }else{
            refresh_token = JwtTokenUtil.getRefreshAppToken(user.getId(), claims);
        }

        loginRespVo.setAccessToken(access_token);
        loginRespVo.setRefreshToken(refresh_token);


        return loginRespVo;
    }

    /**
     * logout
     */
    @Override
    public void logout(String accessToken, String refreshToken) {
        if(StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(refreshToken)){
            throw new BusinessException(BaseResponseCode.DATA_ERROR);
        }

        Subject subject = SecurityUtils.getSubject();
        log.info("subject.getPrincipals()={}",subject.getPrincipals());
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        String userId = JwtTokenUtil.getUserId(accessToken);
        System.out.println("key: " + Constant.JWT_ACCESS_TOKEN_BLACKLIST + accessToken);
        redisService.set(Constant.JWT_ACCESS_TOKEN_BLACKLIST + accessToken,userId,JwtTokenUtil.getRemainingTime(accessToken), TimeUnit.MILLISECONDS);
        redisService.set(Constant.JWT_REFRESH_TOKEN_BLACKLIST + refreshToken,userId,JwtTokenUtil.getRemainingTime(refreshToken),TimeUnit.MILLISECONDS);
    }

    /**
     *
     * 这里的只是做测试所以使用了 mock数据
     * @param userid
     * @return
     */
    private List<String> getRolesByUserId(String userid){
        List<String> roles = new ArrayList<>();

        if("9a26f5f1-cbd2-473d-82db-1d6dcf4598f8".equals(userid)){
            roles.add("admin");
        }else{
            roles.add("test");
        }
        return roles;
    }

    private List<String> getPermissionByUserId(String userid){
        List<String> permissions = new ArrayList<>();

        if("9a26f5f1-cbd2-473d-82db-1d6dcf4598f8".equals(userid)){
            permissions.add("sys:user:add");
            permissions.add("sys:user:list");
            permissions.add("sys:user:update");
            permissions.add("sys:user:detail");
        }else{
            permissions.add("sys:user:detail");
        }
        return permissions;
    }

    @Override
    public PageVo<SysUser> pageInfo(UserPageReqVO vo) {
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        List<SysUser> list= sysUserMapper.selectAll(vo);
        for (SysUser sysUser:list){
            SysDept sysDept = sysDeptMapper.selectByPrimaryKey(sysUser.getDeptId());
            if(sysDept!=null){
               sysUser.setDeptName(sysDept.getName());
            }
        }
        return PageUtil.getPageVo(list);
    }

    @Override
    public void addUser(UserAddReqVo vo) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(vo, sysUser);
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setCreateTime(new Date());
        String salt = PasswordUtils.getSalt();
        String encodedPwd = PasswordUtils.encode(vo.getPassword(), salt);
        sysUser.setPassword(encodedPwd);
        int i = sysUserMapper.insertSelective(sysUser);
        if(i != 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
    }
}
