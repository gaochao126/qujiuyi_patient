package com.jiuyi.qujiuyi.common.pay;

import java.io.Serializable;

public class CardInfoBean implements Serializable{
    /** serialVersionUID. */
    private static final long serialVersionUID = -953529406899800027L;

    /** 短卡号. */
    private String storableCardNo;

    /** bankNo. */
    private String bankNo;

    /** cardType. */
    private String cardType;

    /** phoneNo. */
    private String phoneNo;

    public String getStorableCardNo() {
        return storableCardNo;
    }

    public void setStorableCardNo(String storableCardNo) {
        this.storableCardNo = storableCardNo;
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
}
