package com.king.common.utils.pattern;

import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月27日
 */
public class ParserUtil {
    private static final Logger log = LoggerFactory.getLogger(ParserUtil.class);

    private static char[] models = new char[] { '.', '[', ']', '(', ')' };
    
    private static  String[] basetypes = new String[]{"char","short","int","long","float","double","boolean"};
    
    private static  String[] boxtypes = new String[]{"java.lang.Character","java.lang.Short","java.lang.Integer","java.lang.Long","java.lang.Float","java.lang.Double","java.lang.Boolean"};

    public static final String IDX_MODEL = "[]";

    public static final String MAP_MODEL = "()";

    public static final String NESTED_MODEL = ".";

    /**
     * @descripe 解析接口管理器传入参数定义中的属性名
     * @param name  
     * @param nameList
     * @param idxList
     * @param keyList
     * @throws Exception
     */
    public static void analyzeName(String name, List nameList, List idxList,
            List keyList) throws Exception {
        if (!StringToolkit.isEmpty(name)) {
            char[] nameArr = name.toCharArray();
            if (nameArr[0] == '.' || nameArr[0] == ']' || nameArr[0] == ')'
                    || Character.isDigit(nameArr[0])) {
                if (log.isInfoEnabled()) {
                    log.info("属性名'" + name + "'以'.'、']'、')'或数字开头,不合法!");
                }
                throw new Exception("属性名'" + name + "'以'.'、']'、')'或数字开头,不合法!");
            }

            Stack as = new Stack();
            Stack modelStack = new Stack();
            StringBuffer buf = new StringBuffer();
            for (int i = nameArr.length - 1; i >= 0; i--) {
                if (!isModel(nameArr[i])
                        && !Character.isLetterOrDigit(nameArr[i])) {
                    if (log.isInfoEnabled()) {
                        log
                                .info("属性名'" + name + "'含有非法字符'" + nameArr[i]
                                        + "'!");
                    }
                    throw new Exception("属性名'" + name + "'含有非法字符'" + nameArr[i]
                            + "'!");
                }
                as.push(new Character(nameArr[i]));
            }

            boolean idxFlag = false;
            boolean keyFlag = false;

            while (!as.empty()) {
                Character c = (Character) as.pop();
                Character nC = null;
                if (!as.empty()) {
                    nC = (Character) as.peek();
                    if (Character.isLetterOrDigit(c.charValue())) {
                        buf.append(c.charValue());
                        if (isModel(nC.charValue())) {
                            String var = buf.toString();
                            if (idxFlag) {
                                try {
                                    Integer.parseInt(var);
                                } catch (Exception e) {
                                    if (log.isInfoEnabled()) {
                                        log.info("属性名'" + name + "'定义" + var
                                                + "为数组或List的索引,不合法!");
                                    }
                                    throw new IllegalArgumentException(
                                            "Invalid indexed property '" + name
                                                    + "'");
                                }
                                idxList.add(new Integer(var));
                            } else if (keyFlag) {
                                if (var.indexOf(".") != -1) {
                                    if (log.isInfoEnabled()) {
                                        log.info("属性名'" + var + "'含有不合法字符'.'!");
                                    }
                                    throw new IllegalArgumentException(
                                            "Invalid mapped property '"
                                                    + var
                                                    + "',contain an invalid char '.'");
                                }
                                keyList.add(var);
                            } else {
                                nameList.add(var);
                            }
                            buf.setLength(0);
                        }
                    } else if (isModel(c.charValue())) {
                        if (c.charValue() == '.') {
                            if (!modelStack.empty()) {
                                if (log.isInfoEnabled()) {
                                    log.info("属性名'" + name + "'不匹配!");
                                }
                                throw new IllegalArgumentException(
                                        "Unmatched property '"
                                                + name
                                                + ",'"
                                                + ((Character) modelStack
                                                        .peek()).charValue()
                                                + "'与'" + c.charValue()
                                                + "'不匹配");
                            } else if (isMatched(c.charValue(), nC.charValue())) {
                                nameList.add(c.toString());
                            } else {
                                if (log.isInfoEnabled()) {
                                    log.info("属性名'" + name + "'不匹配!");
                                }
                                throw new IllegalArgumentException(
                                        "Unmatched property '" + name
                                                + ",error at '" + c + nC + "'");
                            }
                        } else {
                            switch (c.charValue()) {
                            case '[':
                                idxFlag = true;
                                break;
                            case ']':
                                if (Character.isLetterOrDigit(nC.charValue())) {
                                    if (log.isInfoEnabled()) {
                                        log.info("属性名'" + name
                                                + "'不匹配,']'后只能跟'.','[','('");
                                    }
                                    throw new IllegalArgumentException(
                                            "Unmatched property '" + name
                                                    + ",error at '" + c + nC
                                                    + "'");
                                }
                                idxFlag = false;
                                break;
                            case '(':
                                keyFlag = true;
                                break;
                            case ')':
                                if (Character.isLetterOrDigit(nC.charValue())) {
                                    if (log.isInfoEnabled()) {
                                        log.info("属性名'" + name
                                                + "'不匹配,')'后只能跟'.','[','('");
                                    }
                                    throw new IllegalArgumentException(
                                            "Unmatched property '" + name
                                                    + ",error at '" + c + nC
                                                    + "'");
                                }
                                keyFlag = false;
                                break;
                            }

                            if (!modelStack.empty()) {
                                Character topC = (Character) modelStack.pop();
                                if (!isMatched(topC.charValue(), c.charValue())) {
                                    if (log.isInfoEnabled()) {
                                        log.info("属性名'" + name + "'不匹配!");
                                    }
                                    throw new IllegalArgumentException(
                                            "Unmatched property '" + name
                                                    + ",error at '" + topC + c
                                                    + "'");
                                }
                                buf.append(topC.charValue()).append(
                                        c.charValue());
                                nameList.add(buf.toString());
                                buf.setLength(0);
                            } else {
                                if (c.charValue() == ']' || c.charValue() == ')') {
                                    if (log.isInfoEnabled()) {
                                        log.info("属性名'" + name + "'不匹配!");
                                    }
                                    throw new IllegalArgumentException(
                                            "Unmatched property '" + name
                                                    + "error at '" + c + "'");
                                }
                                modelStack.push(c);
                            }
                        }
                    }
                } else {// 最后一个字符
                    if (Character.isLetterOrDigit(c.charValue())) {
                        buf.append(c.charValue());
                        nameList.add(buf.toString());
                        buf.setLength(0);
                    } else {
                        switch (c.charValue()) {
                        case '.':
                        case '[':
                        case '(':
                            if (log.isInfoEnabled()) {
                                log.info("属性名'" + name + "'结尾字符'" + c
                                        + "'不能匹配!");
                            }
                            throw new Exception("属性名'" + name + "'结尾字符'" + c
                                    + "'不能匹配!");
                        case ']':
                        case ')':
                            if (!modelStack.empty()) {
                                char tC = ((Character) modelStack.pop())
                                        .charValue();
                                if (isMatched(tC, c.charValue())) {
                                    buf.append(tC).append(c.charValue());
                                    nameList.add(buf.toString());
                                    buf.setLength(0);
                                } else {
                                    if (log.isInfoEnabled()) {
                                        log.info("属性名'" + name + "'结尾字符'" + c
                                                + "'不能匹配!");
                                    }
                                    throw new Exception("属性名'" + name
                                            + "'结尾字符'" + c + "'不能匹配!");
                                }
                            } else {
                                if (log.isInfoEnabled()) {
                                    log.info("属性名'" + name + "'结尾字符'" + c
                                            + "'不能匹配!");
                                }
                                throw new Exception("属性名'" + name + "'结尾字符'"
                                        + c + "'不能匹配!");
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isModel(char c) {
        for (int i = 0; i < models.length; i++) {
            if (models[i] == c) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMatched(char c1, char c2) {
        if (c1 == '[' && c2 == ']') {
            return true;
        } else if (c1 == '(' && c2 == ')') {
            return true;
        } else if (c1 == '.' && Character.isLetter(c2)) {
            return true;
        } else if (Character.isLetterOrDigit(c1) && c2 == '.') {
            return true;
        }
        return false;
    }
    
    public static String parserBaseType(String className){
        for (int i =0; i < basetypes.length; i++){
            if (basetypes[i].equals(className)){
                return boxtypes[i];
            }
        }
        return className;
    }
}
