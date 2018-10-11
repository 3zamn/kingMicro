package com.king.common.utils.pattern;

import java.util.ArrayList;
import java.util.List;

import org.apache.oro.text.PatternCacheLRU;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月20日
 */
public class PatternToolkit {
	private static Logger log = LoggerFactory.getLogger(PatternToolkit.class);
	// 使用PatternCacherFIO2缓存Pattern实例
	private static final int PERLUTIL_CAPACITY = 200;
	private static Perl5Util perlUtil = new Perl5Util(new PatternCacheLRU(
			PERLUTIL_CAPACITY));

	public static Perl5Util getPerlUtil() {
		return perlUtil;
	}

	/**
	 * 获取第{matchidx}次的匹配
	 * @param input
	 * @param regx
	 * @param matchidx
	 * @param groupidx
	 * @return
	 */
	public static String getMatch(String input, String regx, int matchidx,
			int groupidx) {
		try {
			Perl5Util perlUtil = getPerlUtil();
			PatternMatcherInput patternInput = new PatternMatcherInput(input);
			while (perlUtil.match(regx, patternInput)) {
				if (--matchidx < 1){
					MatchResult result = perlUtil.getMatch();
					return result.group(groupidx);
				}
			}
		} catch (Exception e) {
			log.error(e.getStackTrace().toString());
		}
		return "";
	}

	/**
	 * 首次匹配即返回
	 * @param input
	 * @param regx
	 * @param groupidx
	 * @return
	 */
	public static String getMatch(String input, String regx, int groupidx) {
		try {
			Perl5Util perlUtil = getPerlUtil();
			while (perlUtil.match(regx, input)) {
				MatchResult result = perlUtil.getMatch();
				return result.group(groupidx);
			}
		} catch (Exception e) {
			log.error(e.getStackTrace().toString());
		}
		return "";
	}

	/**
	 * 模糊匹配
	 * @param input
	 * @param regx
	 * @return
	 */
	public static boolean matches(String input, String regx) {
		try {
			Perl5Util perlUtil = getPerlUtil();
			return perlUtil.match(regx, input);
		} catch (Exception e) {
			log.error(e.getStackTrace().toString());
		}
		return false;
	}

	/**
	 * 完全匹配
	 * @param input
	 * @param regx
	 * @return
	 */
	public static boolean matcheWhole(String input, String regx) {
		try {
			Perl5Util perlUtil = getPerlUtil();
			/**
			 * 转化正则表达式，如果不是包含在^、$中，则加上，以精确匹配整个表达式 如：/a/ ==>/^a$/ /^a/ == > /^a$/
			 * /a$/ ==> /^a$/
			 */
			if (!regx.startsWith("/^")) {
				regx.replaceFirst("/", "/^");
			}
			if (!regx.endsWith("$/")) {
				regx = regx.substring(0, regx.length() - 1) + "$/";
			}
			return perlUtil.match(regx, input);
		} catch (Exception e) {
			log.error(e.getStackTrace().toString());
		}
		return false;
	}

	/**
	 *获取所有的匹配项
	 * @param input
	 * @param regx
	 * @param groupidx
	 * @return
	 */
	public static List<String> getAllMatches(String input, String regx, int groupidx) {
		List<String> matches = new ArrayList<String>();
		try {
			Perl5Util perlUtil = getPerlUtil();
			PatternMatcherInput patternInput = new PatternMatcherInput(input);
			while (perlUtil.match(regx, patternInput)) {
				MatchResult result = perlUtil.getMatch();
				if (result.groups() > groupidx) {
					matches.add(result.group(groupidx));
				} else {
					matches.add(result.group(0));
				}
			}
		} catch (Exception e) {
			log.error(e.getStackTrace().toString());
		}

		return matches;
	}
	
	
	public static void main(String[] args) {
		String url = "rest/wfs/task/personconfigdata?model={\"processcode\":\"ff061283-767e-43e3-a536-3c49b3f6823a\",\"dataclass\":\"role-\",\"stepcode\":\"78e943d0-84a4-4ffa-9716-cc0cb1aa8735\",\"roletype\":[\"flowrole\",\"commonrole\"]}";
		String regx = "/stepcode\"\\:\"(.*?)\".*/";
		String maString = PatternToolkit.getMatch(url, regx, 1);
		System.out.println(maString);
	}
}
