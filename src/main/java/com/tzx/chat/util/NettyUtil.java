package com.tzx.chat.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettyUtil {


    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");

    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> token) {
        return channel.attr(token).get();
    }

    // 新增：从通道中获取存储的token
    public static String getTokenFromChannel(Channel channel) {
        return channel.attr(NettyUtil.TOKEN).get();
    }
    
}