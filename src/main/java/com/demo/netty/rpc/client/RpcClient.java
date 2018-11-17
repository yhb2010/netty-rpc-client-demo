package com.demo.netty.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.netty.rpc.codec.RpcDecoder;
import com.demo.netty.rpc.codec.RpcEncoder;
import com.demo.netty.rpc.domain.RpcRequest;
import com.demo.netty.rpc.domain.RpcResponse;

/**
 * RPC 客户端（用于发送 RPC 请求）
 *
 * @since 1.0.0
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private final String host;
    private final int port;
    private Channel channel;
    private RpcResponse response;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        try {
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void start() throws Exception {
    	EventLoopGroup group = new NioEventLoopGroup();
        // 创建并初始化 Netty 客户端 Bootstrap 对象
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new RpcEncoder(RpcRequest.class)); // 编码 RPC 请求
                pipeline.addLast(new RpcDecoder(RpcResponse.class)); // 解码 RPC 响应
                pipeline.addLast(RpcClient.this); // 处理 RPC 响应
            }
        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        // 连接 RPC 服务器
        ChannelFuture future = bootstrap.connect(host, port).sync();
        // 写入 RPC 请求数据并关闭连接
        channel = future.channel();
	}

	@Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        this.response = response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception", cause);
        ctx.close();
    }

    public RpcResponse send(RpcRequest request) throws Exception {
        channel.writeAndFlush(request).sync();
        channel.closeFuture().sync();
        // 返回 RPC 响应对象
        return response;
    }

}