package com.jiuyi.qujiuyi.common.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author zhb
 * 
 */
public class RequestFilter implements Filter {
	/** 无需校验的url */
	private static final List<String> noCheckUrlList;
	static {
		noCheckUrlList = new ArrayList<String>();
		noCheckUrlList.add("index.html");
		noCheckUrlList.add("index.jsp");
		noCheckUrlList.add("customer_toLogin.html");
		noCheckUrlList.add("customer_toRegister.html");
		noCheckUrlList.add("customer_login.html");
		noCheckUrlList.add("customer_register.html");
		noCheckUrlList.add("index.jsp");
		noCheckUrlList.add("upload");
        noCheckUrlList.add("commodity");
		noCheckUrlList.add("shopping_putCommodityIntoShoppingCart.html");
		noCheckUrlList.add("shopping_orderList.html");
        noCheckUrlList.add("shopping_delOrder.html");
	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		HttpSession session = req.getSession(false);

		String reqUrl = req.getServletPath();
		boolean isCommonUrl = false;
		if (reqUrl.startsWith("/ico") || reqUrl.startsWith("/images") || reqUrl.startsWith("/css") || reqUrl.startsWith("/js")) {
			isCommonUrl = true;
		}
		reqUrl = reqUrl.substring(reqUrl.lastIndexOf("/") + 1);

		if (reqUrl.startsWith("FileUploadServlet")) {
			RequestDispatcher rdsp = request.getRequestDispatcher("FileUploadServlet");
			rdsp.forward(req, resp);
			return;
		}

		if (!isCommonUrl && (session == null || session.getAttribute("customerDto") == null) && noCheckUrlList != null && !noCheckUrlList.isEmpty()) {
			boolean b = false;
			for (String url : noCheckUrlList) {
				if (reqUrl.startsWith(url)) {
					b = true;
					break;
				}
			}

			if (!b) {
				return;
			}
		}

		chain.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}