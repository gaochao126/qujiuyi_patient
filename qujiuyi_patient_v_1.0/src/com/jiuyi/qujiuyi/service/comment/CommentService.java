package com.jiuyi.qujiuyi.service.comment;

import com.jiuyi.qujiuyi.dto.comment.CommentDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;

/**
 * @description 评论业务层接口
 * @author zhb
 * @createTime 2015年9月2日
 */
public interface CommentService {
    /**
     * @description 评论医生
     * @param commentDto
     * @return
     * @throws Exception
     */
    public ResponseDto patientCommentDoctorService(CommentDto commentDto) throws Exception;

    /**
     * @description 获取我的评论
     * @param commentDto
     * @return
     * @throws Exception
     */
    public ResponseDto getMyComments(CommentDto commentDto) throws Exception;

    /**
     * @description 根据医生获取评价
     * @param commentDto
     * @return
     * @throws Exception
     */
    public ResponseDto getCommentsByDoctor(CommentDto commentDto) throws Exception;
}