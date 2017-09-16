/*
 * Copyright (c) 2015, www.jd.com. All rights reserved.
 *
 * 警告：本计算机程序受著作权法和国际公约的保护，未经授权擅自复制或散布本程序的部分或全部、以及其他
 * 任何侵害著作权人权益的行为，将承受严厉的民事和刑事处罚，对已知的违反者将给予法律范围内的全面制裁。
 */

package com.rebate.service.wx;

import com.rebate.domain.wx.ApiAccessToken;
import com.rebate.domain.wx.AuthorizationCodeInfo;
import com.rebate.domain.wx.WxUserInfo;

/**
 * Description:
 *
 * @author zhouyu7
 * @version 1.0.0
 */
/*
 * =========================== 维护日志 ===========================
 * 2016-08-03 09:47  zhouyu7 新建代码 
 * =========================== 维护日志 ===========================
 */
public interface WxAccessTokenService {

    /**
     * 获取登录accessToken
     * @param loginCode 登录授权返回的code
     * @return
     */
     AuthorizationCodeInfo getLoginAccessToken(String loginCode);

    /**
     * 获取票据
     * @return
     */
    String getTicket();

    /**
     * 获取API 访问token
     * @return
     */
    ApiAccessToken getApiAccessToken();

    /**
     * 登录授权code
     * @param accessToken
     * @param openId
     * @return
     */
    WxUserInfo getWxUserInfo(String accessToken,String openId) ;

} 