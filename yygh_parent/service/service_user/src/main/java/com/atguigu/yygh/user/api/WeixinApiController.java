package com.atguigu.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.JwtHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.ConstantWxPropertiesUtils;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.sun.deploy.net.URLEncoder;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "微信扫码登录管理")
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;

    // 1.生成微信扫描二维码
    // 返回生成二维码需要的参数
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result getLoginParam(HttpSession session) throws UnsupportedEncodingException {

        String redirectUri = URLEncoder.encode(ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis() + "");
        return Result.ok(map);

        /*try {
            Map<String, Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            map.put("scope", "snsapi_login");
            String wxOpenRedirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            map.put("redirect_uri", wxOpenRedirectUrl);
            map.put("state", System.currentTimeMillis()+""); //System.currentTimeMillis()+""
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }*/
    }

    // 2.回调方法，得到扫描人信息
    @GetMapping("callback")
    public String callback(String code, String status) {
        // 第一步 获取临时票据 code
        System.out.println("code:" + code);

        // 第二步 拿着code和微信id和秘钥，请求微信固定地址，得到两个值
        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);

        // 使用httpclient请求这个地址
        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accessTokenInfo:" + accessTokenInfo);
            // 从返回的字符串中获取两个值 openid 和 access_token
            JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
            if(jsonObject.getString("errcode") != null){
                //log.error("获取access_token失败：" + jsonObject.getString("errcode") + resultJson.getString("errmsg"));
                throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
            }

            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            // 判断数据库中是否存在微信的扫描人信息
            // 根据openid判断
            UserInfo userInfo = userInfoService.selectWxInfoByOpenid(openid);
            if(userInfo == null) { // 数据库中不存在微信信息
                // 第三步 拿着access_token 和 openid 请求微信地址，得到扫描人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);

                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println(resultInfo);

                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                if(resultUserInfoJson.getString("errcode") != null){
                    //log.error("获取用户信息失败：" + resultUserInfoJson.getString("errcode") + resultUserInfoJson.getString("errmsg"));
                    throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
                }

                //解析用户信息
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");


                // 获取扫描人信息添加到数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }

            // 返回name和token字符串
            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            // 判断userInfo中是否有手机号，如果手机号为空，返回openid
            // 如果手机号不为空，返回openid值是空字符串
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);

            // 扫码登录成功后，使用redirect跳转到http://localhost:3000/weixin/callback?token=xxx&openid=xxx&name=xxx
            return "redirect:" + ConstantWxPropertiesUtils.YYGH_BASE_URL + "/weixin/callback?token="+map.get("token")+"&openid="+map.get("openid")+"&name="+URLEncoder.encode((String)map.get("name"), "utf-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
