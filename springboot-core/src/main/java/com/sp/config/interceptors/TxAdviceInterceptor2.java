package com.sp.config.interceptors;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 事务拦截
 * 注解：@Configuration/@Component，事务都生效
 */
//@Configuration
public class TxAdviceInterceptor2 {
    private static final int TX_METHOD_TIMEOUT = 5;
    private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.sp..service.*.*(..))";

    /**
     * 事务拦截类型
     * ("txSource") 不能去掉，如去掉就需要在application.properties中配置spring.main.allow-bean-definition-overriding=true，否则会报
     *
     * Invalid bean definition with name 'transactionAttributeSource' defined in class path resource
     * [org/springframework/transaction/annotation/ProxyTransactionManagementConfiguration.class]:
     * Cannot register bean definition [Root bean: class [null]; scope=; abstract=false; lazyInit=false; autowireMode=3; dependencyCheck=0;
     * autowireCandidate=true; primary=false; factoryBeanName=org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
     * factoryMethodName=transactionAttributeSource; initMethodName=null; destroyMethodName=(inferred); defined in class path resource
     * [org/springframework/transaction/annotation/ProxyTransactionManagementConfiguration.class]] for bean
     * 'transactionAttributeSource': There is already [Root bean: class [null]; scope=; abstract=false; lazyInit=false; autowireMode=3; dependencyCheck=0;
     * autowireCandidate=true; primary=false; factoryBeanName=txAdviceInterceptor2; factoryMethodName=transactionAttributeSource; initMethodName=null;
     * destroyMethodName=(inferred); defined in class path resource [com/sp/config/interceptors/TxAdviceInterceptor2.class]] bound.
     */
    @Bean("txSource")
    public TransactionAttributeSource transactionAttributeSource(){
         /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED,
                Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setTimeout(TX_METHOD_TIMEOUT);

        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("add*", requiredTx);
        txMap.put("save*", requiredTx);
        txMap.put("insert*", requiredTx);
        txMap.put("update*", requiredTx);
        txMap.put("delete*", requiredTx);
        txMap.put("get*", readOnlyTx);
        txMap.put("query*", readOnlyTx);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap( txMap );
        return source;
    }

    /**切面拦截规则 参数会自动从容器中注入*/
    @Bean
    public AspectJExpressionPointcutAdvisor pointcutAdvisor(TransactionInterceptor txInterceptor){
        AspectJExpressionPointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        pointcutAdvisor.setAdvice(txInterceptor);
        pointcutAdvisor.setExpression(AOP_POINTCUT_EXPRESSION);
        return pointcutAdvisor;
    }

    /*事务拦截器*/
    @Bean("txInterceptor")
    public TransactionInterceptor getTransactionInterceptor(PlatformTransactionManager tx){
        return new TransactionInterceptor(tx , transactionAttributeSource()) ;
    }
}
