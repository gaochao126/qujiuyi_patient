package com.jiuyi.qujiuyi.dao.lottery;

import java.util.List;

import com.jiuyi.qujiuyi.dto.lottery.LotteryDetailDto;
import com.jiuyi.qujiuyi.dto.lottery.LotteryDto;
import com.jiuyi.qujiuyi.dto.lottery.LotteryTimeRecordDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;

/**
 * @author superb @Date 2015年12月28日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
public interface LotteryDao {
	/**
	 * 
	 * @number 1 @description 添加活动
	 * 
	 * @param lotteryDto
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public void addLottery(LotteryDto lotteryDto) throws Exception;

	/**
	 * 
	 * @number 2 @description 查询活动
	 * 
	 * @param lotteryDto
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public LotteryDto queryLottery(LotteryDto lotteryDto) throws Exception;

	/**
	 * 
	 * @number 3 @description 添加抽奖活动用户
	 * 
	 * @param patientDto
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public void addLotteryUser(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number 4 @description 查询抽奖用户
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public List<PatientDto> queryLotteryUser(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number 5 @description 查询参与用户数量
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public List<PatientDto> queryLotteryUserCount(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number 6 @description 更改部分用户不可抽奖状态
	 * 
	 * @param ids
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public void updateLotteryUserStatus(List<Integer> ids) throws Exception;

	/**
	 * 
	 * @number 7 @description 更改部分用户为可抽奖状态
	 * 
	 * @param ids
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public void resetLotteryUserStatus(List<Integer> ids) throws Exception;

	/**
	 * 
	 * @number 8 @description 添加中奖详情
	 * 
	 * @param lotteryDetailDto
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public void addLotteryDetail(LotteryDetailDto lotteryDetailDto) throws Exception;

	/**
	 * 
	 * @number 9 @description 查询参与用户
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public List<PatientDto> queryGetLotteryUser(List<Integer> ids) throws Exception;

	/**
	 * 
	 * @number 10 @description 删除抽奖记录
	 * 
	 * @param lotteryDetailDto
	 * @throws Exception
	 *
	 * @Date 2015年12月28日
	 */
	public void deleteLotteryDetail(LotteryDetailDto lotteryDetailDto) throws Exception;

	/**
	 * 
	 * @number	11		@description 查询最新的活动
	 * 
	 * @param lotteryDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月31日
	 */
	public LotteryDto queryNewLottery(LotteryDto lotteryDto) throws Exception;

	/**
	 * 
	 * @number	12		@description 查询中奖纪录
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月31日
	 */
	public List<PatientDto> queryResultHis(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number	13		@description	查询用户参与的活动
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月31日
	 */
	public List<PatientDto> queryMeJoinLottery(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number	14	@description	查询活动时间记录
	 * 
	 * @param lotteryTimeRecordDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月11日
	 */
	public LotteryTimeRecordDto queryLotteryTimeRecord(LotteryTimeRecordDto lotteryTimeRecordDto) throws Exception;

	/**
	 * 
	 * @number	 15		@description	退出抽奖	
	 * 
	 * @param patientDto
	 * @throws Exception
	 *
	 * @Date 2016年1月12日
	 */
	public void exitLottery(PatientDto patientDto) throws Exception;
}
