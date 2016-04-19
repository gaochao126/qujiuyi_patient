package com.jiuyi.qujiuyi.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ShortcutPayServlet extends HttpServlet {
    /** serialVersionUID. */
    private static final long serialVersionUID = -1976934934572962448L;
    private final static Logger logger = Logger.getLogger(ShortcutPayServlet.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("-----------------------------------------------kjzf");
        String content = getRequestContent(request);
        logger.info("-----------------------------------------------" + content);
    }

    /**
     * @description 获取上报数据
     * @param request
     * @return
     */
    private static String getRequestContent(HttpServletRequest request) {
        String content = "";
        try {
            InputStream in = request.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                content += new String(buffer, 0, len, "UTF-8");
            }
            in.close();
        } catch (IOException e) {
            logger.error("ApiServlet.getRequestContent#get request content failed", e);
        }
        return content;
    }
}