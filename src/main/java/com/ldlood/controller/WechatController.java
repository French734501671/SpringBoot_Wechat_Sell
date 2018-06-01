package com.ldlood.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.ldlood.VO.ResultVO;
import com.ldlood.config.ProjectUrlConfig;
import com.ldlood.constant.RedisConstant;
import com.ldlood.utils.CheckUtil;
import com.ldlood.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    @Autowired
    private WxMpService wxMpService;
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;
    
    @GetMapping(value = "/hello")
    @ResponseBody
    public String hello() {
        return "hello world!";
    }
    
    
    @GetMapping(value = "/check")
    @ResponseBody
    public void check(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("---token验证---");
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");

            PrintWriter out = response.getWriter();
            if(CheckUtil.checkSignature(signature, timestamp, nonce)){
                log.info("---token验证成功---");
                out.print(echostr);
                out.flush();
            }else {
                log.info("---token验证失败---");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @GetMapping("/authorize")
    @ResponseBody
    public ResultVO authorize(@RequestParam("returnUrl")String returnUrl) {
        // 此处加了项目名-配置文件-server.context-path
        String url = projectUrlConfig.getWechatMpAuthorize() + "/sell/wechat/auth";
        String state = "STATE";
        try {
            state = URLEncoder.encode(returnUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO, state);
        log.info("【微信网页授权 获取code】,result={}", redirectUrl);
        
        return ResultVOUtil.success(redirectUrl);
    }

    
    @GetMapping("/auth")
    public String auth(@RequestParam("code") String code, @RequestParam("state") String returnUrl)
            throws WxErrorException {
        log.info("code={}", code);
        WxMpOAuth2AccessToken oauth2getAccessToken = wxMpService.oauth2getAccessToken(code);
        String accessToken = oauth2getAccessToken.getAccessToken();
        log.info("【微信网页授权 auth】,accessToken={}", accessToken);
        WxMpUser oauth2getUserInfo = wxMpService.oauth2getUserInfo(oauth2getAccessToken, "zh_CN");
        String openId = "";
        if (null != oauth2getUserInfo) {
            openId = oauth2getUserInfo.getOpenId();
        }
        log.info("【微信网页授权成功】,openId={}", openId);

        Gson gson = new Gson();
        String json = gson.toJson(oauth2getUserInfo);
        redisTemplate.opsForHash().put(RedisConstant.WECHAT_USER, openId, json);

        return "redirect:" + returnUrl + "?openid=" + openId;
    }
    
    
    
   /* @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) {

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException ex) {
            log.error("【微信网页授权】{}", ex);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(), ex.getError().getErrorMsg());
        }

        String openId = wxMpOAuth2AccessToken.getOpenId();
        return "redirect:" + returnUrl + "?openid=" + openId;
    }*/


    /*@GetMapping("/qrauthorize")
    public String qrAuthorize(@RequestParam("returnUrl") String returnUrl) {
        //1 配置
        String url = projectUrlConfig.getWechatOpenAuthorize() + "/wechat/qruserInfo";
        String redirectUrl = wxOpenService.buildQrConnectUrl(url, WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, URLEncoder.encode(returnUrl));
        log.info("[微信网页登陆获取 code],resule={}", redirectUrl);

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/qruserInfo")
    public String qrUserInfo(@RequestParam("code") String code,
                             @RequestParam("state") String returnUrl) {

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxOpenService.oauth2getAccessToken(code);
        } catch (WxErrorException ex) {
            log.error("【微信网页登陆】{}", ex);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(), ex.getError().getErrorMsg());
        }

        String openId = wxMpOAuth2AccessToken.getOpenId();
        log.info("opiedId: " + openId);
        return "redirect:" + returnUrl + "?openid=" + openId;
    }*/
}
