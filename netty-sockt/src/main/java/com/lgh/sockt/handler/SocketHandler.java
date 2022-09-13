package com.lgh.sockt.handler;
import com.lgh.sockt.client.ClientManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author liuguanghu
 * @title: SocketHandler
 * @projectName eparking-microservice-nacos
 * @description: 处理器
 * @date 2022/9/6 14:50
 */
public class SocketHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 客户端发消息会触发
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("[" + this.getIP(ctx) + "]收到消息：" + msg);
        System.out.println("字节数："+msg.length());
        ClientManager.getInstance().handleMsg(this.getIP(ctx), "This is response");
    }

    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加channel信息
        ClientManager.getInstance().putChannel(this.getIP(ctx), ctx.channel());
        System.out.println("[" + this.getIP(ctx) + "]已连接。。。");
    }

    /**
     * 客户端断开连接会触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //删除失效的channel
        ClientManager.getInstance().removeChannel(getIP(ctx));
        ctx.close();
        System.out.println("[" + this.getIP(ctx) + "]已断开。。。");
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
        System.out.println("[" + this.getIP(ctx) + "]发生异常：" + t);
        ctx.close();
    }

    /**
     * 获取IP地址
     */
    private String getIP(ChannelHandlerContext ctx) {
        String socketString = ctx.channel().remoteAddress().toString();
        int index = socketString.indexOf(":");
        String ip = socketString.substring(1, index);
        return ip;
    }

}
