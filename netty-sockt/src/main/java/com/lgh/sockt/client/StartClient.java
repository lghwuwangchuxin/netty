package com.lgh.sockt.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author liuguanghu
 * @title: StartClient
 * @projectName eparking-microservice-nacos
 * @description: 客户端
 * @date 2022/9/6 14:52
 */
public class StartClient {

    //主机名/IP
    private String host;
    //端口号
    private int port;
    //标题
    private String title;

    public StartClient(String host, int port, String title) {
        this.host = host;
        this.port = port;
        this.title = title;
    }

    //启动方法
    public void start() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast("decoder", new StringDecoder());
                    channel.pipeline().addLast("encoder", new StringEncoder());
                    channel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            System.out.println("收到响应：" + msg);
                        }
                    });
                }
            });

            //建立连接
            ChannelFuture future = bootstrap.connect(host, port).sync();
            //发送消息
            future.channel().writeAndFlush(title);
            //等待服务监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    //测试代码
    public static void main(String[] args) {
        String msg="444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05";
        try {
            //epciot.eparking.top
            String host = "127.0.0.1";
            int port = 8768;
            new StartClient(host, port, msg).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
