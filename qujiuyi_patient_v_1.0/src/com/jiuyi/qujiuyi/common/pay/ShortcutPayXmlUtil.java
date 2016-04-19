package com.jiuyi.qujiuyi.common.pay;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @description 快捷支付XML工具类
 * @author zhb
 * @createTime 2015年5月6日
 */
public class ShortcutPayXmlUtil {
    /**
     * @description 获取请求xml
     * @param tranCode
     * @param currTime
     * @param obj
     * @return
     */
    public static String getRequestXml(String tranCode, String currTime, Object obj) throws Exception {
        StringBuffer headXml = new StringBuffer();
        headXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        headXml.append("<message>");
        headXml.append("<head>");
        headXml.append("<version>01</version>");
        headXml.append("<msgType>0001</msgType>");
        headXml.append("<chanId>" + ShortcutPayCfig.getString("MERCHANT_CHAN_ID") + "</chanId>");
        headXml.append("<merchantNo>" + ShortcutPayCfig.getString("MERCHANT_ID") + "</merchantNo>");
        headXml.append("<clientDate>" + currTime + "</clientDate>");
        headXml.append("<serverDate></serverDate>");
        headXml.append("<tranFlow>" + ShortcutPayCfig.getString("MERCHANT_ID") + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "</tranFlow>");
        headXml.append("<tranCode>" + tranCode + "</tranCode>");
        headXml.append("<respCode></respCode>");
        headXml.append("<respMsg></respMsg>");
        headXml.append("</head>");
        headXml.append("<body>");
        headXml.append(toXml(obj));
        headXml.append("</body>");
        headXml.append("</message>");
        return headXml.toString();
    }

    /**
     * @description 获取请求xml
     * @param tranCode
     * @param obj
     * @return
     */
    public String getMsgHeadXml(String tranCode, Object obj) throws Exception {
        StringBuffer headXml = new StringBuffer();
        headXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        headXml.append("<message>");
        headXml.append("<head>");
        headXml.append("<version>01</version>");
        headXml.append("<msgType>0001</msgType>");
        headXml.append("<chanId>" + ShortcutPayCfig.getString("MERCHANT_CHAN_ID") + "</chanId>");
        headXml.append("<merchantNo>" + ShortcutPayCfig.getString("MERCHANT_ID") + "</merchantNo>");
        headXml.append("<respCode></respCode>");
        headXml.append("<respMsg></respMsg>");
        headXml.append("</head>");
        headXml.append("<body>");
        headXml.append(toXml(obj));
        headXml.append("</body>");
        headXml.append("</message>");
        return headXml.toString();
    }

    /**
     * @description java对象转换xml
     * @param obj
     * @return
     */
    public static String toXml(Object obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap<String, Field>();
        for (Field f : fields) {
            fieldMap.put(f.getName(), f);
        }
        Class<?> superclass = obj.getClass().getSuperclass();
        while (!superclass.getName().equals("java.lang.Object")) {
            for (Field f : superclass.getDeclaredFields()) {
                if (!fieldMap.containsKey(f.getName())) {
                    fieldMap.put(f.getName(), f);
                }
            }
            superclass = superclass.getSuperclass();
        }
        Map<String, Method> methods = getGetterMethods(obj.getClass());
        String content = "";
        for (String key : methods.keySet()) {
            Method m = methods.get(key);
            Object value = null;
            try {
                Class<?> returnType = methods.get(key).getReturnType();
                if (returnType.isPrimitive() || returnType.getName().equals("java.lang.String")) {
                    value = m.invoke(obj);
                } else if (List.class.isAssignableFrom(returnType)) {// 集合类型

                } else if (returnType.isArray()) {// 数据类型

                }
            } catch (Exception e) {
                throw e;
            }
            if (value != null) {
                String elementName = m.getName().substring(3);
                if (!fieldMap.containsKey(elementName)) {
                    elementName = elementName.substring(0, 1).toLowerCase() + elementName.substring(1);
                }
                content += "<" + elementName + ">" + value.toString() + "</" + elementName + ">";
            }
        }
        return content;
    }

    /**
     * @description 获取getter方法
     * @param clazz
     * @return
     */
    private static Map<String, Method> getGetterMethods(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Map<String, Method> map = new HashMap<String, Method>();
        for (Method m : methods) {
            if (!m.getName().startsWith("get")) {
                continue;
            }
            if (m.getReturnType().getName().equals("void")) {
                continue;
            }
            if (map.containsKey(m.getName())) {
                continue;
            }
            if (m.getParameterTypes().length == 0) {
                map.put(m.getName(), m);
            }
        }
        return map;
    }

    /**
     * @description xml转换java对象
     * @param xml
     * @param nodePath
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ShortcutRespBean getRespBeanFromXml(String xml, String node, ShortcutRespBean respBean) throws Exception {
        try {
            Document doc = DocumentHelper.parseText(xml);
            List<Element> elements = doc.getRootElement().element(node).elements();
            Map<String, Method> methods = getSetterMethods(respBean.getClass());
            for (Element element : elements) {
                if (element.isTextOnly()) {
                    String setterName = "set" + element.getName().substring(0, 1).toUpperCase() + element.getName().substring(1);
                    if (methods.containsKey(setterName)) {
                        Method method = methods.get(setterName);
                        Class<?> c = method.getParameterTypes()[0];
                        Object value = null;
                        if (c.isPrimitive() || c.getName().equals("java.lang.String")) {
                            String className = c.getName();
                            String text = element.getText();
                            if ("java.lang.String".equals(className)) {
                                value = text;
                            } else if ("int".equals(className) || "java.lang.Integer".equals(className)) {
                                value = Integer.parseInt(text);
                            } else if ("long".equals(className) || "java.lang.Long".equals(className)) {
                                value = Long.parseLong(text);
                            } else if ("float".equals(className) || "java.lang.Float".equals(className)) {
                                value = Float.parseFloat(text);
                            } else if ("char".equals(className) || "java.lang.Character".equals(className)) {
                                if (text.length() == 1) {
                                    value = Character.valueOf(text.charAt(0));
                                }
                            } else if ("double".equals(className) || "java.lang.Double".equals(className)) {
                                value = Double.parseDouble(text);
                            } else if ("byte".equals(className) || "java.lang.Byte".equals(className)) {
                                value = Byte.parseByte(text);
                            } else if ("short".equals(className) || "java.lang.Short".equals(className)) {
                                value = Short.parseShort(text);
                            } else if ("boolean".equals(className) || "java.lang.Boolean".equals(className)) {
                                value = Boolean.parseBoolean(text);
                            }
                        }
                        methods.get(setterName).invoke(respBean, value);
                    }
                } else {// 集合类型
                    String setterName = "set" + element.getName().substring(0, 1).toUpperCase() + element.getName().substring(1);
                    List<Element> list = element.elements();
                    List<CardInfoBean> cardInfos = new ArrayList<CardInfoBean>();
                    for (Element element1 : list) {
                        if (!element1.isTextOnly()) {
                            List<Element> list2 = element1.elements();
                            CardInfoBean bean = new CardInfoBean();
                            cardInfos.add(bean);
                            for (Element element2 : list2) {
                                String setterName1 = "set" + element2.getName().substring(0, 1).toUpperCase() + element2.getName().substring(1);
                                Map<String, Method> methods1 = getSetterMethods(bean.getClass());
                                if (element2.isTextOnly()) {
                                    if (methods1.containsKey(setterName1)) {
                                        Method method = methods1.get(setterName1);
                                        Class<?> c = method.getParameterTypes()[0];
                                        Object value = null;
                                        if (c.isPrimitive() || c.getName().equals("java.lang.String")) {
                                            String className = c.getName();
                                            String text = element2.getText();
                                            if ("java.lang.String".equals(className)) {
                                                value = text;
                                            } else if ("int".equals(className) || "java.lang.Integer".equals(className)) {
                                                value = Integer.parseInt(text);
                                            } else if ("long".equals(className) || "java.lang.Long".equals(className)) {
                                                value = Long.parseLong(text);
                                            } else if ("float".equals(className) || "java.lang.Float".equals(className)) {
                                                value = Float.parseFloat(text);
                                            } else if ("char".equals(className) || "java.lang.Character".equals(className)) {
                                                if (text.length() == 1) {
                                                    value = Character.valueOf(text.charAt(0));
                                                }
                                            } else if ("double".equals(className) || "java.lang.Double".equals(className)) {
                                                value = Double.parseDouble(text);
                                            } else if ("byte".equals(className) || "java.lang.Byte".equals(className)) {
                                                value = Byte.parseByte(text);
                                            } else if ("short".equals(className) || "java.lang.Short".equals(className)) {
                                                value = Short.parseShort(text);
                                            } else if ("boolean".equals(className) || "java.lang.Boolean".equals(className)) {
                                                value = Boolean.parseBoolean(text);
                                            }
                                        }
                                        method.invoke(bean, value);
                                    }
                                }
                            }
                        }
                    }
                    methods.get(setterName).invoke(respBean, cardInfos);
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return respBean;
    }

    /**
     * @description 获取setter方法
     * @param clazz
     * @return
     */
    private static Map<String, Method> getSetterMethods(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Map<String, Method> map = new HashMap<String, Method>();
        for (Method m : methods) {
            if (!m.getName().startsWith("set")) {
                continue;
            }
            if (map.containsKey(m.getName())) {
                continue;
            }
            if (m.getParameterTypes().length == 1) {
                map.put(m.getName(), m);
            }
        }
        return map;
    }
}