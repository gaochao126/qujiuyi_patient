package com.jiuyi.qujiuyi.common.pay;

import java.io.Serializable;

public class ShortcutReqBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8046275942190691142L;

    private String token;

    /***************************** <head> ********************************/

    /**
     * 信息格式版本号,标识本报文格式版本号。 版本号不符应拒绝收发。 初始值：01
     */
    private String version;

    /**
     * 报文类型请求报文：0001应答报文：0002
     */
    private String msgType;

    /**
     * 渠道代号 ,由快捷支付平台分配，民生体系外商户填99
     */
    private String chanId;

    /**
     * 商户编号，由快捷支付平台分配
     */
    private String merchantNo;

    /**
     * 客户端系统日期，格式：YYYYMMDDHHMMSS
     */
    private String clientDate;

    /**
     * 服务端日期，格式：YYYYMMDDHHMMSS
     */
    private String serverDate;

    /**
     * 服务端系统流水号，唯一标识一笔交易，商户端自定义(必须以商户号开头)
     */
    private String tranFlow;

    /**
     * 快捷交易代码，如QP0001
     */
    private String tranCode;

    /**
     * 报文头返回码 成功：C000000000 错误：返回具体的响应码。宝易互通快捷支付平台返回的错误码为10位，
     */
    private String respCode;

    /**
     * 中文描述
     */
    private String respMsg;

    /***************************** </head> **************************************/

    /****************************** <body> **************************************/

    /**
     * 订单号,商户订单号，与动态鉴权一致
     */
    private String merOrderId;

    /**
     * 商品种类，如不传，则从结算至默认账户中
     */
    private String subject;

    /**
     * 需要快捷支付的银行号，必输，宝易互通提供，具体见附录
     */
    private String bankNo;

    /**
     * 需要快捷支付的卡号，首次
     */
    private String cardNo;

    /**
     * 需要快捷支付的卡有效期，卡号为借记卡时，无需输入，先月后年,如1219，代表2019年12月，贷记卡信用卡首次快捷支付必输
     */
    private String expiredDate;

    /**
     * 卡校验码,需要快捷支付的卡验证码，卡号为借记卡时，无需输入，贷记卡信用卡首次快捷支付必输
     */
    private String cvv2;

    /**
     * 付款金额,需要快捷支付的金额，以元为单位，如1.00为一元
     */
    private String amount;

    /**
     * 持卡人姓名,如用户已开通快捷支付，则再次支付只需要输入客户号，此信息不需填写，否则必填
     */
    private String custName;

    /**
     * 持卡人证件号,如用户已开通快捷支付，则再次支付只需要输入客户号，此信息不需填写，否则必填
     */
    private String custIdNo;

    /**
     * 持卡人证件类型,如用户已开通快捷支付，则再次支付只需要输入客户号，此信息不需填写，否则必填，默认值：0身份证
     */
    private String custIdType;

    /**
     * 是否保存客户信息,0-不保存（默认）1-保存（建议）
     */
    private String saveCustFlag;

    /**
     * 客户号,如用户已开通快捷支付，则再次支付只需要输入客户号，通过数据密钥des加密
     */
    private String custId;

    /**
     * 手机号,必须与持卡人在银行中开卡时预留的手机号一致
     */
    private String phoneNo;

    /**
     * 手机验证码,持卡人收到的手机验证码
     */
    private String phoneVerCode;

    /**
     * 手机校验码令牌,如鉴权交易返回，则必输
     */
    private String phoneToken;

    /**
     * 短卡号,在快捷再次支付时，若查询到客户号已绑定2张（含2张）以上的卡号时，短卡号必填，由卡号前六位后四位组成
     */
    private String storableCardNo;

    /**
     * 回调地址,快捷支付平台主动发起请求通知商户该回调地址,如果发卡银行系统能够实时返回消费交易应答消息，则将直接应答消费交易处理的结果信息；
     * 如果发卡银行系统无法实时返回消费交易应答消息将应答一个应答码给商户端，表示交易请求已经提交发卡银行，稍后将通知回调给商户端的backUrl。
     * 注意：如果支付交易返回给商户端的是 C0-提交成功状态时，商户端可接收快捷支付平台的回调结果(报文格式与支付响应报文一致)以判定此条交易是否成功，
     * 若回调给商户端的结果依旧不能够明确，此时商户端需主动调用查询QP0006接口以确定此条支付交易结果
     */
    private String backUrl;

    /**
     * 原消费交易系统参考号,原消费交易系统参考号(消费交易快捷返回的refNo)与原消费交易商户订单号必须填写一个
     */
    private String oriRefNo;

    /**
     * 原消费交易商户订单号,原消费交易系统参考号(消费交易快捷返回的refNo)与原消费交易商户订单号必须填写一个
     */
    private String oriMerOrderId;

    /**
     * 原交易日期,原消费交易商户端日期YYYYMMDDHHMMSS
     */
    private String oriTransDate;

    /**
     * 系统参考号,需要查询的系统参考号 系统参考号与（商户订单号+商户端交易日期）必须填写一个，如填写系统参考号，则以系统参考号查询为准
     */
    private String refNo;

    /**
     * 商户订单号,需要查询的商户订单号
     * 系统参考号与（商户订单号+商户端交易日期）必须填写一个，如填写商户订单号+商户端交易日期，如有成功记录，则成功记录为准
     * ，如没有成功记录，则以最后一笔为准
     */
    private String merOderNo;

    /**
     * 附加信息
     */
    private String msgExt;

    /**
     * 商户端交易日期,原消费交易商户端日期YYYYMMDDHHMMSS
     */
    private String merTransDate;

    /**
     * 卡类型
     */
    private String cardType;

    /**
     * 交易类型,原交易类型
     */
    private String txnType;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getChanId() {
        return chanId;
    }

    public void setChanId(String chanId) {
        this.chanId = chanId;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getClientDate() {
        return clientDate;
    }

    public void setClientDate(String clientDate) {
        this.clientDate = clientDate;
    }

    public String getServerDate() {
        return serverDate;
    }

    public void setServerDate(String serverDate) {
        this.serverDate = serverDate;
    }

    public String getTranFlow() {
        return tranFlow;
    }

    public void setTranFlow(String tranFlow) {
        this.tranFlow = tranFlow;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String getMerOrderId() {
        return merOrderId;
    }

    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getCvv2() {
        return cvv2;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustIdNo() {
        return custIdNo;
    }

    public void setCustIdNo(String custIdNo) {
        this.custIdNo = custIdNo;
    }

    public String getCustIdType() {
        return custIdType;
    }

    public void setCustIdType(String custIdType) {
        this.custIdType = custIdType;
    }

    public String getSaveCustFlag() {
        return saveCustFlag;
    }

    public void setSaveCustFlag(String saveCustFlag) {
        this.saveCustFlag = saveCustFlag;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPhoneVerCode() {
        return phoneVerCode;
    }

    public void setPhoneVerCode(String phoneVerCode) {
        this.phoneVerCode = phoneVerCode;
    }

    public String getPhoneToken() {
        return phoneToken;
    }

    public void setPhoneToken(String phoneToken) {
        this.phoneToken = phoneToken;
    }

    public String getStorableCardNo() {
        return storableCardNo;
    }

    public void setStorableCardNo(String storableCardNo) {
        this.storableCardNo = storableCardNo;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getOriRefNo() {
        return oriRefNo;
    }

    public void setOriRefNo(String oriRefNo) {
        this.oriRefNo = oriRefNo;
    }

    public String getOriMerOrderId() {
        return oriMerOrderId;
    }

    public void setOriMerOrderId(String oriMerOrderId) {
        this.oriMerOrderId = oriMerOrderId;
    }

    public String getOriTransDate() {
        return oriTransDate;
    }

    public void setOriTransDate(String oriTransDate) {
        this.oriTransDate = oriTransDate;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getMerOderNo() {
        return merOderNo;
    }

    public void setMerOderNo(String merOderNo) {
        this.merOderNo = merOderNo;
    }

    public String getMerTransDate() {
        return merTransDate;
    }

    public void setMerTransDate(String merTransDate) {
        this.merTransDate = merTransDate;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getMsgExt() {
        return msgExt;
    }

    public void setMsgExt(String msgExt) {
        this.msgExt = msgExt;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}