package com.sp.config.interceptors;

import com.alibaba.fastjson.JSON;
import com.sp.annotation.RepeatSubmit;
import com.sp.bean.vo.ResponseData;
import com.sp.common.contants.Constants;
import com.sp.util.HttpUtil;
import com.sp.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 拦截扫描到RepeatSubmit注解到方法,
 * 然后调用tokenService的checkToken()方法校验token是否正确，如果捕捉到异常就将异常信息渲染成json返回给前端
 * Created by admin on 2020/2/15.
 */
@Component
@Slf4j
public class RepeatSubmitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 被RepeatSubmit标识的扫描
        RepeatSubmit idempotent = method.getAnnotation(RepeatSubmit.class);
        if (idempotent != null) {
            try {
                if (isRepeatSubmit(request)) {
                    ResponseData failResult = ResponseData.ok("重复提交，请稍后再试！");
                    HttpUtil.writeReturnJson(response, JSON.toJSONString(failResult));
                    return false;
                }
            } catch (Exception e) {
                ResponseData failResult = ResponseData.fail("服务正忙，请稍后再试");
                HttpUtil.writeReturnJson(response, JSON.toJSONString(failResult));
                throw e;
            }
        }

        return true;
    }

    /**
     * 验证是否重复提交， 子类可覆盖该方法实现具体的防重复提交规则
     *
     * @param request
     * @return true 重复, false 不重复
     * @throws Exception
     */
    protected boolean isRepeatSubmit(HttpServletRequest request) throws Exception {
        String token = HttpUtil.getValueFromRequest(request, Constants.NO_REPEAT_TOKEN);
        if (StringUtils.isBlank(token)) {
            log.error("获取参数token为空");
            return true;
        }

        //TimeUnit.MILLISECONDS.sleep(2000);

        if (!redisUtils.hasKey(token)) {
            log.error("重复操作, 请稍后再试！ has");
            return true;
        }

        boolean remove = redisUtils.del(token);
        if (!remove) {
            log.error("重复操作, 请稍后再试！ del");
            return true;
        }
        return false;
    }

}
