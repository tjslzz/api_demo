package com.api.swagger.config;

import com.alibaba.fastjson.JSON;
import com.api.swagger.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Objects;

@Slf4j
public abstract class AbstractInterceptorAdapter extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            request = preFilter(request);
        } catch (Exception e) {
            request = null;
        }
        if (request != null) {
            return true;
        }
        handleResponese(response);
        return false;
    }

    protected abstract HttpServletRequest preFilter(HttpServletRequest request);

    private void handleResponese(HttpServletResponse response) {
        PrintWriter printWriter = null;
        ResponseModel responseModel = new ResponseModel(401, "rate limiting...");
        try {
            printWriter = response.getWriter();
            printWriter.write(JSON.toJSONString(responseModel));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Objects.requireNonNull(printWriter).close();
        }
    }
}
