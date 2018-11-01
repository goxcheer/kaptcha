package com.qgx.kaptchademo.controller;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 *@Author: Goxcheer
 *@Date:8:34 2018/11/1
 *@Email:604721660@qq.com
 *@decription: 验证码Controller
 */
@Controller
public class KaptchaController {

    @Autowired
    DefaultKaptcha defaultKaptcha;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @RequestMapping("/defaultKaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
        byte[] captchaChallengeAsJpeg = null;
        try (ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream()) {
            try {
                //生产验证码字符串并保存到session中
                String createText = defaultKaptcha.createText();
                httpServletRequest.getSession().setAttribute("vrifyCode", createText);
                //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
                BufferedImage challenge = defaultKaptcha.createImage(createText);
                ImageIO.write(challenge, "jpg", jpegOutputStream);
            } catch (IllegalArgumentException e) {
                httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
            captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        }
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream =
                httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    @PostMapping("/imgvrifyControllerDefaultKaptcha")
    public String vrity(HttpServletRequest request,String vrifyCode){
        String Session_vrityCode =  (String)request.getSession().getAttribute("vrifyCode");
        String msg = null;
        if (vrifyCode.equalsIgnoreCase(Session_vrityCode)) {
            msg = "success";
        } else {
            msg = "false";
        }
        request.setAttribute("msg",msg);
        return "index";
    }
}
