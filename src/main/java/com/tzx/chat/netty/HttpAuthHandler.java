package com.tzx.chat.netty;
import cn.hutool.extra.spring.SpringUtil;
import com.tzx.chat.component.RedisComponent;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.util.NettyUtil;
import com.tzx.chat.websocket.WebSocketService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class HttpAuthHandler extends ChannelInboundHandlerAdapter {

    private final RedisComponent redisComponent;

    // 通过构造函数注入RedisComponent
    public HttpAuthHandler(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }
    /**
     * ws连接进行前校验
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 只处理HTTP请求（WebSocket握手前的HTTP Upgrade请求）
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            URI uri = new URI(request.uri());
            String token = getTokenFromUri(uri);
            LoginUserVO loginUserVO = redisComponent.getTokenUserIndo(token);
            if (loginUserVO == null){
                sendErrorResponse(ctx, HttpResponseStatus.UNAUTHORIZED, "无效的token");
                return;
            }
            //redisComponent.setUserIdToToken(loginUserVO.getId(), token);
            //把token存入到通道中
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
            log.info("token验证成功，继续握手流程");
            ctx.fireChannelRead(msg);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    // 从URI中提取token参数
    private String getTokenFromUri(URI uri) {
        String query = uri.getQuery();
        if (query == null) {
            log.warn("请求URL中没有查询参数");
            return null;
        }

        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        String token = params.get("token");
        if (token == null) {
            log.warn("查询参数中没有token");
        }
        return token;
    }

    // 发送错误响应
    private void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
        response.headers().set("Content-Type", "text/plain; charset=UTF-8");
        response.headers().set("Connection", "close"); // 明确关闭连接
        log.info("发送响应: {} - {}", status.code(), message);
        // 发送响应并关闭连接
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
