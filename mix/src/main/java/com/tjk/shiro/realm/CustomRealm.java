package com.tjk.shiro.realm;

import com.tjk.dao.UserDao;
import com.tjk.vo.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserDao userDao;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        // 从数据库或者缓存中获得角色数据
        Set<String> roles = getRolesByUserName(username);
        Set<String> permissions = getPermissionsByUserName(username);

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);

        return simpleAuthorizationInfo;
    }

    private Set<String> getPermissionsByUserName(String username) {
        System.out.println("从数据库中获取权限数据");
        List<String> list = userDao.getPermissionsByUserName(username);
        Set<String> sets = new HashSet<String>(list);
        return sets;
    }

    private Set<String> getRolesByUserName(String username) {
        System.out.println("从数据库中获取角色数据");
        List<String> list = userDao.getRolesByUserName(username);
        Set<String> sets = new HashSet<String>(list);
        return sets;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 1.从主体传过来的认证信息中，获得用户名
        String username = (String) token.getPrincipal();

        // 2.通过用户名到数据库中获取凭证
        String password = getPasswordByUsername(username);
        if (password == null) {
            return null;
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username,
            password, "customRealm");
        //加盐
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(username));
        return simpleAuthenticationInfo;
    }

    private String getPasswordByUsername(String username) {
        List<User> userList = userDao.getUserByUsername(username);
        if (!CollectionUtils.isEmpty(userList)) {
            return userList.get(0).getPassword();
        }
        return null;
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123456", "TJK");
        System.out.println(md5Hash);
    }
}