package listener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import luncher.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.CountFail;
import utils.CRC16Util;
import utils.ExceptionUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by FMY on 2017/1/11 0011.
 */
@ChannelHandler.Sharable
public class NettyListener extends ChannelInboundHandlerAdapter {
    final Logger logger = LoggerFactory.getLogger("SEND");
    private ChannelHandlerContext ctx = null;
    public volatile boolean timeOut = true;
    private String inetHost;
    private int port;

    private Startup client;
    public NettyListener(Startup client,String inetHost, int port) {
        this.inetHost = inetHost;
        this.port = port;
        this.client = client;
    }
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        logger.info("{} 端口断开连接,正在重连...",port);
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                client.createBootstrap(eventLoop, inetHost, port);
            }
        }, 5L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    /**
     * 发生异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 读取到数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        String str = (String) msg;
        logger.debug("netty 读取到数据: {}", str);
        //TODO
        if (str.substring(0, 1).equals("&") || 1 == 1) {
            timeOut = false;
        }
    }

    /**
     * 链路建立成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        logger.info("连接成功 - {}:{}",inetHost,port);
    }

    /**
     * 发送数据
     * @param str
     * @return 标志数据是否正确,1-未发送，2-发送成功，3-发生异常
     */
    public Integer sendMsg(String str) {
        try {
            if (ctx == null){
                return 1;
            }
            logger.info("发送给 {} 端口: {}",port,str);
            //转化成 16 进制
            byte[] bs = CRC16Util.HexString2Buf(str);

            timeOut = true;//重置超时标记
            for (int i = 1; i <= 3; i++) {
                ByteBuf byteBuf = Unpooled.copiedBuffer(bs);
                ctx.writeAndFlush(byteBuf);
                logger.debug("TCP 第 {} 次发送成功",i);
                for (int j = 0; j < 10; j++) {
                    Thread.sleep(1000);
                    if (timeOut == false){
                        timeOut = true;
                        logger.info("对方已反馈，发送成功");
                        return 2;
                    }
                }
            }
        }catch (Exception e){
            logger.error(ExceptionUtils.getTrace(e));
            return 3;
        }
        logger.info("对方未反馈，发送失败");
        logger.debug("失败次数:{}",++CountFail.failCount);
        return 1;
    }
}