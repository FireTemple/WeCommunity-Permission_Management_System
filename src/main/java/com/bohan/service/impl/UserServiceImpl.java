package com.bohan.service.impl;

import com.bohan.config.TokenSetting;
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
import com.bohan.vo.request.*;
import com.bohan.vo.respose.LoginRespVo;
import com.bohan.vo.respose.PageVo;
import com.bohan.vo.respose.UserOwnRoleRespVo;
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

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private TokenSetting tokenSetting;
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

    @Override
    public UserOwnRoleRespVo getUserOwnRole(String userId) {
        UserOwnRoleRespVo respVo = new UserOwnRoleRespVo();
        respVo.setOwnRoles(userRoleService.getRoleIdsByUserId(userId));
        respVo.setAllRole(roleService.selectAll());
        return respVo;
    }

    @Override
    public void setUserOwnRole(UserOwnRoleReqVo vo) {
        /**
         * 标记用户 要主动刷新
         */
        userRoleService.addUserRoleInfo(vo);
        redisService.set(Constant.JWT_REFRESH_KEY + vo.getUserId(), vo.getUserId(), tokenSetting.getAccessTokenExpireTime().toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public String refreshToken(String refreshToken) {
        //是否刷新
        //是否加入黑名单

        if(JwtTokenUtil.validateToken(refreshToken) || redisService.hasKey(Constant.JWT_REFRESH_TOKEN_BLACKLIST + refreshToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_ERROR);
        }

        String userid = JwtTokenUtil.getUserId(refreshToken);
        log.info("userid: {}", userid);
        Map<String, Object> claims = null;
        if(redisService.hasKey(Constant.JWT_REFRESH_KEY + userid)){
            claims = new HashMap<>();
            claims.put(Constant.JWT_PERMISSIONS_KEY,getPermissionByUserId(userid));
            claims.put(Constant.JWT_ROLES_KEY,getRolesByUserId(userid));
        }
        String newAccessToken = JwtTokenUtil.refreshToken(refreshToken, claims);

        if(redisService.hasKey(Constant.JWT_REFRESH_KEY + userid)){
            redisService.set(Constant.JWT_REFRESH_KEY + userid, newAccessToken, JwtTokenUtil.getRemainingTime(Constant.JWT_REFRESH_KEY + userid),TimeUnit.MILLISECONDS);
        }
        System.out.println("刷新完成！！");
        return newAccessToken;
    }

    @Override
    public void updateUserInfo(UserUpdateReqVo vo, String operatorId) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(vo, sysUser);
        sysUser.setUpdateTime(new Date());
        sysUser.setUpdateId(operatorId);
        if(StringUtils.isEmpty(vo.getPassword())){
            sysUser.setPassword(null);
        }else{
            String salt = PasswordUtils.getSalt();
            String encodePwd = PasswordUtils.encode(vo.getPassword(), salt);
            sysUser.setSalt(salt);
            sysUser.setPassword(encodePwd);
        }

        int i = sysUserMapper.updateByPrimaryKeySelective(sysUser);
        if(i != 1){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        if(vo.getStatus() == 2){
            redisService.set(Constant.ACCOUNT_LOCK_KEY + vo.getId(), vo.getId());

        }else{
            redisService.delete(Constant.ACCOUNT_LOCK_KEY + vo.getId());
        }
    }

    @Override
    public void deleteUsers(List<String> list, String operationId) {
        SysUser sysUser = new SysUser();
        sysUser.setUpdateId(operationId);
        sysUser.setUpdateTime(new Date());
        int i = sysUserMapper.deleteUsers(sysUser, list);
        if(i == 0){
            throw new BusinessException(BaseResponseCode.OPERATION_ERROR);
        }
        for (String userId: list){
            redisService.set(Constant.DELETED_USER_KEY + userId, userId,tokenSetting.getRefreshTokenExpireAppTime().toMillis(),TimeUnit.MILLISECONDS);
        }

    }
}
