package liuling.rpcCore.hook;


import liuling.rpcCommon.utils.NacosUtil;
import liuling.rpcCore.facotry.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 *  使用了单例模式创建钩子
 */
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    /**
     * 在 addClearAllHook 中，Runtime 对象是 JVM 虚拟机的运行时环境，调用其 addShutdownHook 方法增加一个钩子函数，
     * 创建一个新线程调用 clearRegistry 方法完成注销工作。这个钩子函数会在 JVM 关闭之前被调用。
     */
    public void addClearAllHook(){
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            NacosUtil.clearRegister();
            threadPool.shutdown();
        }));
    }


}
