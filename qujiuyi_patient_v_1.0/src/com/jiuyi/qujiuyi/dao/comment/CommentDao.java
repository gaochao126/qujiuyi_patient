package com.jiuyi.qujiuyi.dao.comment;

import java.util.List;

import com.jiuyi.qujiuyi.dto.comment.CommentDto;

/**
 * @description 评论dao层接口
 * @author zhb
 * @createTime 2015年9月2日
 */
public interface CommentDao {
    /**
     * @description 患者评论医生
     * @param commentDto
     * @throws Exception
     */
    public void patientCommentDoctorService(CommentDto commentDto) throws Exception;

    /**
     * @description 获取患者评价
     * @return
     * @throws Exception
     */
    public List<CommentDto> getMyComments(CommentDto commentDto) throws Exception;

    /**
     * @description 根据医生获取评价
     * @return
     * @throws Exception
     */
    public List<CommentDto> getCommentsByDoctor(CommentDto commentDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 根据服务ID和医生患者ID查询评论
	 * 
	 * @param commentDto
	 * @return
	 * @throws Exception
	 * @Date 2015年11月27日
	 */
	public List<CommentDto> queryCommentByServiceId(CommentDto commentDto) throws Exception;
}