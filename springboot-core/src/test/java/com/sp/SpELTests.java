package com.sp;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Created by admin on 2020/4/1.
 */
public class SpELTests {

    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();
        System.out.println(parser.parseExpression("null").getValue());
    }
}
