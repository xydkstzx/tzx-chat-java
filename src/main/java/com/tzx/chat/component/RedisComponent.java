package com.tzx.chat.component;

import com.tzx.chat.constants.Constants;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.vo.message.MessageVO;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.redis.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;


    public void saveLoginUserVO(LoginUserVO loginUserVO) {
        redisUtils.setex(Constants.REDIS_KEY_WS_USERID + loginUserVO.getId(),loginUserVO.getToken(),Constants.REDIS_KEY_EXPIRES_DAY * 7);
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN + loginUserVO.getToken(),loginUserVO,Constants.REDIS_KEY_EXPIRES_DAY * 7);
    }

    public void cleanTokenInfo(String token) {
        if (!StringUtils.isEmpty(token)) {
                redisUtils.delete(Constants.REDIS_KEY_WS_TOKEN + token);
        }
    }
    public void cleanTokenUserId(String userId){
        redisUtils.delete(Constants.REDIS_KEY_WS_USERID + userId);

    }

    public LoginUserVO getTokenUserIndo(String token) {
        LoginUserVO loginUserVO = (LoginUserVO) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN + token);
        return loginUserVO;

    }

    public String getUserIdLatestToken(String userId) {
        String key = Constants.REDIS_KEY_WS_USERID + userId;
        return (String) redisUtils.get(key);
    }

    public void setUserIdToToken(String id, String token) {
        // 假设token有效期为24小时，映射关系有效期应与token一致
        redisUtils.setex(Constants.USER_ID_TO_TOKEN_KEY + id,token,Constants.REDIS_KEY_EXPIRES_DAY * 7);
    }

    // 根据用户ID查询旧token
    public String getTokenByUserId(String id) {
        String lodToken = (String) redisUtils.get(Constants.REDIS_KEY_WS_USERID + id);
        return lodToken;

    }

    /**
     * 存储离线消息到redis
     * @param targetUserId
     * @param messageVO
     */
    public void saveOfflineMessage(String targetUserId, MessageVO messageVO) {
        String offlineKey = Constants.REDIS_KEY_OFFLINE_MES + targetUserId;
        redisUtils.set(offlineKey,messageVO);
    }

    public MessageVO getOfflineMessage(String userId) {
        return (MessageVO) redisUtils.get(Constants.REDIS_KEY_OFFLINE_MES + userId);
    }

    public void clearOfflineMessage(String userId) {
        redisUtils.delete(Constants.REDIS_KEY_OFFLINE_MES + userId);
    }
}
