package com.jiuyi.qujiuyi.dao.refund;

import com.jiuyi.qujiuyi.dto.refund.RefundDto;

/**
 * @author superb @Date 2015年12月15日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
public interface RefundDao {
	/**
	 * 
	 * @number 1 @description 添加退款记录
	 * 
	 * @param refundDto
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public void insertRefund(RefundDto refundDto) throws Exception;

	/**
	 * 
	 * @number 2 @description 修改退款
	 * 
	 * @param refundDto
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	public void updateRefund(RefundDto refundDto) throws Exception;
}
