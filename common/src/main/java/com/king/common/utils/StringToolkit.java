package com.king.common.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class StringToolkit {

	private static Logger log = LoggerFactory.getLogger(StringToolkit.class);
	public static final String EMPTY = "";
	private static String SEPORATOR_START = "{";
	private static String SEPORATOR_END = "}";

	public static String[] findNestedStrings(String source, String tag,
			boolean preserveRepeated) {
		return findNestedStrings(source, tag, tag, preserveRepeated);
	}

	/**
	 * 找出字符串中的绑定参数， 'test:{param1},test2:{param2}',绑定参数为param1和param2,
	 * 开始分隔符为'{',结束分隔符为'}'
	 * 
	 * @author CaiWanLi
	 * @param source
	 *            原始字符串
	 * @param open
	 *            开始分隔符
	 * @param close
	 *            结束分隔符
	 * @param preserveRepeated
	 *            是否保留相同的绑定参
	 * @return
	 */
	public static String[] findNestedStrings(String source, String open,
			String close, boolean preserveRepeated) {
		StringUtils.substringBetween(source, open, close);
		List<String> strList = new ArrayList<String>();
		if (source == null || open == null || close == null) {
			return null;
		}
		int start = 0;
		int end = 0;

		start = source.indexOf(open);
		while (start != -1 && end < source.length()) {
			end = source.indexOf(close, start + open.length());
			if (end != -1) {
				String nestedStr = source.substring(start + open.length(), end);
				if (!preserveRepeated && !strList.contains(nestedStr)) {
					strList.add(nestedStr);
				}
				start = source.indexOf(open, end + 1);
			}
		}
		String[] strs = new String[strList.size()];
		System.arraycopy(strList.toArray(), 0, strs, 0, strList.size());
		return strs;
	}

	/**
	 * 按给定的对象格式化字符串
	 * 
	 * @author CaiWanLi
	 * @param valueHolder
	 * @param str
	 * @param preserveRepeated
	 * @return 格式化后的字符串
	 */
	public static String format(Object valueHolder, String str,
			boolean preserveRepeated, String open,String close) {
		if (valueHolder != null && StringUtils.isNotEmpty(str)) {
			try {
				String[] nestedStrs = findNestedStrings(str, open,
						close, preserveRepeated);
				for (int i = 0; i < nestedStrs.length; i++) {
					String value = null;
					String replace = open + nestedStrs[i]
							+ close;
					try {
						if (valueHolder instanceof List) {
							List valueList = (List) valueHolder;
							value = valueList.get(i).toString();
						}else if (valueHolder instanceof JSONObject){
							value = ((JSONObject)valueHolder).getString(nestedStrs[i]);
						} else {
							value = BeanUtils.getProperty(valueHolder,
									nestedStrs[i]);

						}
					} catch (Exception e) {
						// doNothing
					}
					if (value == null) {
						value = "";
					}
					str = StringUtils.replace(str, replace, value);
				}
			} catch (Exception e) {
				log.error("调用StringToolki.format方法出错!", e);

			}
		}
		return str;
	}
	
	/**
	 * 按给定的对象格式化字符串
	 * 
	 * @author CaiWanLi
	 * @param valueHolder
	 * @param str
	 * @param preserveRepeated
	 * @return 格式化后的字符串
	 */
	public static String format(Object valueHolder, String str,
			boolean preserveRepeated) {
		return format(valueHolder, str, preserveRepeated, SEPORATOR_START, SEPORATOR_END);
	}
	

	/**
	 * Initialization lock for the whole class. Init's only happen once per
	 * class load so this shouldn't be a bottleneck.
	 */
	private static Object initLock = new Object();

	/**
	 * Array of numbers and letters of mixed case. Numbers appear in the list
	 * twice so that there is a more equal chance that a number will be picked.
	 * We can use the array to get a random number or letter by picking a random
	 * array index.
	 */
	private static char[] numbersAndLetters = null;

	/**
	 * Pseudo-random number generator object for use with randomString(). The
	 * Random class is not considered to be cryptographically secure, so only
	 * use these random Strings for low to medium security applications.
	 */
	private static Random randGen = null;

	/**
	 * Matching two strings blurring. There is four forms of <code>dec</code>.
	 * <P>
	 * For example: <blockquote>
	 * 
	 * <pre>
	 * <code>
	 * String dec = &quot;%dec%&quot;;
	 * </code>
	 * <code>
	 * String dec = &quot;dec%&quot;;
	 * </code>
	 * <code>
	 * String dec = &quot;%dec&quot;;
	 * </code>
	 * <code>
	 * String dec = &quot;dec&quot;;
	 * </code>
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param src
	 *            string to be matched
	 * @param dec
	 *            string you want to match
	 * @return boolean the match result
	 */
	public static boolean blurEquals(String src, String dec) {
		boolean isMatch = false;
		if (dec.startsWith("%") && dec.endsWith("%")) {
			if (src.indexOf(dec.substring(1, dec.length() - 1)) != -1) {
				isMatch = true;
			}
		} else if (dec.startsWith("%")) {
			if (src.endsWith(dec.substring(1))) {
				isMatch = true;
			}
		} else if (dec.endsWith("%")) {
			if (src.startsWith(dec.substring(0, dec.length() - 1))) {
				isMatch = true;
			}
		} else {
			if (src.equals(dec)) {
				isMatch = true;
			}
		}
		return isMatch;
	}

	/**
	 * Intelligently chops a String at a word boundary (whitespace) that occurs
	 * at the specified index in the argument or before. However, if there is a
	 * newline character before <code>length</code>, the String will be chopped
	 * there. If no newline or whitespace is found in <code>string</code> up to
	 * the index <code>length</code>, the String will chopped at
	 * <code>length</code>.
	 * <p>
	 * For example, chopAtWord("This is a nice String", 10) will return "This is
	 * a" which is the first word boundary less than or equal to 10 characters
	 * into the original String.
	 * 
	 * @param string
	 *            the String to chop.
	 * @param length
	 *            the index in <code>string</code> to start looking for a
	 *            whitespace boundary at.
	 * @return a substring of <code>string</code> whose length is less than or
	 *         equal to <code>length</code>, and that is chopped at whitespace.
	 */
	public static final String chopAtWord(String string, int length) {
		if (string == null) {
			return string;
		}

		char[] charArray = string.toCharArray();
		int sLength = string.length();
		if (length < sLength) {
			sLength = length;
		}

		// First check if there is a newline character before length; if so,
		// chop word there.
		for (int i = 0; i < sLength - 1; i++) {
			// Windows
			if (charArray[i] == '\r' && charArray[i + 1] == '\n') {
				return string.substring(0, i);
			}
			// Unix
			else if (charArray[i] == '\n') {
				return string.substring(0, i);
			}
		}
		// Also check boundary case of Unix newline
		if (charArray[sLength - 1] == '\n') {
			return string.substring(0, sLength - 1);
		}

		// Done checking for newline, now see if the total string is less than
		// the specified chop point.
		if (string.length() < length) {
			return string;
		}

		// No newline, so chop at the first whitespace.
		for (int i = length - 1; i > 0; i--) {
			if (charArray[i] == ' ') {
				return string.substring(0, i).trim();
			}
		}

		// Did not find word boundary so return original String chopped at
		// specified length.
		return string.substring(0, length);
	}

	/**
	 * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
	 * &lt;table&gt;, etc) and converts the '&lt'' and '&gt;' characters to
	 * their HTML escape sequences.
	 * 
	 * @param input
	 *            the text to be converted.
	 * @return the input string with the characters '&lt;' and '&gt;' replaced
	 *         with their HTML escape sequences.
	 */
	public static final String escapeHTMLTags(String input) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (input == null || input.length() == 0) {
			return input;
		}
		// Use a StringBuffer in lieu of String concatenation -- it is
		// much more efficient this way.
		StringBuffer buf = new StringBuffer(input.length());
		char ch = ' ';
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			// Change if else-if else to switch
			// to reduce CC(Cyclomatic Complexity) in Java Code Conventions
			switch (ch) {
			case '<':
				buf.append("&lt;");
				break;
			case '>':
				buf.append("&gt;");
				break;
			case '&':
				buf.append("&amp;");
				break;
			case '"':
				buf.append("&quot;");
				break;
			default:
				buf.append(ch);
				break;
			}
		}
		return buf.toString();
	}

	/**
	 * Change the coding mode GBK of <code>str</code> to ISO-8859-1
	 * <P>
	 * This method return empty string when coming string is null or length is 0
	 * 
	 * @param str
	 *            the coming string which want to change
	 * @return return string with coding ISO-8859-1
	 */
	public static String GBKtoISO(String str) {
		if (str == null || str.trim().length() == 0) {
			return "";
		}
		try {
			str = new String(str.getBytes("gbk"), "ISO-8859-1");
		} catch (Exception e) {
			log.error("StringToolkit.java:GBKtoISO():" + e);
		}
		return str;
	}

	/**
	 * Change the coding mode gb2312 of <code>str</code> to ISO-8859-1
	 * <P>
	 * This method return empty string when coming string is null or length is 0
	 * 
	 * @param str
	 *            the coming string which want to change
	 * @return return string with coding ISO-8859-1
	 */
	public static String GBtoISO(String str) {
		if (str == null || str.trim().length() == 0) {
			return "";
		}
		try {
			str = new String(str.getBytes("gb2312"), "ISO-8859-1");
		} catch (Exception e) {
			log.error("StringToolkit.java:GBtoISO():" + e);
		}

		return str;
	}

	/**
	 * Chop the coming string by <code>substring()</code> with param
	 * <code>length</code> If <code>content</code> 's length longer than
	 * <code>length</code> return string will end with '...'. Otherwise return
	 * <code>content</code>
	 * <P>
	 * This method return "" when <code>content</code> is null
	 * 
	 * @param content
	 *            coming string to be chopped
	 * @param length
	 *            the length of coming string want to leave
	 * @return String string after chopped,end with ...
	 */
	public static String getLimitLengthString(String content, int length) {
		if (content == null)
			return "";
		if (content.length() > length) {
			content = content.substring(0, length) + "...";
		}
		return content;
	}

	/**
	 * Change the coding mode ISO-8859-1 of <code>str</code> to gb2312
	 * <P>
	 * This method return "" when coming string is null or length is 0
	 * 
	 * @param str
	 *            the coming string which want to change
	 * @return return string with coding gb2312
	 */
	public static String ISOtoGB(String str) {
		if (str == null || str.trim().length() == 0) {
			return "";
		}
		try {
			str = new String(str.getBytes("ISO-8859-1"), "gb2312");
		} catch (Exception e) {
			log.error("StringToolkit.java:ISOtoGB():" + e);
		}
		return str;
	}

	/**
	 * Change the coding mode ISO-8859-1 of <code>str</code> to gbk
	 * <P>
	 * This method return "" when coming string is null or length is 0
	 * 
	 * @param str
	 *            the coming string which want to change
	 * @return return string with coding gbk
	 */
	public static String ISOtoGBK(String str) {
		if (str == null || str.trim().length() == 0) {
			return "";
		}
		try {
			str = new String(str.getBytes("ISO-8859-1"), "GBK");
		} catch (Exception e) {
			log.error("StringToolkit.java:ISOtoGB():" + e);
		}
		return str;
	}

	/**
	 * Return true if the string is neither null nor empty string
	 * <p>
	 * 
	 * @param str
	 *            The string for checking
	 * @return true if the string is neither null nor empty string
	 */
	public static boolean isValuedString(String str) {
		return ((str != null) && (!str.equals("")));
	}

	public static boolean isEmpty(Object str) {
		return (str == null || "".equals(str));
	}

	public static boolean isNotEmpty(Object str) {
		return !isEmpty(str);
	}

	/**
	 * Pad <code>body</code> in the left side with <code>c</code> to make sure
	 * returning String's length's <code>len</code>
	 * <P>
	 * If <code>body</code> is <code>null</code> returns String which length's
	 * zero.
	 * <P>
	 * If <code>len</code> larger than <code>body</code>'s length throws
	 * <code>IllegalArgumentException</code> exception
	 * 
	 * @param body
	 *            Coming String want to perform padding
	 * @param len
	 *            Length of return String
	 * @param c
	 *            Padding char
	 * @return String which length is <code>len</code> paded with <code>c</code>
	 * @exception IllegalArgumentException
	 *                If <code>len</code> is larger than <code>body</code>'s
	 *                length
	 */
	public static String lpad(String body, int len, char c) {
		if (body == null)
			return "";
		if (len < body.length())
			throw new IllegalArgumentException("Wrong argument!");

		StringBuffer temp = new StringBuffer();
		int l = body.length();
		for (int i = 0; i < len - l; i++) {
			temp.append(c);
		}
		temp.append(body);
		return temp.toString();
	}

	/**
	 * Returns a random String of numbers and letters of the specified length.
	 * The method uses the Random class that is built-in to Java which is
	 * suitable for low to medium grade security uses. This means that the
	 * output is only pseudo random, i.e., each number is mathematically
	 * generated so is not truly random.
	 * <p>
	 * 
	 * For every character in the returned String, there is an equal chance that
	 * it will be a letter or number. If a letter, there is an equal chance that
	 * it will be lower or upper case.
	 * <p>
	 * 
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 * 
	 * @param length
	 *            the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static final String randomString(int length) {
		if (length < 1) {
			return null;
		}
		// Init of pseudo random number generator.
		if (randGen == null) {
			synchronized (initLock) {
				if (randGen == null) {
					randGen = new Random();
					// Also initialize the numbersAndLetters array
					numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
							+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
							.toCharArray();
				}
			}
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * Replaces all instances of oldString with newString in line.
	 * <P>
	 * Recommend to use
	 * <code>java.lang.String#replace(CharSequence, CharSequence)</code> which
	 * since java 1.5
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 * @deprecated Recommend to use
	 *             <code>java.lang.String#replace(CharSequence, CharSequence)</code>
	 * @see java.lang.String#replace(CharSequence, CharSequence)
	 */
	public static final String replace(String line, String oldString,
			String newString) {
		if (line == null) {
			return null;
		}
		if (oldString == null || newString == null) {
			return line;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return line;
	}

	/**
	 * Replaces all instances of oldString with newString in line.
	 * <P>
	 * The count Integer is updated with number of replaces.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * @param count
	 *            number of replaces
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString,
			String newString, int[] count) {
		if (line == null) {
			return null;
		}
		if (oldString == null || newString == null) {
			return line;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			int counter = 0;
			counter++;
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				counter++;
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			count[0] = counter;
			return buf.toString();
		}
		return line;
	}

	/**
	 * Replaces all strings of <code>from</code> with <code>to</code> in src.
	 * <P>
	 * Recommend to use <code>java.lang.String#replaceAll(String, String)</code>
	 * which since java 1.4
	 * 
	 * @param src
	 *            the String to search to perform replacements on
	 * @param from
	 *            the String that should be replaced by to
	 * @param to
	 *            the String that will replace all strings of from
	 * @return String a String will all strings of from replaced by to
	 * @deprecated Recommend to use
	 *             <code>java.lang.String#replaceAll(String, String)</code>
	 * @see java.lang.String#replaceAll(String, String)
	 */
	public static String replaceAll(String src, String from, String to) {
		if (src == null || src.length() < 1)
			return src;
		if (from == null || to == null || from.equals(to))
			return src;

		int tmpIndex = -1;
		while ((tmpIndex = src.indexOf(from)) != -1) {
			src = src.substring(0, tmpIndex) + to
					+ src.substring(tmpIndex + from.length());
		}
		return src;
	}

	/**
	 * Replaces all instances of oldString with newString in line with the added
	 * feature that matches of newString in oldString ignore case.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replaceIgnoreCase(String line, String oldString,
			String newString) {
		if (line == null) {
			return null;
		}
		if (oldString == null || newString == null) {
			return line;
		}
		String lcLine = line.toLowerCase();
		String lcOldString = oldString.toLowerCase();
		int i = 0;
		if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return line;
	}

	/**
	 * Pad <code>body</code> in the right side with <code>c</code> to make sure
	 * returning String's length's <code>len</code>
	 * <P>
	 * If <code>body</code> is <code>null</code> returns String which length's
	 * zero.
	 * <P>
	 * If <code>len</code> larger than <code>body</code>'s length throws
	 * <code>IllegalArgumentException</code> exception
	 * 
	 * @param body
	 *            Coming String want to perform padding
	 * @param len
	 *            Length of return String
	 * @param c
	 *            Padding char
	 * @return String which length is <code>len</code> paded with <code>c</code>
	 * @exception IllegalArgumentException
	 *                If <code>len</code> is larger than <code>body</code>'s
	 *                length
	 */
	public static String rpad(String body, int len, char c) {
		if (body == null)
			return "";
		if (len < body.length())
			throw new IllegalArgumentException("Wrong argument!");

		StringBuffer temp = new StringBuffer();
		temp.append(body);
		for (int i = 0; i < len - body.length(); i++) {
			temp.append(c);
		}
		return temp.toString();
	}

	/**
	 * Accourding to <code>regex</code> to split <code>strSrc</code>
	 * <P>
	 * For example, The <code>strSrc</code> "boo:and:foo",
	 * <P>
	 * Regex Result : { "boo", "and", "foo" } o { "b", "", ":and:f" }
	 * <P>
	 * Recommend to use <code>java.lang.String#split(String)</code>
	 * 
	 * @param strSrc
	 *            String
	 * @param regex
	 *            String
	 * @return String[] The array of strings computed by splitting this string
	 *         around matches of the given regular expression
	 * @deprecated Recommend to use <code>java.lang.String#split(String)</code>
	 * @see java.lang.String#split(String)
	 */
	public static String[] tokenizeByRegex(String strSrc, String regex) {
		String[] strRst = null;
		if (isValuedString(strSrc) && isValuedString(regex)) {
			strRst = strSrc.split(regex);
		}
		return strRst;
	}

	/**
	 * Converts a line of text into an array of lower case words. Words are
	 * delimited by the following characters: , .\r\n:/\+
	 * <p>
	 * In the future, this method should be changed to use a
	 * BreakIterator.wordInstance(). That class offers much more fexibility.
	 * <p>
	 * This method return <code>null</code> when <code>test</code> is null and
	 * <code>new String[]{""}</code> when length is 0
	 * 
	 * @param text
	 *            a String of text to convert into an array of words
	 * @return text broken up into an array of words.
	 */
	public static final String[] toLowerCaseWordArray(String text) {
		if (text == null) {
			return null;
		}
		if (text.length() == 0) {
			return new String[] { "" };
		}
		StringTokenizer tokens = new StringTokenizer(text, " ,\r\n.:/\\+");
		String[] words = new String[tokens.countTokens()];
		for (int i = 0; i < words.length; i++) {
			words[i] = tokens.nextToken().toLowerCase();
		}
		return words;
	}


	public static String capitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	/**
	 * Convert the first letter of coming String to be a capital
	 * 
	 * @param src
	 *            a String with the first char to be converted into capital
	 * @return String with the first letter be a capital
	 */
	public static String toUpperFisrtChar(String src) {
		if (src == null || src.length() < 1)
			return "";

		String first = src.substring(0, 1);
		return first.toUpperCase() + src.substring(1, src.length());
	}

	public static List<String> splitToVecString(String in, String delimiter) {
		List<String> q = new ArrayList<String>();

		if (in == null || in.length() == 0) {
			return q;
		}

		int pos = 0;
		pos = in.indexOf(delimiter);
		String val = null;
		while (pos >= 0) {
			if (pos == 0) {
				val = "";
			}
			if (pos > 0) {
				val = in.substring(0, pos);
			}
			q.add(val);
			in = in.substring(pos + delimiter.length(), in.length());
			pos = in.indexOf(delimiter);
		}
		if (in.length() > 0) {
			q.add(in);
		}
		return q;
	}

	/**
	 * private constructor for this class prevent <code>StringToolkit</code> to
	 * be instanced
	 * 
	 */
	private StringToolkit() {

	}

	public static String isNullString(String arg) {
		if (arg == null || arg.equals("")) {
			return null;
		}

		return arg;
	}

	/**
	 * @description 暂时专用于生成mybatis的表
	 * @param source
	 * @return
	 */
	public static String tableName2VOName(String source) {

		StringBuffer sb = new StringBuffer();
		int length = source.length();
		String lastStr = "";
		for (int i = 0; i < length; i++) {
			String str = source.substring(i, i + 1);
			if (i == 0) {
				sb.append(str.toUpperCase());
			} else if ("_".equals(str)) {
			} else {
				if ("_".equals(lastStr)) {
					sb.append(str.toUpperCase());
				} else {
					sb.append(str);
				}
			}
			lastStr = str;
		}

		return sb.append("VO").toString();
	}

	public static String tableName2ObjectName(String source) {
		StringBuffer sb = new StringBuffer();
		int length = source.length();
		String lastStr = "";
		for (int i = 0; i < length; i++) {
			String str = source.substring(i, i + 1);
			if (i == 0) {
				sb.append(str.toUpperCase());
			} else if ("_".equals(str)) {
			} else {
				if ("_".equals(lastStr)) {
					sb.append(str.toUpperCase());
				} else {
					sb.append(str);
				}
			}
			lastStr = str;
		}

		return sb.toString();
	}


	public static byte[] getBytesFromInputStream(InputStream inStream)
			throws Exception {

		long length = inStream.available();
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = inStream.read(bytes, offset, bytes.length
						- offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new Exception("Could not completely read inputstream ");
		}

		return bytes;
	}

	public static String getStringFromInputstream(InputStream inStream)
			throws Exception {

		return new String(getBytesFromInputStream(inStream));
	}

	public static String getObjectString(Object str) {
		return null != str ? str.toString() : null;
	}
	
	public static String getObjectStringWithEmpty(Object str) {
		return null != str ? str.toString() : "";
	}

	public static String getObjectString(Object str, String defaultString) {
		return null != str ? str.toString() : defaultString;
	}
	
	
	public static String wrapString(Object str,String wrap){
		if (null == str){
			return null;
		}
		
		if (null == wrap){
			return getObjectString(str);
		}
		
		return wrap + getObjectString(str) + wrap;
	}
	
	
	public static String join(String src, String srcSeperator,String destSeperator,String wrap) {
		if (StringToolkit.isEmpty(src)){
			return src;
		}
		if (StringToolkit.isNotEmpty(wrap)){
			return wrapString(src.replaceAll(srcSeperator, wrapString(destSeperator,wrap)),wrap);
		}
		
		return src.replaceAll(srcSeperator, destSeperator);
		
	}

	
	
	public static String join(Collection<?> collection, String seperator,
			String wrap) {
		return join(collection.iterator(), seperator, wrap);
	}

	
	public static String join(Iterator<?> iterator, String separator,
			String wrap) {
		if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return StringToolkit.wrapString(first, wrap);
        }

        // two or more elements
        StrBuilder buf = new StrBuilder(256); // Java default is 16, probably too small
        if (first != null) {
        	if (isNotEmpty(wrap)){
        		buf.append(wrap).append(first).append(wrap);
        	}else {
        		buf.append(first);
        	}
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
            	if (isNotEmpty(wrap)){
            		buf.append(wrap).append(obj).append(wrap);
            	}else {
            		buf.append(obj);
            	}
            }
        }
        return buf.toString();
	}
	
	 public static String escapeForXML(String string) {
	        //Check if the string is null or zero length -- if so, return
	        //what was sent in.
	        if (string == null || string.length() == 0 ) {
	            return string;
	        }
	        char [] sArray = string.toCharArray();
	        StringBuffer buf = new StringBuffer(sArray.length);
	        char ch;
	        for (int i=0; i<sArray.length; i++) {
	            ch = sArray[i];
	            if(ch == '<') {
	                buf.append("&lt;");
	            }
	            else if (ch == '&') {
	                buf.append("&amp;");
	            }
	            else if (ch == '"') {
	                buf.append("&quot;");
	            }
	            else {
	                buf.append(ch);
	            }
	        }
	        return buf.toString();
	    }
	 
	 
	 public static String arrayToIn(Object[] a) {
		 if (a == null)
			 throw new IllegalArgumentException("array is null");
		 
		 int iMax = a.length - 1;
		 if (iMax == -1)
			 throw new IllegalArgumentException("array is empty");
		 
		 StringBuilder b = new StringBuilder();
		 b.append('(');
		 for (int i = 0; ; i++) {
			 b.append("'" + String.valueOf(a[i]) + "'");
			 if (i == iMax)
				 return b.append(')').toString();
			 b.append(", ");
	     }
	 }
}
