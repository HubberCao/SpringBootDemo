package com.sp.service.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2020/4/2.
 */
@Component
public class ShapeGuess {

    @Value("#{ numberGuess.randomeNumber}")
    private long initSeed;

    @Value("#{ systemProperties['user.region'] }")
    private String defaultLocale;

    public long getInitSeed() {
        return initSeed;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }
}
