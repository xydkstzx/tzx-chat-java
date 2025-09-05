package com.tzx.chat.websocket;

import cn.hutool.json.JSONUtil;
import com.tzx.chat.component.RedisComponent;
import com.tzx.chat.constants.Constants;
import com.tzx.chat.entiy.enums.MessageCode;
import com.tzx.chat.entiy.vo.message.MessageVO;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.util.JwtUtil;
import com.tzx.chat.util.NettyUtil;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Service
public class WebSocketService {
    @Resource
    private RedisComponent redisComponent;





    @Data
    public static class WsContent {
        private String type;
        private Object content;
    }


    public static final ConcurrentHashMap<String, Channel> Online_User = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Channel, String> Online_Channel = new ConcurrentHashMap<>();

    /**
     * 第一次建立映射成功后，存入的老token，当新设备登录时，提前把redis中的老token覆盖了，导致我拿到的都是新token
     * @param channel
     * @param token
     */
    public void online(Channel channel, String token) {

        Claims claims = JwtUtil.parseToken(token);
        String userId = (String) claims.get("id");
        //双向绑定
        Online_User.put(userId, channel);
        Online_Channel.put(channel, userId);
        log.info("建立用户通道映射成功online:{}", userId);

        MessageVO messageVO = redisComponent.getOfflineMessage(userId);
        if (messageVO != null) {
            //推送redis中的未读信息给登录用户
            sendToUser(userId,messageVO);
            //推送完成后清理redis中的数据
            redisComponent.clearOfflineMessage(userId);
        }


    }
    /**
     * 发送消息
     * @param channel
     * @param msg
     * @param type
     */
    private void sendMsg(Channel channel, Object msg, String type) {
        WsContent wsContent = new WsContent();
        wsContent.setType(type);
        wsContent.setContent(msg);
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsContent)));
        log.info("发送好友通知成功");
    }
    public void closeOldConnection(String userId, Object message) {
        // 从在线映射中获取旧连接
        String oldToken = redisComponent.getTokenByUserId(userId);
        if (oldToken != null) {
            try {
                redisComponent.cleanTokenInfo(oldToken);
                log.info("用户[{}]的老token[{}]已从Redis中清除", userId, oldToken);
                Channel oldChannel = Online_User.get(userId);
                if (oldChannel != null) {
                    // 发送被挤下线的消息
                    sendMsg(oldChannel, message, Constants.MSG);
                    //关闭旧连接
                    oldChannel.close().addListener(future -> {
                        if (future.isSuccess()) {
                            log.info("用户[{}]的旧连接已被新登录挤下线", userId);
                            // 清理映射关系
                            Online_User.remove(userId);
                            Online_Channel.remove(oldChannel);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("关闭用户[{}]的旧连接失败", userId, e);
            }
        }
    }

    /**
     * 发送好友请求通知
     * @param msg
     * @param friendsId
     */
    public void sendFriendsMsg(Object msg,String friendsId) {
        //获取接收人的连接通道
        Channel channel = Online_User.get(friendsId);
        if (channel != null) {
            sendMsg(channel, msg, Constants.ADD_FRIEND);
        }
    }

    /**
     * 发送默认的会话信息
     * @param msg
     * @param userId
     */
    public void sendUserDefaultMsg(Object msg, String userId) {
        //获取接收人的连接通道
        Channel channel = Online_User.get(userId);
        if (channel != null){
            sendMsg(channel, msg, Constants.DEFAULT_USER);
        }
    }



    /**
     * 给指定用户发送消息
     * @param msg
     * @param userId
     * @param targetId
     */
    public boolean sendMsgToUser(Object msg, String userId, String targetId) {
        //发送信息给自己
        sendToUser(userId, (MessageVO) msg);
        log.info("发送给自己成功");
        //发送信息给指定用户
        Channel channel = Online_User.get(targetId);
        if (channel != null) {
            try {
                sendMsg(channel, msg, Constants.PRIVATE_CHEAT_MESSAGE);
                log.info("发送给指定对象成功");
                return true;
            }catch (Exception e) {
                log.error("发送消息给用户[{}]失败", targetId, e);
            }

        }
        return false;
    }
    /**
     * 发送信息给自己
     */
    private void sendToUser(String userId, MessageVO msg) {
        Channel channel = Online_User.get(userId);
        if (channel != null && channel.isActive()) {
            try {
                sendMsg(channel, msg, Constants.PRIVATE_CHEAT_MESSAGE);
            } catch (Exception e) {
                log.error("发送消息给用户[{}]失败", userId, e);
            }
        }
    }

}
