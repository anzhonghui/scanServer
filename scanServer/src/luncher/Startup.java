package luncher;

import dao.DataDAO;
import exception.*;
import gnu.io.SerialPort;
import handler.PollHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import listener.ConnectionListener;
import listener.NettyListener;
import listener.PortListener;
import timer.TimerManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExcelUtils;
import utils.ExceptionUtils;
import utils.SerialTool;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Passerby_B on 2017/5/1.
 */
public class Startup {
    private int baudrate = 9600;//串口波特率
    final static Logger scanLogger = LoggerFactory.getLogger("SCAN");
    final static Logger sendLogger = LoggerFactory.getLogger("SEND");
    private static PortListener portListener = new PortListener();
    public static Map<Integer, NettyListener> nettyListeners = new HashMap<>();//放置每个端口对应得 nettyListener
    Map<Integer, ChannelFuture> channelFutures = new HashMap<>();//放置每个端口对应得 channelFuture
    private static EventLoopGroup group = new NioEventLoopGroup();//netty 多个客户端公用线程组

    /**
     * 开始
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            String inetHost = null;//ip 地址
            List<Integer> portList = null;//端口号集合
            Startup startup = new Startup();
            inetHost = ExcelUtils.getAddress();//获取 ip 地址
            portList = ExcelUtils.getPortList();//获取端口号集合

            scanLogger.debug("ip地址:{}", inetHost);
            scanLogger.debug("端口号:{}", portList);

            //监听串口
            startup.openSerialPort();

            //定时清理数据库中已发送的记录
            //TODO 测试收发量，打开
            // startup.openClearDb();

            // startup.createBootstrap(group, "127.0.0.1", 15465);
            //开启多个 netty 客户端
            startup.openNetty(inetHost,portList);
            
            //开启发送漏扫的任务
            TimerManager timerManager = new TimerManager();

        } catch (Exception e) {
            scanLogger.error(ExceptionUtils.getTrace(e));
        }
    }


    /**
     * 定时清理数据库
     */
    private void openClearDb() {
        long intervalTime = 2 * 60 * 1000;//间隔时间
        final DataDAO dataDAO = new DataDAO();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                dataDAO.deleteIsSendData("data");
                scanLogger.debug("数据库已清空");
            }
        };
        new Timer().scheduleAtFixedRate(timerTask, intervalTime, intervalTime);
    }

    /**
     * 从数据库取数据
     */
    public static void openPoll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new PollHandler().start();
            }
        }).start();
    }

    /**
     * 开启 netty
     */
    public void openNetty(final String inetHost, List<Integer> portList) {
        for (int i = 0; i < portList.size(); i++) {//开启多个客户端
            final int port = portList.get(i);
            sendLogger.info("正在连接: {}:{}", inetHost, port);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    createBootstrap(group, inetHost, port);
                }
            }).start();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        openPoll();//开启轮询
    }

    public void createBootstrap(EventLoopGroup eventLoop, String inetHost, int port) {
        try {
            Bootstrap bootstrap = new Bootstrap();
            final NettyListener nettyListener = new NettyListener(this,inetHost,port);
            bootstrap.group(eventLoop)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    //.addLast(new StringEncoder(Charset.forName("GBK")))
                                    .addLast(new StringDecoder(Charset.forName("GBK")))
                                    .addLast(nettyListener);
                        }
                    });
            bootstrap.remoteAddress(inetHost, port);
            ChannelFuture channelFuture = bootstrap.connect().addListener(new ConnectionListener(this,inetHost,port));

            //把当前客户端的监听器加入 nettyListeners 集合中
            nettyListeners.put(port, nettyListener);
            channelFutures.put(port, channelFuture);
            channelFuture.channel().closeFuture();
        } catch (Exception e) {
            sendLogger.error(ExceptionUtils.getTrace(e));
        } finally {

        }
    }

    /**
     * 监听串口
     */
    public void openSerialPort() {
        ArrayList<String> portList = SerialTool.findPort();
        if (portList.isEmpty()) {
            scanLogger.info("未检测到可用端口");
        } else {
            try {
                SerialPort serialPort = SerialTool.openPort(
                        portList.get(0), baudrate);
                // 添加监听
                portListener.setPortListener(serialPort);

                SerialTool.addListener(serialPort, portListener);
                scanLogger.info("已监听串口 - {}:{}", portList.get(0), baudrate);
            } catch (SerialPortParameterFailure e) {
                //捕获异常并记录日志
                scanLogger.error(ExceptionUtils.getTrace(e));
            } catch (NotASerialPort e) {
                scanLogger.error(ExceptionUtils.getTrace(e));
            } catch (NoSuchPort e) {
                scanLogger.error(ExceptionUtils.getTrace(e));
            } catch (PortInUse e) {
                scanLogger.error(ExceptionUtils.getTrace(e));
            } catch (TooManyListeners e) {
                scanLogger.error(ExceptionUtils.getTrace(e));
            }
        }
    }
}
