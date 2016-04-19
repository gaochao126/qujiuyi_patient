package com.jiuyi.qujiuyi.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author zhb
 * @date 2015年3月22日
 */
public class Util {
	private final static Random random = new Random();

	public static boolean isNotEmpty(String str) {
		if (str == null || "".equals(str)) {
			return false;
		} else {
			return true;
		}
	}

	public static String getUniqueSn() {
		return System.currentTimeMillis() + "" + random.nextInt(9) + random.nextInt(9);
	}

	public static String getOrderNumber() {
		return System.currentTimeMillis() + "" + random.nextInt(9) + random.nextInt(9) + random.nextInt(9);
	}

	public static String getVerificationCode() {
		return random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + "";
	}

	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
		if (str.length() > 9) {
			m = p1.matcher(str);
			b = m.matches();
		} else {
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}

	/**
	 * @description 获取邀请码
	 * @return
	 */
	public static String getInvitationCode() {
		String str = "" + System.currentTimeMillis();
		return str.substring(str.length() - 11);
	}

	/**
	 * @description 获取一周开始时间
	 * @return
	 * @throws ParseException
	 */
	public static Date getStartTimeOfWeek() throws ParseException {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);
		long time = System.currentTimeMillis();
		if (day > 1) {
			time = time - (day - 2) * 24 * 60 * 60 * 1000L;
		} else {
			time = time - 6 * 24 * 60 * 60 * 1000L;
		}
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date = sdf.parse(sdf.format(date));
		return date;
	}

	/**
	 * @description 获取一周结束时间
	 * @return
	 * @throws ParseException
	 */
	public static Date getEndTimeOfWeek() throws ParseException {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);
		long time = System.currentTimeMillis();
		if (day > 1) {
			time = time + (8 - day) * 24 * 60 * 60 * 1000L;
		}
		Date date = new Date(time);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		date = sdf2.parse(sdf1.format(date) + " 23:59:59");
		return date;
	}

	/**
	 * @description 返回当前时间是周几
	 * @return
	 */
	public static int getCurrentDayOfWeek() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);
		return day == 1 ? 7 : day - 1;
	}

	/**
	 * @description 根据日期获取星期
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getDayOfWeek(Date date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = sdf.parse(sdf.format(new Date()));
		Date targetDate = sdf.parse(sdf.format(date));
		if (currentDate.getTime() == targetDate.getTime()) {
			return getCurrentDayOfWeek();
		} else if (currentDate.getTime() < targetDate.getTime()) {
			return (int) ((targetDate.getTime() - currentDate.getTime()) / (24 * 60 * 60 * 1000) + getCurrentDayOfWeek()) % 7;
		} else if (currentDate.getTime() > targetDate.getTime()) {
			return (int) (getCurrentDayOfWeek() + 7 - (currentDate.getTime() - targetDate.getTime()) / (24 * 60 * 60 * 1000) % 7) % 7;
		}
		return 0;
	}

	/**
	 * @description 根据日期获取一周的日期(从周一到周日)
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static List<Date> getDateListOfWeek(Date date) throws ParseException {
		List<Date> list = new ArrayList<Date>();
		int dayOfWeek = getDayOfWeek(date);
		Date firstDay = new Date(date.getTime() - (dayOfWeek - 1) * 24 * 60 * 60 * 1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		firstDay = sdf.parse(sdf.format(firstDay));
		list.add(firstDay);
		for (int i = 1; i < 7; i++) {
			if (i == 1) {
				list.add(new Date(firstDay.getTime() + i * 24 * 60 * 60 * 1000L));
			} else {
				list.add(new Date(firstDay.getTime() + (i + 1) * 24 * 60 * 60 * 1000L));
			}
		}
		return list;
	}

	/**
	 * @description 根据生日得到年龄
	 */
	public static int getAge(Date birthDate) {

		if (birthDate == null)
			throw new RuntimeException("出生日期不能为null");

		int age = 0;

		Date now = new Date();

		SimpleDateFormat format_y = new SimpleDateFormat("yyyy");
		SimpleDateFormat format_M = new SimpleDateFormat("MM");

		String birth_year = format_y.format(birthDate);
		String this_year = format_y.format(now);

		String birth_month = format_M.format(birthDate);
		String this_month = format_M.format(now);

		// 初步，估算
		age = Integer.parseInt(this_year) - Integer.parseInt(birth_year);

		// 如果未到出生月份，则age - 1
		if (this_month.compareTo(birth_month) < 0) {
			age -= 1;
		}
		if (age < 0) {
			age = 0;
		}
		return age;
	}

	@SuppressWarnings("deprecation")
	public static Date getNextDate() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		date.setDate(date.getDate() + 1);

		String str = format.format(date);
		return format.parse(str);
	}

	public static void main(String[] args) {

		try {
			System.out.println(Util.getDayOfWeek(new Date()));
			System.out.println(Util.getDateListOfWeek(new Date()));
			System.out.println(new SimpleDateFormat("MM月dd日").format(new Date()));
			// <xml><appid>wx3b6e7cd6e9e98842</appid>
			// <mch_id>1241510502</mch_id>
			// <nonce_str>b50c83ef0d141c19ec8115c3104390a6</nonce_str>
			// <out_trade_no>145403131296427</out_trade_no>
			// <sign>2c862cce577acf911191164c3614b21c</sign></xml>
			String reqContent = "<xml><appid>wx3b6e7cd6e9e98842</appid><mch_id>1241510502</mch_id><nonce_str>62bc11e9759e2ca539077dd9d785d374</nonce_str><out_trade_no>145398386892365</out_trade_no><sign>6bf69e857c81735481fe411589351a4c</sign></xml>";
			HttpsUtil.post("https://api.mch.weixin.qq.com/pay/orderquery", reqContent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * @number
	 * @description 判断指定日期时间是上午还是下午 0：上午——1：下午
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 * @Date 2015年11月17日
	 */
	public static int timeZone(Date date) throws Exception {
		GregorianCalendar ca = new GregorianCalendar();
		ca.setTime(date);
		return ca.get(GregorianCalendar.AM_PM);
	}

	/**
	 * 
	 * @number
	 * @description 获得指定日期，并指定格式为（"yyyy-MM-dd"）
	 * 
	 * @param date
	 * @return
	 * @Date 2015年11月17日
	 */
	public static Date nowFormatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = format.format(date);
		Date d = null;

		try {
			d = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * 
	 * @number
	 * @description 获得指定日期，并指定格式为（"HH:mm:ss"）
	 * 
	 * @param date
	 * @return
	 * @Date 2015年11月17日
	 */
	public static Date nowFormatTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String dateStr = format.format(date);
		Date d = null;

		try {
			d = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * 
	 * @number
	 * @description 判断指定日期是上午，下午，晚上0-上午；1-下午；2-晚上；3-过期
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 * @Date 2015年11月17日
	 */
	public static int timeAmPmNight(Date date) throws Exception {
		String start = "00:00:00";
		String night = "22:00:00";
		String am = "12:00:00";
		String pm = "18:00:00";

		SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss");
		Date d_start = s.parse(start);
		Date d_night = s.parse(night);
		Date d_am = s.parse(am);
		Date d_pm = s.parse(pm);

		Date paramsTime = nowFormatTime(date);

		if (paramsTime.after(d_start) && paramsTime.before(d_am)) {
			return 0;
		}
		if (paramsTime.after(d_am) && paramsTime.before(d_pm)) {
			return 1;
		}
		if (paramsTime.after(d_pm) && paramsTime.before(d_night)) {
			return 2;
		}
		return 3;
	}

	/**
	 * 
	 * @number			@description 获得上午下午晚上
	 * 
	 * @param range
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月25日
	 */
	public static String getReange(int range) throws Exception {
		if (range == 0) {
			return "上午";
		}
		if (range == 1) {
			return "下午";
		}
		return "晚上";
	}

	/**
	 * 
	 * @number			@description	获得挂号成功后推送消息内容
	 * 
	 * @param name
	 * @param doctor
	 * @param date
	 * @param hospital
	 * @return
	 *
	 * @Date 2016年1月28日
	 */
	public static String getRegisterSms(String name, String doctor, String date, String hospital) {
		return name + "：您已成功预约" + doctor + "医生，请于" + date + "前往" + hospital + "，在手机上取号并就诊。不能前往请提前1天取消。";
	}
	
	/**
	 * 停诊消息组合
	 * @param patientName
	 * @param doctorName
	 * @param visitNo
	 * @return
	 */
	public static String stopRegister(String patientName,String doctorName,String visitNo){
		return "【791去就医】尊敬的"+patientName+",由于"+doctorName+"医生停诊，您的预约挂号（就诊号"+visitNo+"）已自动取消，费用将悉数退回。给您带来的不便，敬请谅解。";
	}
}