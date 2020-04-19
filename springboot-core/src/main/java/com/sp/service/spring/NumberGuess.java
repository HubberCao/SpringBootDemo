package com.sp.service.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2020/4/2.
 */
@Component
public class NumberGuess {
    @Value("#{ T(java.lang.Math).random()*100.0}")
    private long randomeNumber;

    public long getRandomeNumber() {
        return randomeNumber;
    }
}
