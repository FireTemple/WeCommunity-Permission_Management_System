package com.bohan.config;


import com.bohan.shiro.CredentialsMatcher;
import com.bohan.shiro.CustomAccessControlerFilter;
import com.bohan.shiro.CustomRealm;
import com.bohan.shiro.RedisCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public RedisCacheManager redisCacheManager(){
        return new RedisCacheManager();
    }

    @Bean
    public CredentialsMatcher credentialsMatcher(){
        return new CredentialsMatcher();
    }


    @Bean
    public CustomRealm customRealm(){
        CustomRealm customRealm = new CustomRealm();
        customRealm.setCredentialsMatcher(credentialsMatcher());
        customRealm.setCacheManager(redisCacheManager());
        return customRealm;
    }

    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        /**
         * 固定代码
         */
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /**
         * 这里添加过滤器来进行过滤 这里用了一个accessToken的验证过滤
         */
        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
        filtersMap.put("token", new CustomAccessControlerFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);
        /**
         * 配置不会被拦截的链接
         */
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/api/user/login", "anon");
        filterChainDefinitionMap.put("/index/**","anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/layui/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/treetable-lay/**", "anon");
        filterChainDefinitionMap.put("/api/user/token", "anon");
        //放开swagger-ui地址
        filterChainDefinitionMap.put("/swagger/**", "anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/captcha.jpg", "anon");
        filterChainDefinitionMap.put("/","anon");
        filterChainDefinitionMap.put("/csrf","anon");
        filterChainDefinitionMap.put("/**","token,authc");
        //配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        shiroFilterFactoryBean.setLoginUrl("/api/user/unLogin");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    /**
     * 开启aop注解支持 使用代理方法 所以需要开启代码支持
     * @param securityManager
     * @return
     */

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }
}
