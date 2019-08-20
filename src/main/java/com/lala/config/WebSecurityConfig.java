package com.lala.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author lala
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /** HTTP权限控制 **/
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /** 开始请求权限配置 **/
        http.authorizeRequests()
                .antMatchers("/admin/login").permitAll()//管路员登录入口，所有用户都可以点进入

                .antMatchers("/css/**").permitAll()//静态资源
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/lib/**").permitAll()

                .antMatchers("/user/login").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/user/**").hasAnyRole("ADMIN", "USER") //用户访问的接口页面

                .and()
                .formLogin()

                .loginProcessingUrl("/login")//配置角色登录处理入口
                .and();

        http.headers().frameOptions().sameOrigin();
    }

    /** 自定义认证策略 **/
    @Autowired
    public void configGlobal(AuthenticationManagerBuilder auth) {
        try {
            auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN").and();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
