/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.common.satoken.utils;

import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.utils.CodecUtil;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

public class AuthUtil {

    public static String getUserId() {
        return String.valueOf(LoginHelper.getUserId());
    }

    public static List<String> getUserRoles() {
        return StpUtil.getRoleList();
    }

    public static boolean isAdmin() {
        return LoginHelper.isSuperAdmin();
    }

    public static boolean isClientUser() {
        return AuthUtil.getUserRoles().contains(Constants.ROLE_CLIENT);
    }

    public static boolean hasWriteRole() {
        return AuthUtil.getUserRoles().contains(Constants.ROLE_WRITE);
    }

    public static String enCryptPwd(String pwd) throws Exception {
        return CodecUtil.aesEncrypt(CodecUtil.md5Str(pwd) + ":"
                + RandomUtils.nextInt(1000, 9999), Constants.ACCOUNT_SECRET);
    }

    public static boolean checkPwd(String pwd, String secret) throws Exception {
        String code = CodecUtil.aesDecrypt(secret, Constants.ACCOUNT_SECRET);
        String[] arr = code.split(":");
        return arr.length > 0 && CodecUtil.md5Str(pwd).equals(arr[0]);
    }
}
