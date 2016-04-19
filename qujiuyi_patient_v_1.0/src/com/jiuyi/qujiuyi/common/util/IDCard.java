package com.jiuyi.qujiuyi.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jiuyi.qujiuyi.service.BusinessException;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class IDCard {
	/*********************************** 身份证验证开始 ****************************************/
	/**
	 * 身份证号码验证 1、号码的结构 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码，
	 * 八位数字出生日期码，三位数字顺序码和一位数字校验码。 2、地址码(前六位数）
	 * 表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。 3、出生日期码（第七位至十四位）
	 * 表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。 4、顺序码（第十五位至十七位）
	 * 表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号， 顺序码的奇数分配给男性，偶数分配给女性。 5、校验码（第十八位数）
	 * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
	 * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4
	 * 2 （2）计算模 Y = mod(S, 11) （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0
	 * X 9 8 7 6 5 4 3 2
	 */

	private final static Pattern pattern;
	private final static String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
	private final static String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
	private final static Map<String, String> prinviceMap;
	static {
		pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		prinviceMap = new HashMap<String, String>();
		prinviceMap.put("11", "北京");
		prinviceMap.put("12", "天津");
		prinviceMap.put("13", "河北");
		prinviceMap.put("14", "山西");
		prinviceMap.put("15", "内蒙古");
		prinviceMap.put("21", "辽宁");
		prinviceMap.put("22", "吉林");
		prinviceMap.put("23", "黑龙江");
		prinviceMap.put("31", "上海");
		prinviceMap.put("32", "江苏");
		prinviceMap.put("33", "浙江");
		prinviceMap.put("34", "安徽");
		prinviceMap.put("35", "福建");
		prinviceMap.put("36", "江西");
		prinviceMap.put("37", "山东");
		prinviceMap.put("41", "河南");
		prinviceMap.put("42", "湖北");
		prinviceMap.put("43", "湖南");
		prinviceMap.put("44", "广东");
		prinviceMap.put("45", "广西");
		prinviceMap.put("46", "海南");
		prinviceMap.put("50", "重庆");
		prinviceMap.put("51", "四川");
		prinviceMap.put("52", "贵州");
		prinviceMap.put("53", "云南");
		prinviceMap.put("54", "西藏");
		prinviceMap.put("61", "陕西");
		prinviceMap.put("62", "甘肃");
		prinviceMap.put("63", "青海");
		prinviceMap.put("64", "宁夏");
		prinviceMap.put("65", "新疆");
		prinviceMap.put("71", "台湾");
		prinviceMap.put("81", "香港");
		prinviceMap.put("82", "澳门");
		prinviceMap.put("91", "国外");
	}

	/**
	 * 功能：身份证的有效验证
	 * 
	 * @param IDStr
	 *            身份证号
	 * @return 有效：返回"" 无效：返回String信息
	 * @throws ParseException
	 */
	public static void IDCardValidate(String IDStr) throws Exception {
		if (!Util.isNotEmpty(IDStr)) {
			throw new BusinessException("身份证号码为空");
		}
		String errorInfo = "";// 记录错误信息
		String Ai = "";
		// ================ 号码的长度 15位或18位 ================
		if (IDStr != null && IDStr.length() != 15 && IDStr.length() != 18) {
			errorInfo = "身份证号码长度应该为15位或18位。";
			throw new BusinessException(errorInfo);
		}
		// =======================(end)========================

		// ================ 数字 除最后以为都为数字 ================
		if (IDStr != null && IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr != null && IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
			throw new BusinessException(errorInfo);
		}
		// =======================(end)========================

		// ================ 出生年月是否有效 ================
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 月份
		if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
			errorInfo = "身份证生日无效。";
			throw new BusinessException(errorInfo);
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				errorInfo = "身份证生日不在有效范围。";
				throw new BusinessException(errorInfo);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			errorInfo = "身份证月份无效";
			throw new BusinessException(errorInfo);
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			errorInfo = "身份证日期无效";
			throw new BusinessException(errorInfo);
		}
		// =====================(end)=====================

		// ================ 地区码时候有效 ================
		if (prinviceMap.get(Ai.substring(0, 2)) == null) {
			errorInfo = "身份证地区编码错误。";
			throw new BusinessException(errorInfo);
		}
		// ==============================================

		// ================ 判断最后一位的值 ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (IDStr.length() == 18) {
			if (Ai.equals(IDStr) == false) {
				errorInfo = "身份证无效，不是合法的身份证号码";
				throw new BusinessException(errorInfo);
			}
		}
		// =======================(end)========================
	}

	/**
	 * 
	 * @number @description 根据身份证获取性别
	 * 
	 * @param card
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月10日
	 */
	public static Integer getGenderByCard(String card) throws Exception {
		IDCardValidate(card);

		if (card.length() == 15) {
			int num = Integer.parseInt(card.substring(14));
			return num % 2 == 0 ? 2 : 1;
		} else {
			int num = Integer.parseInt(card.substring(16, 17));
			return num % 2 == 0 ? 2 : 1;
		}
	}

	/**
	 * 
	 * @number @description 根据身份证获取生日
	 * 
	 * @param card
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月11日
	 */
	public static Date getBirthdayByCard(String card) throws Exception {
		IDCardValidate(card);
		String bir = "";
		if (card.length() == 15) {
			bir = "19" + card.substring(6, 8) + "-" + card.substring(8, 10) + "-" + card.substring(10, 12) + " 00:00:00";
		} else {
			bir = card.substring(6, 10) + "-" + card.substring(10, 12) + "-" + card.substring(12, 14) + " 00:00:00";
		}
		System.out.println(Util.StrToDate(bir));
		return Util.StrToDate(bir);
	}

	/**
	 * 功能：判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 功能：判断字符串是否为日期格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}
}