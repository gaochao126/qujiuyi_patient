package com.jiuyi.qujiuyi.servlet;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.dto.consult.ConsultDto;
import com.jiuyi.qujiuyi.service.consult.ConsultService;
import com.jiuyi.qujiuyi.service.coupon.CouponService;
import com.jiuyi.qujiuyi.service.order.ThirdPayOrderService;

/**
 * @author zhb
 * @date 2015年3月26日
 */
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = -5305744701888018846L;
	private final static Logger logger = Logger.getLogger(InitServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		Constants.applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		CacheContainer.init();
		SysCfg.init();
		ConsultHandle();
		expireExpiredOrder();
		expiredCouponHandle();
		consultRefund();
		weixinOrderQuery();
	}

	/**
	 * @description 48小时后，医生接受的咨询患者没有评价结束的咨询处理为已结束，以及医生未接收的患者免费咨询处理
	 */
	private void ConsultHandle() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					ConsultDto consultDto = new ConsultDto();
					long time = System.currentTimeMillis() - 48 * 60 * 60 * 1000L;
					consultDto.setStartTime(new Date(time));
					Constants.applicationContext.getBean(ConsultService.class).updateConsultStatus(consultDto);
					Constants.applicationContext.getBean(ConsultService.class).updateFreeConsultNoRecive(consultDto);
				} catch (Exception e) {
					logger.error("图文咨询处理异常", e);
				}
			}
		}, 0, 5 * 60 * 1000);
	}

	/**
	 * @description 医生未接受患者付费咨询，1小时候自动退款
	 */
	private void consultRefund() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					Constants.applicationContext.getBean(ConsultService.class).handleConsultDoctorNoAccept();
				} catch (Exception e) {
					logger.error("图文咨询过期退款处理异常", e);
				}
			}
		}, 0, 5 * 60 * 1000);
	}

	/**
	 * @description 过期优惠券处理
	 */
	private void expiredCouponHandle() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {

					Constants.applicationContext.getBean(CouponService.class).CouponExpired();
				} catch (Exception e) {
					logger.error("过期优惠券处理异常", e);
				}
			}
		}, 0, 12 * 60 * 60 * 1000);
	}

	/**
	 * @description 过期挂号订单，15分钟有效,并且定时清理1小时后未付款订单（脏数据）
	 */
	private void expireExpiredOrder() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					logger.info("InitServlet.定时查询未过期，未付款订单");
					Constants.applicationContext.getBean(ThirdPayOrderService.class).handleThirdPayOrder();
					Constants.applicationContext.getBean(ThirdPayOrderService.class).deleteNoPayOrder();
				} catch (Exception e) {
					logger.error("待就诊挂号过期处理异常", e);
				}
			}
		}, 0, 5 * 60 * 1000);
	}

	/**
	 * @description 请求微信服务器，判断订单是否支付成功
	 */
	private void weixinOrderQuery() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					Constants.applicationContext.getBean(ThirdPayOrderService.class).reqWinxinOrderQuery();
				} catch (Exception e) {
					logger.error("请求微信服务器查看订单支付情况异常", e);
				}
			}
		}, 0, 60 * 1000);
	}
}