package com.andy.spring.util;

import com.andy.spring.context.ServletContext;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Servlet 工具
 *
 * @author 庞先海 2020-01-27
 */
public class ServletUtil {

    /**
     * 判断请求是否来之手机端
     */
    public static boolean isMobile() {
        String userAgent = ServletContext.getRequest().getHeader("USER-AGENT");
        if (StringUtils.isEmpty(userAgent)) {
            userAgent = "";
        }
        userAgent = userAgent.toLowerCase();
        //先检测几个特定的pc来源
        String[] desktopSysAgents = new String[] {"Windows NT", "compatible; MSIE 9.0;", "Macintosh"};
        for (String d : desktopSysAgents) {
            if (userAgent.toLowerCase().contains(d.toLowerCase())) {
                return false;
            }
        }
        //在通过正则表达式检测是否手机浏览器
        String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i" + "|windows (phone|ce)|blackberry"
                          + "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
                          + "|laystation portable)|nokia|fennec|htc[-_]"
                          + "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
        String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser" + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
        Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
        Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);
        // 匹配
        Matcher matcherPhone = phonePat.matcher(userAgent);
        Matcher matcherTable = tablePat.matcher(userAgent);
        return matcherPhone.find() || matcherTable.find();
    }

    /**
     * 获取请求方的ip地址
     *
     * @return ip地址
     */
    public static String getIpAddress() {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        HttpServletRequest request = ServletContext.getRequest();
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (! ("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    public static String parseParam(HttpServletRequest request) {
        StringBuilder paramBuilder = new StringBuilder();
        Map<String, String[]> paramMap = request.getParameterMap();
        boolean isPost = RequestMethod.POST.toString().equalsIgnoreCase(request.getMethod());
        boolean isPut = RequestMethod.PUT.toString().equalsIgnoreCase(request.getMethod());
        if (isPost || isPut) {
            paramBuilder.append(ServletContext.getParam());
            for (String paramKey : paramMap.keySet()) {
                paramBuilder.append("&").append(paramKey);
                paramBuilder.append("=").append(request.getParameter(paramKey));
            }
        } else {
            paramBuilder.append(request.getQueryString());
        }
        return paramBuilder.toString();
    }

}