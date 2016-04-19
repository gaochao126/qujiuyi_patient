package com.jiuyi.qujiuyi.service.feedback;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.feedback.SuggestionFeedbackDto;

/**
 * @description 意见反馈业务层接口
 * @author zhb
 * @createTime 2015年5月18日
 */
public interface SuggestionFeedbackService {
    /**
     * @description 创建意见反馈
     * @param suggestionFeedbackDto
     * @return
     * @throws Exception
     */
    public ResponseDto createSuggestionFeedback(SuggestionFeedbackDto suggestionFeedbackDto) throws Exception;
}