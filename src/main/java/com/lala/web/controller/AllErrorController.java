package com.lala.web.controller;

import com.lala.service.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Web错误，全局处理
 * @author lala
 */
@Controller
public class AllErrorController implements ErrorController {

    private ErrorAttributes errorAttributes;
    private static final String ERROR_PATH = "/error";

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @Autowired
    public AllErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /** Web页面错误处理 **/
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
        int status = response.getStatus();
        switch(status) {
            case 403:
                return "403";
            case 404:
                return "404";
            case 500:
                return "500";
            default:
                return "index";
        }
    }

    /** 除Web页面外的错误处理，比如Json/XML等 **/
    @RequestMapping(value = ERROR_PATH, produces = "application/json")
    public ServiceResult errorApiHandler(HttpServletRequest request) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);
        Map<String, Object> map = this.errorAttributes.getErrorAttributes(servletWebRequest, false);
        int code = getStatus(request);
        return ServiceResult.ofMessage(code, String.valueOf(map.getOrDefault("message", "error")));
    }

    private int getStatus(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(code != null) {
            return code;
        }
        return 500;
    }
}
