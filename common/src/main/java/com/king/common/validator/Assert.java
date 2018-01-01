package com.king.common.validator;

import org.apache.commons.lang.StringUtils;

import com.king.common.exception.RRException;

/**
 * 数据校验
 * @author King chen
 * @date 2017年12月25日
 */
public abstract class Assert {

    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new RRException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new RRException(message);
        }
    }
}
