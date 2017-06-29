package listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import luncher.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by vaf71 on 2017/6/12.
 */
public class ConnectionListener implements ChannelFutureListener {
    final Logger logger = LoggerFactory.getLogger("SEND");
    private Startup client;
    private String inetHost;
    private int port;

    public ConnectionListener(Startup client,String inetHost, int port) {
        this.inetHost = inetHost;
        this.port = port;
        this.client = client;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {

            final EventLoop eventLoop = channelFuture.channel().eventLoop();
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    client.createBootstrap(eventLoop, inetHost, port);
                }
            }, 1L, TimeUnit.SECONDS);
        }
    }
}
