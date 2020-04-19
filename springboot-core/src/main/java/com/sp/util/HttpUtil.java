package com.sp.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by admin on 2020/2/15.
 */
public class HttpUtil {

    /**
     * 从请求头和参数中获取参数
     * @param req
     * @param name
     * @return
     */
    public static String getValueFromRequest(HttpServletRequest req, String name) {
        if (req == null) {
            return StringUtils.EMPTY;
        }
        String value = req.getHeader(name);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return req.getParameter(name);
    }

    public static String getValueFromRequest(String name) {
        return getValueFromRequest(getRequest(), name);
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return servletRequestAttributes.getRequest();
    }

    /**
     * 返回的json值
     * @param response
     * @param json
     * @throws Exception
     */
    public static void writeReturnJson(HttpServletResponse response, String json) throws Exception{
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
