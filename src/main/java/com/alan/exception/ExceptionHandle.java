package com.alan.exception;

import com.alibaba.fastjson.JSONArray;
import com.alan.controller.BaseController;
import com.alan.pojo.OperateRecord;
import com.alan.service.OperateRecordService;
import com.alan.utils.EnumCode;
import com.alan.utils.ResultUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@ControllerAdvice
public class ExceptionHandle extends BaseController {

    @Autowired
    private OperateRecordService operatingRecordService;

    private final  static Logger log = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Object handle(Exception e) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String remoteAddr = request.getRemoteAddr();
        String method = request.getMethod();

        OperateRecord or = new OperateRecord();
        or.setRequestUrl(request.getRequestURI());
        or.setRemoteAddr(remoteAddr);
        or.setMethod(method);
        or.setCreateTime(new Date());
        or.setUserId(super.getUserId());
        or.setIsSuccess(0);
        operatingRecordService.insert(or);


        if (e instanceof MyException) {
            MyException myException = (MyException)e;
            return myException.getResult();
        } else if (e instanceof AuthenticationException) {
            AuthenticationException authenticationException = (AuthenticationException)e;
            return JSONArray.toJSON(ResultUtil.result(EnumCode.LOGIN_FAIL.getValue(), authenticationException.getMessage()));
        }
        else {
            log.info("系统异常 {}",e);
            return ResultUtil.result(-1, "未知错误");
        }
    }
}
