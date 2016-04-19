package com.jiuyi.qujiuyi.common.pay;

public class Test {
    public static void main(String[] args) {
        test2();
    }

    public static void test1() {
        ShortcutReqBean reqBean = new ShortcutReqBean();
        String merOrderId = System.currentTimeMillis() + "";// 订单ID
        String subject = "";
        String bankNo = "01030000";// 银行号
        String cardNo = "6228480478302302672"; // null"6226193300216676"//卡号
        String expiredDate = "";// 卡有效期
        String cvv2 = "123";// 卡校验码
        String amount = "1.00";// 金额
        String custName = "高超";// 持卡人姓名
        String custIdNo = "152324199101021410";// 持卡人证件号
        String custIdType = "0";// 持卡人证件类型
        String saveCustFlag = "0";// 是否保存客户信息
        String custId = "test002";// 客户号
        String phoneNo = "18523419004";// 手机号
        String phoneVerCode = "507845";// 手机验证码
        String phoneToken = "507845";// 手机校验码令牌
        String storableCardNo = "123"; // 短卡号
        String backUrl = "http://113.204.51.36:51102/qujiuyi_chat/api"; // 回调地址
        String msgExt = "123";// 附加信息

        reqBean.setMerOrderId(merOrderId);
        reqBean.setMsgExt(msgExt);
        reqBean.setSubject(subject);
        reqBean.setSaveCustFlag(saveCustFlag);
        reqBean.setPhoneVerCode(phoneVerCode);
        reqBean.setPhoneToken(phoneToken);
        reqBean.setBackUrl(backUrl);
        reqBean.setCustId(custId);
        reqBean.setCustName(custName);
        reqBean.setCustIdNo(custIdNo);
        reqBean.setCustIdType(custIdType);
        reqBean.setCardNo(cardNo);
        reqBean.setStorableCardNo(storableCardNo);
        reqBean.setBankNo(bankNo);
        reqBean.setExpiredDate(expiredDate);
        reqBean.setCvv2(cvv2);
        reqBean.setAmount(amount);
        reqBean.setPhoneNo(phoneNo);

        try {
            ShortcutRespBean respBean = ShortcutPayUtil.execute(reqBean, ShortcutPayEnum.QP0001);
            System.out.println(respBean.getRespCode() + "---" + respBean.getRespMsg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test2() {
        ShortcutReqBean reqBean = new ShortcutReqBean();
        String merOrderId = "000001";// 订单ID
        String custId = "0124";// 客户号
        String custName = "高超";// 持卡人姓名
        String custIdNo = "152324199101021410";// 持卡人证件号
        String custIdType = "0";// 持卡人证件类型
        String cardNo = "6228480478302302672"; // null"6226193300216676"//卡号
        String storableCardNo = "6228482672";// 短卡号
        String bankNo = "01030000";// 银行号
        String expiredDate = "";// 卡有效期
        String cvv2 = "";// 卡校验码
        String amount = "0.01";// 金额
        String phoneNo = "15310559572";// 手机号

        reqBean.setMerOrderId(merOrderId);
        reqBean.setCustId(custId);
        reqBean.setCustName(custName);
        reqBean.setCustIdNo(custIdNo);
        reqBean.setCustIdType(custIdType);
        reqBean.setCardNo(cardNo);
        reqBean.setStorableCardNo(storableCardNo);
        reqBean.setBankNo(bankNo);
        reqBean.setExpiredDate(expiredDate);
        reqBean.setCvv2(cvv2);
        reqBean.setAmount(amount);
        reqBean.setPhoneNo(phoneNo);

        try {
            ShortcutRespBean respBean = ShortcutPayUtil.execute(reqBean, ShortcutPayEnum.QP0002);
            System.out.println(respBean.getRespXml());
            System.out.println(respBean.getRespCode() + "---" + respBean.getRespMsg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test3() {
        ShortcutReqBean reqBean = new ShortcutReqBean();

        reqBean.setMerOrderId(System.currentTimeMillis() + "");
        reqBean.setCustId("test002");
        reqBean.setCardNo("123131");
        reqBean.setStorableCardNo("1231");
        reqBean.setBankNo("12313213");
        try {
            ShortcutRespBean respBean = ShortcutPayUtil.execute(reqBean, ShortcutPayEnum.QP0003);
            System.out.println(respBean.getRespCode() + "---" + respBean.getRespMsg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test4() {
        ShortcutReqBean reqBean = new ShortcutReqBean();
        reqBean.setCustId("0124");
        reqBean.setCardType("1");
        reqBean.setStorableCardNo("6228483672");
        reqBean.setBankNo("");
        try {
            ShortcutRespBean respBean = ShortcutPayUtil.execute(reqBean, ShortcutPayEnum.QP0004);
            System.out.println(respBean.getRespXml());
            System.out.println(respBean.getCardInfos().size() + ";" + respBean.getCardInfos().get(0).getStorableCardNo() + ";"
                    + respBean.getCardInfos().get(0).getCardType());
            System.out.println(respBean.getCustId() + ";" + respBean.getCardNum() + ";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test5() {

    }

    public static void test6() {

    }

    public static void test7() {

    }

    public static void test8() {

    }

    public static void test9() {

    }
}