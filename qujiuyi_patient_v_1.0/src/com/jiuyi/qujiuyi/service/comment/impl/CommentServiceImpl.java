package com.jiuyi.qujiuyi.service.comment.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.URLInvoke;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.comment.CommentDao;
import com.jiuyi.qujiuyi.dao.consult.ConsultDao;
import com.jiuyi.qujiuyi.dao.doctor.DoctorDao;
import com.jiuyi.qujiuyi.dto.chat.ChatDto;
import com.jiuyi.qujiuyi.dto.comment.CommentDto;
import com.jiuyi.qujiuyi.dto.common.RequestDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.consult.ConsultDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.comment.CommentService;

/**
 * @description 评论业务层实现
 * @author zhb
 * @createTime 2015年9月2日
 */
@Service
public class CommentServiceImpl implements CommentService {
	@Autowired
	private CommentDao commentDao;

	@Autowired
	private ConsultDao consultDao;

	@Autowired
	private DoctorDao doctorDao;

	/**
	 * @description 评论医生
	 * @param commentDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto patientCommentDoctorService(CommentDto commentDto) throws Exception {
		if (commentDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		if (!Util.isNotEmpty(commentDto.getServiceId())) {
			throw new BusinessException("服务id不能为空");
		}

		if (commentDto.getServiceType() == null) {
			throw new BusinessException("服务类型不能为空");
		}

		List<CommentDto> comments = commentDao.queryCommentByServiceId(commentDto);
		if (comments != null && !comments.isEmpty() && comments.size() > 0) {
			throw new BusinessException("该服务已评论");
		}

		// 如果是咨询，判断医生是否回复，如果没有回复，不可评价
		if (commentDto.getServiceType() == 2) {
			// 判断咨询医生接受状态
			ConsultDto consultDto = new ConsultDto();
			consultDto.setId(commentDto.getServiceId());
			ConsultDto consult = consultDao.queryConsultById(consultDto);
			// 如果医生已经接受，判断医生是否有回复
			if (consult.getAcceptStatus() == 1 && consult.getConsultStatus() == 1) {
				ChatDto chatDto = new ChatDto();
				chatDto.setServiceId(commentDto.getServiceId());
				List<ChatDto> chats = consultDao.queryChatBySendTypeAndServiceId(chatDto);
				if (chats == null || chats.isEmpty() || chats.size() == 0) {
					throw new BusinessException("您不可评价医生未回复的咨询");
				}
			}
		}

		if (commentDto.getDoctorId() == null) {
			throw new BusinessException("医生id不能为空");
		}

		if (commentDto.getDoctorType() == null || (commentDto.getDoctorType() != 0 && commentDto.getDoctorType() != 1)) {
			throw new BusinessException("医生类型未知");
		}

		if (commentDto.getSatisfaction() == null || (commentDto.getSatisfaction() != 1 && commentDto.getSatisfaction() != 2 && commentDto.getSatisfaction() != 3 && commentDto.getSatisfaction() != 4 && commentDto.getSatisfaction() != 5)) {
			throw new BusinessException("满意度未知");
		}

		if (commentDto.getTreatmentEffect() == null || (commentDto.getTreatmentEffect() != 1 && commentDto.getTreatmentEffect() != 2 && commentDto.getTreatmentEffect() != 3 && commentDto.getTreatmentEffect() != 4 && commentDto.getTreatmentEffect() != 5)) {
			throw new BusinessException("治疗效果未知");
		}

		TokenDto token = CacheContainer.getToken(commentDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		commentDto.setCommentTime(new Date());
		commentDto.setPatientId(patient.getId());
		commentDao.patientCommentDoctorService(commentDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("评价成功");

		if (commentDto.getServiceType() == 2) {
			// 更改咨询状态
			ConsultDto consultDto = new ConsultDto();
			consultDto.setId(commentDto.getServiceId());
			consultDto.setStartTime(null);
			consultDao.updateConsultStatus(consultDto);

			// 更改该服务未读消息为已读
			ChatDto chatDto = new ChatDto();
			chatDto.setServiceId(commentDto.getServiceId());
			consultDao.updateReadStatusByServiceId(chatDto);

			// 查询医生
			DoctorDto doctorDto = new DoctorDto();
			doctorDto.setId(commentDto.getDoctorId());
			DoctorDto doctor = doctorDao.queryDoctorInfo(doctorDto);

			// 同步聊天服务器
			RequestDto requestDto = new RequestDto();
			requestDto.setCmd("stopConsultByPatient");
			Map<String, Object> params = new HashMap<String, Object>();
			requestDto.setParams(params);
			params.put("consultId", commentDto.getServiceId());
			params.put("patientId", commentDto.getPatientId());
			params.put("doctorId", commentDto.getDoctorId());
			params.put("doctorName", doctor.getName());
			URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

		}
		return responseDto;
	}

	/**
	 * @description 获取我的评论
	 * @param commentDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getMyComments(CommentDto commentDto) throws Exception {
		if (commentDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		TokenDto token = CacheContainer.getToken(commentDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		commentDto.setPatientId(patient.getId());
		List<CommentDto> list = commentDao.getMyComments(commentDto);

		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", commentDto.getPage());
		detail.put("list", list);
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 根据医生获取评价
	 * @param commentDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getCommentsByDoctor(CommentDto commentDto) throws Exception {
		if (commentDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		List<CommentDto> list = commentDao.getCommentsByDoctor(commentDto);

		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", commentDto.getPage());
		detail.put("list", list);
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(detail);
		return responseDto;
	}
	
	
}