package com.sp.config.interceptors;

import com.sp.annotation.RepeatSubmit;
import com.sp.common.contants.Constants;
import com.sp.service.lock.RedisLuaDisLock;
import com.sp.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Created by admin on 2020/4/15.
 */
//@Component
@Aspect
@Slf4j
public class RepeatSubmitAspect {

    @Around("@annotation(repeatSubmit)")
    public Object around(ProceedingJoinPoint pjp, RepeatSubmit repeatSubmit) {
        HttpServletRequest request = HttpUtil.getRequest();
        String token = HttpUtil.getValueFromRequest(request, Constants.ACCESS_TOKEN);
        String path = request.getServletPath();
        String key = token + path;
        String clientId = UUID.randomUUID().toString();

        String request_id = HttpUtil.getValueFromRequest(request,"request_id");

        boolean success = RedisLuaDisLock.getLock(key, clientId, repeatSubmit.expireTime());
        if (success) {
            log.info("request_id:{}, key:{}, clientid:{}  get lock", request_id, key, clientId);
            Object result = null;
            try {
                result = pjp.proceed();
                //TimeUnit.SECONDS.sleep(1);
            } catch (Throwable e) {
                log.error("服务正忙，请稍后再试.", e);
            } finally {
                // 如果不主动释放锁，可以让并发的请求在repeatSubmit.expireTime()期间防重
                // 如果主动释放，则要考虑实践业务处理实践和repeatSubmit.expireTime()的差值，如这里测试的业务方法几乎不耗时，如不设置上面的sleep，则可能发生多个并发并发
                //RedisLuaDisLock.releaseLock(key, clientId);
            }
            return result;
        } else {
            return "repeat submit";
        }
    }
}
