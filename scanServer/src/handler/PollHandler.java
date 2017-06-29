package handler;

import dao.DataDAO;
import entity.Data;
import listener.NettyListener;
import luncher.Startup;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExceptionUtils;
import utils.Lock;

/**
 * Created by Passerby_B on 2017/5/1.
 */
public class PollHandler {

    private DataDAO dataDAO = new DataDAO();
    final Logger logger = LoggerFactory.getLogger("SEND");

    /**
     * 开始轮询
     */
    public void start() {
        synchronized (Lock.lock) {
            try {
                while (true) {
//                    Data data = dataDAO.getFirstValidData("data");
                	List<Data> dataList = dataDAO.getValidDataList("data");
                	
                	if(dataList != null){
                		for (Data data : dataList) {
    	                    int result = dataHandler(data.getData());
    	                    if (result == 2) {
    	                    	//发送成功设置消息已经发送和消息有效性不变
    	                        dataDAO.updateData("data", data.getId(), 1, 0);
    	                    } else if (result == 3){
    	                        //如果发送的时候出现异常，那么这条数据有可能是错误的，作废
    	                    	//作废的标识发送和有效都为1
    	                        dataDAO.updateData("data", data.getId(), 1, 1);
    	                    }
    	                    Thread.sleep(2000);
    					}
                	} else {
                        Lock.isRun = false;
                        Lock.lock.wait();//没有要发送的数据则暂停并释放锁
              		}
                	
                	//遍历玩之后将dataList至空
                	dataList = null;
                	
//                    if (data != null) {
//                        int result = dataHandler(data.getData());
//                        if (result == 2) {
//                            dataDAO.updateData("data", data.getId(), 1, 0);
//                        } else if (result == 3){
//                            //如果发送的时候出现异常，那么这条数据有可能是错误的，作废
//                            dataDAO.updateData("data", data.getId(), 0, 1);
//                        }
//                        Thread.sleep(1000);
//                    } else {
//                        Lock.isRun = false;
//                        Lock.lock.wait();//没有要发送的数据则暂停并释放锁
//                    }
                }
            } catch (InterruptedException e) {
                logger.error(ExceptionUtils.getTrace(e));
            }
        }
    }

    /**
     * 发送数据
     *
     * @param str
     */
    public Integer dataHandler(String str) {
        try {
            String[] split = str.split("\\$");
            String head = split[0];//消息头
            String body = split[1];//报文
            String[] splitHead = head.split("#");
            int port = Integer.parseInt(splitHead[0]);//端口

            NettyListener nettyListener = Startup.nettyListeners.get(port);

            if (nettyListener != null) {
                return nettyListener.sendMsg(body);
            }
            return 1;
        }catch (Exception e){
            logger.error(ExceptionUtils.getTrace(e));
            return 3;
        }
    }
}