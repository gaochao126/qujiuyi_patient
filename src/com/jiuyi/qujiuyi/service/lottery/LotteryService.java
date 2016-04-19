package com.jiuyi.qujiuyi.service.lottery;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;

/**
 * @author superb @Date 2015年12月28日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
public interface LotteryService {

	/**
	 * 
	 * @number 1 @description 获取抽奖权限
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public ResponseDto getLotteryLimits(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number	2		@description 查询最新活动中奖结果
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月31日
	 */
	public ResponseDto queryNewLotteryResult(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number	3		@description	查询我参与的活动
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月31日
	 */
	public ResponseDto queryJoinLottery(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number	4		@description 根据活动号查询中奖纪录
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月31日
	 */
	public ResponseDto queryLotteryResultByLotteryNo(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number	5		@description	退出抽奖
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月12日
	 */
	public ResponseDto exitLottery(PatientDto patientDto) throws Exception;

}
