package pers.lish.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * 接受、处理，响应客户端webSocket请求的核心业务处理类
 * create by lishengbo on 2018-03-06 16:15
 */
public class MyWebSocketHandler extends SimpleChannelInboundHandler<Object>{


    private WebSocketServerHandshaker webSocketServerHandshaker;
    private static final String WEB_SOCKET_URL="ws://localhost:8888/WebSocket";

    /**
     * 服务端处理客户端websocket请求时的核心方法
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

        //处理客户端向服务端发起http握手请求的业务
        if(o instanceof FullHttpRequest){
            handHttpRequest(channelHandlerContext,(FullHttpRequest) o);
        //处理webSocket连接业务
        }else if(o instanceof WebSocketFrame){
            handWebsocketFrame(channelHandlerContext,(WebSocketFrame) o);
        }

    }

    /**
     * 处理客户端与服务端之间的业务
     * @param ctx
     * @param frame
     */
    private void handWebsocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        //判断是否是关闭websocket 的指令
        if(frame instanceof CloseWebSocketFrame){
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        }
        //判断是否是ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //判断是否是二进制消息，如果是抛出异常
        if(!(frame instanceof  TextWebSocketFrame)){
            System.out.println("目前不支持二进制消息");
            throw new RuntimeException("【"+this.getClass().getName()+"】-不支持消息");
        }
        //返回应答消息
        //获取客户端向服务端发送的消息
        String request=((TextWebSocketFrame) frame).text();
        System.out.println("服务端收到客户端消息====="+request);
        TextWebSocketFrame tws=new TextWebSocketFrame(new Date().toString()+ctx.channel().id()+"===>>>"+request);

        //群发：服务端向每个连接上来的客户端群发消息
        NettyConfig.group.writeAndFlush(tws);
    }


    /**
     * 处理客户端向服务端发起http握手请求的业务
     * @param ctx
     * @param req
     */
    private void handHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req){

        if(!req.getDecoderResult().isSuccess()||!("websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory=new WebSocketServerHandshakerFactory(WEB_SOCKET_URL,null,false);
        webSocketServerHandshaker= wsFactory.newHandshaker(req);
        if(webSocketServerHandshaker==null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }else{
            webSocketServerHandshaker.handshake(ctx.channel(),req);
        }





    }

    /**
     * 服务端向客户端响应消息
     * @param ctx
     * @param req
     * @param res
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res){

        if(res.getStatus().code()!=200){
            ByteBuf buf= Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        //服务端向客户端发送数据
        ChannelFuture f=ctx.channel().writeAndFlush(res);
        if(res.getStatus().code()!=200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }






    /**
     * 工程出现异常时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       cause.printStackTrace();
       ctx.close();
    }

    /**
     * 客户端与服务端创建连接的时候调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyConfig.group.add(ctx.channel());
        System.out.println("客户端与服务端连接开启--");
    }

    /**
     * 客户端与服务端断开连接的时候调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyConfig.group.remove(ctx.channel());
        System.out.println("客户端与服务端连接关闭--");
    }

    /**
     * 服务端接收客户端发送过来的数据结束之后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       ctx.flush();
    }
}
