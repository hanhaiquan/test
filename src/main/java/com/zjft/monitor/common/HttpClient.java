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

        workerGroup = new NioEventLoopGroup();//������Ϣ�����߳�
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);//����ͨѶ��ʱʱ��
        bootstrap.remoteAddress(new InetSocketAddress(host, port));

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // �ͻ��˽��յ�����httpResponse��Ӧ������Ҫʹ��HttpResponseDecoder���н���
                ch.pipeline().addLast(new HttpResponseDecoder());
                // �ͻ��˷��͵���httprequest������Ҫʹ��HttpRequestEncoder���б���
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
            System.out.println("���ӷ������ɹ�");
            Connection conn = DBUtil.getConnection();
            String sql = "SELECT PLATE_NO,STATUS FROM LOGISTICS_CAR_INFO WHERE STATUS='1' AND PLATE_NO LIKE '��%'";
            CachedRowSet rowset = DBUtil.executeQuery(sql, conn, log);

            ExecutorService threadPool = Executors.newFixedThreadPool(3);

            log.info("============����׼����ʼ�볡...==============");
            while (rowset.next()) {
                String plateNo = rowset.getString("PLATE_NO");
                MoveCarThread moveCarThread = new MoveCarThread(plateNo, f.channel(),conn);

                Thread.sleep(15000);//����10�������һ������
                threadPool.submit(moveCarThread);
            }

            threadPool.shutdown();//�̳߳ز����������˳���ֱ����ӵ��̳߳��е������Ѿ�������ɲ��˳�
            rowset.close();

            while (true) {
                if (threadPool.isTerminated()) {
                    log.info("���г������ӽ�����");
                    String update = "UPDATE LOGISTICS_CAR_INFO SET STATUS='1' WHERE PLATE_NO LIKE '��%'";
                    DBUtil.executeCUID(update, conn, log);
                    log.info("����������س���״̬Ϊ[δǩ��]");
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
