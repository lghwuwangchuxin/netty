package com.lgh.sockt.server;
import com.lgh.sockt.handler.SocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @author liuguanghu
 * @title: StartServer
 * @projectName eparking-microservice-nacos
 * @description: 服务端
 * @date 2022/9/6 14:47
 */
public class StartServer {

    //端口号
    private int port;

    public StartServer(int port) {
        this.port = port;
    }

    //启动方法
    public void start() throws Exception {
        //负责接收客户端的连接的线程。线程数设置为1即可，netty处理链接事件默认为单线程，过度设置反而浪费cpu资源
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //负责处理数据传输的工作线程。线程数默认为CPU核心数乘以2
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            //在ServerChannelInitializer中初始化ChannelPipeline责任链，并添加到serverBootstrap中
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) {
                    //添加编解码
                    channel.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                    channel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                    channel.pipeline().addLast("socketHandler", new SocketHandler());
                }
            });
            //标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //是否启用心跳保活机制
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            //绑定端口后，开启监听
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            //释放资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    //测试代码
    public static void main(String[] args) {
        try {
            int port = 8768;
            new StartServer(port).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
