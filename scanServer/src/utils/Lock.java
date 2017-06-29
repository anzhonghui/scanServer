package utils;


/**
 * Created by vaf71 on 2017/5/10.
 */
public class Lock {
    /**
     * 该类作为轮询数据库的线程锁，同时标记该线程是否处于暂停状态
     */
    public static Lock lock = new Lock();
    public static boolean isRun = true;//false 表示线程轮询已暂停
}
