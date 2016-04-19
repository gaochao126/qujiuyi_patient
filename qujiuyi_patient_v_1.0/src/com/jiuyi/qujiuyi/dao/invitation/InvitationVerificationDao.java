package com.jiuyi.qujiuyi.dao.invitation;

import java.util.List;

import com.jiuyi.qujiuyi.dto.invitation.InvitationVerificationDto;

/**
 * @description 验证码dao层接口
 * @author zhb
 * @createTime 2015年5月27日
 */
public interface InvitationVerificationDao {
    /**
     * @description 根据患者id获取邀请码验证记录
     * @param invitationVerificationDto
     * @return
     * @throws Exception
     */
    public List<InvitationVerificationDto> queryInvitationVerificationsByPatientId(InvitationVerificationDto invitationVerificationDto) throws Exception;

    /**
     * @description 根据患者id获取邀请码验证记录
     * @param invitationVerificationDto
     * @throws Exception
     */
    public void saveInvitationVerification(InvitationVerificationDto invitationVerificationDto) throws Exception;
}