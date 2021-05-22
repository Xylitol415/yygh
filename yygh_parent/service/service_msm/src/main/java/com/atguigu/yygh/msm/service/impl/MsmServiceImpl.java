package com.atguigu.yygh.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.utils.ConstantPropertiesUtils;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class MsmServiceImpl implements MsmService {

    // 发送手机验证码
    @Override
    public boolean send(String phone, String code) {
        // 判断手机号是否为空
        if(StringUtils.isEmpty(phone)) {
            return false;
        }

        // 整合阿里云短信服务
        // 设置相关参数
        DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesUtils.REGION_Id,
                        ConstantPropertiesUtils.ACCESS_KEY_ID,
                        ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "我的谷粒在线教育网站");
        //模板code
        request.putQueryParameter("TemplateCode", "SMS_180051135");
        //验证码  使用json格式   {"code":"123456"}
        Map<String,Object> param = new HashMap<>();
        param.put("code",code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));

        //调用方法进行短信发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean send(String phone, Map<String,Object> param) {
        // 判断手机号是否为空
        if(StringUtils.isEmpty(phone)) {
            return false;
        }

        // 整合阿里云短信服务
        // 设置相关参数
        DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesUtils.REGION_Id,
                        ConstantPropertiesUtils.ACCESS_KEY_ID,
                        ConstantPropertiesUtils.SECRECT);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //签名名称
        request.putQueryParameter("SignName", "我的谷粒在线教育网站");
        //模板code
        request.putQueryParameter("TemplateCode", "SMS_180051135");

        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));

        //调用方法进行短信发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }


    // 使用容联云发送短信验证码
    @Override
    public boolean sendByCloopen(String phone, String code) {
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8aaf0708790d65aa01791367d0e702ae";
        String accountToken = "314572aab96c4b479edfa81d6fc88223";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8aaf0708790d65aa01791367d1f602b5";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = phone;
        String templateId= "1";
        String[] datas = {code, "5"};
        //String subAppend="1234";  //可选 扩展码，四位数字 0~9999
        //String reqId="atguigu";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
            return true;
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            return false;
        }
    }

    // mq发送预约通知短信
    private boolean sendByCloopen(String phone, Map<String,Object> param) {
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "your_accountSId";
        String accountToken = "your_accountToken";
        //请使用管理控制台中已创建应用的APPID
        String appId = "your_appId";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = phone;
        String templateId= "1";
        String[] datas = {(String)param.get("param"), "5"};
        //String subAppend="1234";  //可选 扩展码，四位数字 0~9999
        //String reqId="atguigu";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
            return true;
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            return false;
        }
    }

    // mq发送短信封装：阿里云短信服务
    /*@Override
    public boolean send(MsmVo msmVo) {
        if(!StringUtils.isEmpty(msmVo.getPhone())) {
            return this.send(msmVo.getPhone(), msmVo.getParam());
        }
        return false;
    }*/

    // mq发送短信封装：容联云短信服务
    @Override
    public boolean send(MsmVo msmVo) {
        if(!StringUtils.isEmpty(msmVo.getPhone())) {
            //String code = (String)msmVo.getParam().get("code");
            return this.sendByCloopen(msmVo.getPhone(), msmVo.getParam());
        }
        return false;
    }
}
