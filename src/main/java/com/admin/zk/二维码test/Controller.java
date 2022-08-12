package com.admin.zk.二维码test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Autor liurong
 * @Data 2022/8/11
 * @Description:
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class Controller {
    /**
     * 生成二维码
     *
     * @param response
     */
    @GetMapping(value = "/qr")
    public void getQrCodeImage(HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream()) {
            String url = "用户名：liurong，密码：Fei88888888";
            BufferedImage image = QrCodeUtils.getQrLogoCode(url);
            //设置response
            response.setContentType("image/png");
            // 输出jpg格式图片
            ImageIO.write(image, "jpg", os);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("二维码生成失败!");
        }
    }

    /**
     * 解析二维码图片，返回字符串
     *
     * @param file
     */
    @PostMapping(value = "/decode")
    public String decodeQrImage(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            return QrCodeUtils.decodeQrImage(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("二维码解析失败!");
        }
    }

    /**
     * 生成带logo的二维码
     *
     * @param response
     */
    @GetMapping(value = "/qr/logo")
    public void getQrLogoCodeImage(HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream()) {
            String url = "傻逼早上好，美好得一天从犯傻开始";
            String logoPath = "C:\\Users\\admin\\Desktop\\liubin.jpg";
            // 生成二维码对象
            BufferedImage image = QrCodeUtils.getQrLogoCode(url, logoPath);
            //设置response
            response.setContentType("image/png");
            // 输出jpg格式图片
            ImageIO.write(image, "jpg", os);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("二维码生成失败!");
        }
    }


}
