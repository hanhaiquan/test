package com.zjft.monitor.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.rowset.CachedRowSet;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hqhan on 2018/12/10.
 */
public class HttpClient {

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private Channel channel;

    private static Log log = LogFactory.getLog(HttpClient.class);

    public void init(String host, int port) throws Exception {

        workerGroup = new NioEventLoopGroup();//创建消息发送线程
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);//设置通讯超时时间
        bootstrap.remoteAddress(new InetSocketAddress(host, port));

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                ch.pipeline().addLast(new HttpResponseDecoder());
                // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                ch.pipeline().addLast(new HttpRequestEncoder());
                ch.pipeline().addLast(new HttpClientInboundHandler());
            }
        });
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public static void main(String[] args) throws Exception {

        HttpClient httpClient = new HttpClient();

        try {
            httpClient.init("127.0.0.1", 8070);
            ChannelFuture f = httpClient.getBootstrap().connect().sync();
            System.out.println("连接服务器成功");
            Connection conn = DBUtil.getConnection();
            String sql = "SELECT PLATE_NO,STATUS FROM LOGISTICS_CAR_INFO WHERE STATUS='1' AND PLATE_NO LIKE '苏%'";
            CachedRowSet rowset = DBUtil.executeQuery(sql, conn, log);

            ExecutorService threadPool = Executors.newFixedThreadPool(3);

            log.info("============车辆准备开始入场...==============");
            while (rowset.next()) {
                String plateNo = rowset.getString("PLATE_NO");
                MoveCarThread moveCarThread = new MoveCarThread(plateNo, f.channel(),conn);

                Thread.sleep(15000);//休眠10秒调度下一辆车辆
                threadPool.submit(moveCarThread);
            }

            threadPool.shutdown();//线程池并不会立刻退出，直到添加到线程池中的任务都已经处理完成才退出
            rowset.close();

            while (true) {
                if (threadPool.isTerminated()) {
                    log.info("所有车辆交接结束！");
                    String update = "UPDATE LOGISTICS_CAR_INFO SET STATUS='1' WHERE PLATE_NO LIKE '苏%'";
                    DBUtil.executeCUID(update, conn, log);
                    log.info("重置所有相关车辆状态为[未签到]");
                    break;
                }
                Thread.sleep(1000);
            }
            DBUtil.closeConnection(conn);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getWorkerGroup().shutdownGracefully();
        }

    }


}
