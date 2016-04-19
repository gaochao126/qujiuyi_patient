package com.jiuyi.qujiuyi.common.pay;

import java.io.Serializable;
import java.util.List;

public class ShortcutRespBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6776404457583360192L;

    /**
     * 响应xml
     */
    private String respXml;

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

    /**
     * 交易响应码,当报文头响应码为C000000000时有值。
     * C0-提交成功，快捷支付受理成功(非实时返回)，等银行通知处理结果，处理完后返回到backUrl
     * 00-成功，快捷支付成功(实时返回)，并返回到backUrl 其它-失败，具体见错误码信息
     */
    private String tranRespCode;

    /**
     * 订单号,
     */
    private String merOrderId;

    /**
     * 客户号 当商户传了客户号给快捷支付系统时，返回客户号。 当客户没传客户号，但saveCustFlag标志为1时，返回客户号
     * 当客户没传客户号，saveCustFlag标志为0时，不返回客户号 通过数据密钥des加密
     */
    private String custId;

    /**
     * 系统参考号,快捷支付平台本条交易的内部订单号
     */
    private String refNo;

    /**
     * 短卡号,当报文头响应码为C000000000时有值
     */
    private String storableCardNo;

    /**
     * 金额
     */
    private String amount;

    /**
     * 令牌信息,作为消费交易的请求参数
     */
    private String phoneToken;

    /**
     * 卡张数
     */
    private String cardNum;

    private List<CardInfoBean> cardInfos;

    /**
     * 支付银行号,需要查询快捷支付的银行号，宝易互通提供，具体见附录
     */
    private String bankNo;

    /**
     * 卡类型,需要查询快捷支付的卡类型 0-信用卡,1-借记卡
     */
    private String cardType;

    /**
     * 手机号
     */
    private String phoneNo;

    /**
     * 发生退货交易时间
     */
    private String transTime;

    /**
     * 交易类型,QPXXXX 包括消费及关闭交易
     */
    private String txnType;

    /**
     * 交易状态,0-处理中 1-成功 2-失败
     */
    private String txnStat;

    /**
     * 商户交易时间, 格式为yyyyMMddHHmmss，例如：20140825010101
     */
    private String merTransTime;

    /**
     * 撤销标志,目前没用，保留
     */
    private String voidFlag;

    /**
     * 应答码文本信息
     */
    private String ranRespMsg;

    /**
     * 支付银行号
     */
    private String bankId;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 卡所属银行名称
     */
    private String bankNm;

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

    public String getTranRespCode() {
        return tranRespCode;
    }

    public void setTranRespCode(String tranRespCode) {
        this.tranRespCode = tranRespCode;
    }

    public String getMerOrderId() {
        return merOrderId;
    }

    public void setMerOrderId(String merOrderId) {
        this.merOrderId = merOrderId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getStorableCardNo() {
        return storableCardNo;
    }

    public void setStorableCardNo(String storableCardNo) {
        this.storableCardNo = storableCardNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPhoneToken() {
        return phoneToken;
    }

    public void setPhoneToken(String phoneToken) {
        this.phoneToken = phoneToken;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnStat() {
        return txnStat;
    }

    public void setTxnStat(String txnStat) {
        this.txnStat = txnStat;
    }

    public String getMerTransTime() {
        return merTransTime;
    }

    public void setMerTransTime(String merTransTime) {
        this.merTransTime = merTransTime;
    }

    public String getVoidFlag() {
        return voidFlag;
    }

    public void setVoidFlag(String voidFlag) {
        this.voidFlag = voidFlag;
    }

    public String getRanRespMsg() {
        return ranRespMsg;
    }

    public void setRanRespMsg(String ranRespMsg) {
        this.ranRespMsg = ranRespMsg;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNm() {
        return bankNm;
    }

    public void setBankNm(String bankNm) {
        this.bankNm = bankNm;
    }

    public String getRespXml() {
        return respXml;
    }

    public void setRespXml(String respXml) {
        this.respXml = respXml;
    }

    public List<CardInfoBean> getCardInfos() {
        return cardInfos;
    }

    public void setCardInfos(List<CardInfoBean> cardInfos) {
        this.cardInfos = cardInfos;
    }
}