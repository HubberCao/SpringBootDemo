package com.sp.service.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by admin on 2020/4/2.
 */
@Configuration
public class AppConfig {

    @Bean
    //@Scope("prototype")
    public AsyncCommand asyncCommand() {
        AsyncCommand command = new AsyncCommand();

        return command;
    }

    /**
     * 方法查找注入，用于单例对原型有依赖的情况
     * @return
     */
    @Bean
    public CommandManager commandManager() {
        return new CommandManager() {
            @Override
            protected Command createCommand() {
                return asyncCommand();
            }
        };
    }

    @Bean
    public CommandService commandService1() {
        CommandService commandService = new CommandService();
        commandService.setAsyncCommand(asyncCommand());

        return commandService;
    }

}
