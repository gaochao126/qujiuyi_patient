package com.jiuyi.qujiuyi.service.feedback.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.feedback.SuggestionFeedbackDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.feedback.SuggestionFeedbackDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.feedback.SuggestionFeedbackService;

/**
 * @description 意见反馈业务层实现
 * @author zhb
 * @createTime 2015年5月18日
 */
@Service
public class SuggestionFeedbackServiceImpl implements SuggestionFeedbackService {
	@Autowired
	private SuggestionFeedbackDao suggestionFeedbackDao;

	/**
	 * @description 创建意见反馈
	 * @param suggestionFeedbackDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto createSuggestionFeedback(SuggestionFeedbackDto suggestionFeedbackDto) throws Exception {
		/** step1:空异常处理. */
		if (suggestionFeedbackDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:校验反馈内容. */
		if (!Util.isNotEmpty(suggestionFeedbackDto.getContent().trim())) {
			throw new BusinessException("反馈内容必须输入");
		}

		/** step3:获取用户. */
		TokenDto token = CacheContainer.getToken(suggestionFeedbackDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		suggestionFeedbackDto.setContactWay(patient.getPhone());

		/** step4:保存数据. */
		suggestionFeedbackDto.setUserId(CacheContainer.getToken(suggestionFeedbackDto.getToken()).getPatient().getId());
		suggestionFeedbackDto.setCreateTime(new Date());
		suggestionFeedbackDao.createSuggestionFeedback(suggestionFeedbackDto);

		/** step5:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("提交成功");
		return responseDto;
	}
}