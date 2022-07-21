package com.alan.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.alan.exception.MyException;
import com.alan.mapper.UserMapper;
import com.alan.pojo.LoginLog;
import com.alan.pojo.Perms;
import com.alan.pojo.User;
import com.alan.pojo.UserRole;
import com.alan.service.LoginLogService;
import com.alan.service.RolePermsService;
import com.alan.service.UserRoleService;
import com.alan.service.UserService;
import com.alan.utils.EnumCode;
import com.alan.utils.ResultUtil;
import com.xiaoleilu.hutool.crypto.SecureUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private RolePermsService rolePermissionService;

    @Override
    public Object login(User u, HttpSession session, HttpServletRequest request) {

        UsernamePasswordToken upToken = new UsernamePasswordToken(u.getUsername(), SecureUtil.md5(u.getPassword()));
        Subject subject = SecurityUtils.getSubject();
        subject.login(upToken);
        User user = (User) subject.getPrincipal();
        session.setAttribute("user", user);

        // 登录日志
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setLoginTime(new Date());
        loginLog.setLoginIp(request.getRemoteAddr());
        loginLog.setLoginTotal(loginLogService.findMaxLoginTatalByUserId(user.getId())); // 登录总次数
        loginLogService.insert(loginLog);

        // 根据用户类型查询  一级菜单
        List<Perms> parentList = rolePermissionService.findRolesPermisByFatherId(null, user.getRoleId());
        List<Perms> sonList = null;
        List<Perms> sonssonList = null;
        for (int i = 0, j = parentList.size(); i < j; i++) {

            // 二级 页面
            sonList = rolePermissionService.findRolesPermisByFatherId(parentList.get(i).getId(), user.getRoleId());
            for (int k = 0, l = sonList.size(); k < l; k++) {

                // 三级 按钮
                sonssonList = rolePermissionService.findRolesPermisByFatherId(sonList.get(k).getId(), user.getRoleId());
                sonList.get(k).setChildren(sonssonList);
            }
            parentList.get(i).setChildren(sonList);
        }
        user.setUserPerms(parentList);
        user.setLastLoginTime(new Date());
        super.updateById(user);
        return ResultUtil.result(EnumCode.OK.getValue(), "登陆成功", JSON.toJSON(user));
    }

    @Transactional
    @Override
    public Object addUser(User user) {

        Map<String,Object> map = new HashMap<>();
        map.put("username",user.getUsername().trim());
        List<User> list = super.baseMapper.selectByMap(map);
        if (list.size() > 0) {
            throw new MyException(ResultUtil.result(EnumCode.BAD_REQUEST.getValue(), "该用户名已经存在", null));
        }
        User u = new User();
        u.setUsername(user.getUsername());
        u.setPassword(SecureUtil.md5(user.getPassword()));
        u.setEmail(user.getEmail());
        u.setPhotoUrl(user.getPhotoUrl());
        u.setEnable(user.getEnable());
        u.setCreateTime(new Date());
        super.baseMapper.insert(u);
        //插入角色
        UserRole userRole = new UserRole();
        userRole.setRoleId(user.getRoleId());
        userRole.setUserId(u.getId());
        userRoleService.insert(userRole);
        return ResultUtil.result(EnumCode.OK.getValue(), "新增成功");
    }

    @Override
    public List<User> findUserByPage(Page<User> page, User user) {
        return super.baseMapper.findUserByPage(page,user);
    }

    @Override
    public Object delUsers(String[] ids) {
        for (String id : ids) {
            baseMapper.deleteById(id);
        }
        return ResultUtil.result(EnumCode.OK.getValue(), "删除成功");
    }

    @Override
    public List<User> checkUser(User user) {
        return super.baseMapper.checkUser(user);
    }

    @Override
    public Object editUserStatus(User user) {
        User u = new User();
        u.setId(user.getId());
        u.setEnable(user.getEnable());
        Integer row = super.baseMapper.updateById(u);
        return row > 0 ? ResultUtil.result(EnumCode.OK.getValue(), user.getEnable() == 0 ? "已禁止登陆" : "已允许登陆") : ResultUtil.result(EnumCode.INTERNAL_SERVER_ERROR.getValue(), "修改失败");
    }

    @Override
    public Object editUserInfo(User u) {
        User user = new User();
        user.setId(u.getId());
        user.setUsername(u.getUsername());
        user.setEmail(u.getEmail());
        user.setPhotoUrl(u.getPhotoUrl());
        Integer row = super.baseMapper.updateById(user);
        if (row > 0) {
            return ResultUtil.result(EnumCode.OK.getValue(), "修改成功，下次登录生效");
        }
        return ResultUtil.result(EnumCode.INTERNAL_SERVER_ERROR.getValue(), "修改失败");
    }

    @Override
    public List<User> findAllUser(User user) {
        return super.baseMapper.findAllUser(user);
    }

    /**
     * 根据用户名查询用户
     */
    @Override
    public User findUserByUsername(String username){
        return super.baseMapper.findUserByUsername(username);
    }
}
