package com.sp.service.spring;

/**
 * Created by admin on 2020/4/2.
 */
public abstract class CommandManager {
    protected abstract Command createCommand();


}

class AsyncCommand extends Command{

}

class Command {

}

