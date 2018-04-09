package com.king.services.spi;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.ShiroService;
import com.king.common.utils.Constant;
import com.king.dal.gen.model.smp.SysMenu;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;
import com.king.dao.SysMenuDao;
import com.king.dao.SysUserDao;
import com.king.dao.SysUserTokenDao;

import java.util.*;

@Service("shiroService")
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysUserTokenDao sysUserTokenDao;

    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN){
            List<SysMenu> menuList = sysMenuDao.queryList(new HashMap<>());
            permsList = new ArrayList<>(menuList.size());
            for(SysMenu menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = sysUserDao.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perms : permsList){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Transactional(readOnly = true)
    public SysUserToken queryByToken(String token) {
        return sysUserTokenDao.queryByToken(token);
    }

    @Transactional(readOnly = true)
    public SysUser queryUser(Long userId) {
        return sysUserDao.queryObject(userId);
    }
}
