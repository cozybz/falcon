package io.falcon.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.junit.Test;

import java.net.SocketAddress;

/**
 * Created by cozybz@gmail.com on 2015/4/25.
 * HttpTest
 */
public class HttpTest {
    private static final byte[] CONTENT = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};

    @Test
    public void runServer() {
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(masterGroup, workerGroup).channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("channelWritabilityChanged");
                        super.channelWritabilityChanged(ctx);
                    }

                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        System.out.println("userEventTriggered");
                        super.userEventTriggered(ctx, evt);
                    }

                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("handlerRemoved");
                        super.handlerRemoved(ctx);
                    }

                    @Override
                    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("handlerAdded");
                        super.handlerAdded(ctx);
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("channelActive");
                        super.channelActive(ctx);
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("channelInactive");
                        super.channelInactive(ctx);
                    }

                    @Override
                    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                        System.out.println("deregister");
                        super.deregister(ctx, promise);
                    }

                    @Override
                    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                        System.out.println("close");
                        super.close(ctx, promise);
                    }

                    @Override
                    public void flush(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("flush");
                        super.flush(ctx);
                    }

                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        System.out.println("write");
                        super.write(ctx, msg, promise);
                    }

                    @Override
                    public void read(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("read");
                        super.read(ctx);
                    }

                    @Override
                    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
                        System.out.println("connect");
                        super.connect(ctx, remoteAddress, localAddress, promise);
                    }

                    @Override
                    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                        System.out.println("disconnect");
                        super.disconnect(ctx, promise);
                    }

                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("channelRegistered");

                        super.channelRegistered(ctx);
                    }

                    @Override
                    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("channelUnregistered");

                        super.channelUnregistered(ctx);
                    }

                    @Override
                    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
                        System.out.println("bind");
                        super.bind(ctx, localAddress, promise);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("channelRead");
                        if (msg instanceof HttpRequest) {
                            HttpRequest req = (HttpRequest) msg;
                            if (HttpHeaderUtil.is100ContinueExpected(req)) {
                                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
                            }
                            boolean keepAlive = HttpHeaderUtil.isKeepAlive(req);
                            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(CONTENT));
                            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

                            if (!keepAlive) {
                                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                            } else {
                                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                                ctx.write(response);
                            }
                        }
                    }

                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("channelReadComplete");
                        ctx.flush();
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        cause.printStackTrace();
                        ctx.close();
                    }
                });
            }
        });
        bootstrap.option(ChannelOption.SO_BACKLOG, 1);
        //bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
        try {
            bootstrap.bind(8080).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
        }
    }

}
