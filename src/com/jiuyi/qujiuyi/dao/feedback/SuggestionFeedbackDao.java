package com.jiuyi.qujiuyi.dao.feedback;

import com.jiuyi.qujiuyi.dto.feedback.SuggestionFeedbackDto;

/**
 * @description 意见反馈dao层接口
 * @author zhb
 * @createTime 2015年5月18日
 */
public interface SuggestionFeedbackDao {
    /**
     * @description 创建意见反馈
     * @param suggestionFeedbackDto
     * @throws Exception
     */
    public void createSuggestionFeedback(SuggestionFeedbackDto suggestionFeedbackDto) throws Exception;
}