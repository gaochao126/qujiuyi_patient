package com.jiuyi.qujiuyi.service.lottery.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.lottery.LotteryDao;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.lottery.LotteryDto;
import com.jiuyi.qujiuyi.dto.lottery.LotteryTimeRecordDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.lottery.LotteryService;

/**
 * @author superb @Date 2015年12月28日
 * 
 * @Description
 *
 * @Copyright 2015 重庆柒玖壹健康管理有限公司
 */
@Service
public class LotteryServiceImpl implements LotteryService {
	@Autowired
	private LotteryDao lotteryDao;

	@Autowired
	private PatientDao patientDao;

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
	@Override
	public ResponseDto getLotteryLimits(PatientDto patientDto) throws Exception {
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		PatientDto patientDt = token != null ? token.getPatient() : new PatientDto();
		patientDto.setPatientId(patientDt.getId());
		patientDto.setPatientName(patientDt.getName());

		if (!Util.isNotEmpty(patientDto.getCodeWord())) {
			throw new BusinessException("请输入活动暗语");
		}

		// 查询患者是否已经注册
		PatientDto patientD = new PatientDto();
		patientD.setId(patientDto.getPatientId());
		PatientDto patient = patientDao.queryPatientById(patientD);
		if (patient == null) {
			throw new BusinessException("您未注册791用户");
		}
		if (patient.getPhone() == null) {
			throw new BusinessException("请绑定手机号");
		}

		// 查询活动
		LotteryDto lotteryDto = new LotteryDto();
		lotteryDto.setCodeWord(patientDto.getCodeWord());
		LotteryDto lot = lotteryDao.queryLottery(lotteryDto);// 根据暗语查询活动

		if (lot == null) {
			return new ResponseDto(4, "暗语不正确", null);
		}

		if (lot.getStatus() == 0) {
			return new ResponseDto(3, "活动已结束", lot);
		}

		// 如果注册查看是否已经报名抽奖
		patientDto.setLotteryNo(lot.getNo());
		List<PatientDto> pat = lotteryDao.queryLotteryUser(patientDto);
		if (pat != null && !pat.isEmpty() && pat.size() > 0) {
			return new ResponseDto(2, "已获得抽奖资格", lot);
		}

		// 添加抽奖用户
		patient.setLotteryNo(lot.getNo());
		patient.setPatientName(patientDto.getName());
		patient.setStatus(0);
		patient.setPatientId(patient.getId());
		patient.setCreateTime(new Date());
		lotteryDao.addLotteryUser(patient);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("成功获得抽奖资格");
		responseDto.setDetail(patient);
		return responseDto;
	}

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
	@Override
	public ResponseDto queryNewLotteryResult(PatientDto patientDto) throws Exception {
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		PatientDto patientDt = token != null ? token.getPatient() : new PatientDto();

		// 查询最新活动
		LotteryDto lotteryDto = new LotteryDto();
		LotteryDto lottery = lotteryDao.queryNewLottery(lotteryDto);

		// 查询中奖纪录
		patientDto.setLotteryNo(lottery.getNo());
		patientDto.setPhone(patientDt.getPhone());
		List<PatientDto> pats = lotteryDao.queryResultHis(patientDto);

		// 如果是正在进行的活动，则返回活动最后一次抽奖时间
		LotteryTimeRecordDto lotteryTimeRecordDto = new LotteryTimeRecordDto();
		if (lottery.getStatus() == 1) {
			lotteryTimeRecordDto.setLotteryNo(patientDto.getLotteryNo());
			lotteryTimeRecordDto = lotteryDao.queryLotteryTimeRecord(lotteryTimeRecordDto);
		}

		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", pats);
		map.put("status", lottery.getStatus());
		if (lotteryTimeRecordDto != null && lotteryTimeRecordDto.getLotteryTime() != null) {
			map.put("lotteryTime", lotteryTimeRecordDto.getLotteryTime());
		} else {
			map.put("lotteryTime", "");
		}

		responseDto.setResultDesc("中奖纪录");
		responseDto.setDetail(map);
		return responseDto;
	}

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
	@Override
	public ResponseDto queryJoinLottery(PatientDto patientDto) throws Exception {
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		PatientDto patientDt = token != null ? token.getPatient() : new PatientDto();

		/** step3:查询活动. */
		patientDto.setPhone(patientDt.getPhone());
		patientDto.setId(patientDt.getId());
		List<PatientDto> pats = lotteryDao.queryMeJoinLottery(patientDto);

		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", pats);
		responseDto.setDetail(map);
		responseDto.setResultDesc("参与的活动列表");
		return responseDto;
	}

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
	@Override
	public ResponseDto queryLotteryResultByLotteryNo(PatientDto patientDto) throws Exception {
		/** step1: 空值判断. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** setp2: 检验活动号. */
		if (!Util.isNotEmpty(patientDto.getLotteryNo())) {
			throw new BusinessException("请指定活动号");
		}

		/** step3:获取用户. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		PatientDto patientDt = token != null ? token.getPatient() : new PatientDto();

		// 查询中奖纪录
		patientDto.setPhone(patientDt.getPhone());
		List<PatientDto> pats = lotteryDao.queryResultHis(patientDto);

		// 查询活动
		LotteryDto lotteryDto = new LotteryDto();
		lotteryDto.setNo(patientDto.getLotteryNo());
		LotteryDto lottery = lotteryDao.queryLottery(lotteryDto);

		// 如果是正在进行的活动，则返回活动最后一次抽奖时间
		LotteryTimeRecordDto lotteryTimeRecordDto = new LotteryTimeRecordDto();
		if (lottery.getStatus() == 1) {
			lotteryTimeRecordDto.setLotteryNo(patientDto.getLotteryNo());
			lotteryTimeRecordDto = lotteryDao.queryLotteryTimeRecord(lotteryTimeRecordDto);
		}

		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", pats);
		map.put("status", lottery.getStatus());
		if (lotteryTimeRecordDto != null && lotteryTimeRecordDto.getLotteryTime() != null) {
			map.put("lotteryTime", lotteryTimeRecordDto.getLotteryTime());
		} else {
			map.put("lotteryTime", "");
		}

		responseDto.setResultDesc("中奖纪录");
		responseDto.setDetail(map);
		return responseDto;
	}

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
	@Override
	public ResponseDto exitLottery(PatientDto patientDto) throws Exception {
		/** step1: 空值判断. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** setp2: 检验活动号. */
		if (!Util.isNotEmpty(patientDto.getLotteryNo())) {
			throw new BusinessException("请指定活动号");
		}

		/** step3: 获取用户. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		PatientDto patientDt = token != null ? token.getPatient() : new PatientDto();
		patientDto.setPatientId(patientDt.getId());

		/** step4: */
		lotteryDao.exitLottery(patientDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("退出成功");
		return responseDto;
	}
}
