package com.admin.zk.二维码test;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Autor liurong
 * @Data 2022/8/11
 * @Description:
 */
public class QrCodeUtils {

    /**
     * 二维码尺寸
     */
    private static final int QRCODE_SIZE = 300;

    /**
     * LOGO宽度
     */
    private static final int LOGO_WIDTH = 80;
    /**
     * LOGO高度
     */
    private static final int LOGO_HEIGHT = 80;

    /**
     * 生成二维码
     *
     * @param url 二维码解析后的URL地址
     * @return 图片
     * @throws Exception
     */
    public static BufferedImage getQrLogoCode(String url) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>(8);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 设置黑白相见的颜色
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return image;
    }

    /**
     * 解析二维码
     *
     * @param inputStream 二维码图片流
     * @return
     * @throws Exception
     */
    public static String decodeQrImage(InputStream inputStream) throws Exception {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Map<DecodeHintType, Object> hints = new HashMap<>(2);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * 生成二维码
     *
     * @param url      二维码解析后的URL地址
     * @param logoPath logo地址 如果为空则表示不带logo
     * @return 图片
     * @throws Exception
     */
    public static BufferedImage getQrLogoCode(String url, String logoPath) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>(8);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 设置黑白相见的颜色
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (logoPath == null || "".equals(logoPath)) {
            return image;
        }
        // 插入图片
        QrCodeUtils.setLogoImage(image, logoPath);
        return image;
    }

    /**
     * 插入LOGO
     *
     * @param source   二维码图片
     * @param logoPath LOGO图片地址
     */
    private static void setLogoImage(BufferedImage source, String logoPath) throws Exception {
        File file = new File(logoPath);
        BufferedImage imageIo = ImageIO.read(file);
        int width = imageIo.getWidth(null);
        int height = imageIo.getHeight(null);

        // 设置图片尺寸，如果超过指定大小，则进行响应的缩小
        if (width > LOGO_WIDTH || height > LOGO_HEIGHT) {
            width = LOGO_WIDTH;
            height = LOGO_HEIGHT;
        }

        Image image = imageIo.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        // 重新绘制Image对象
        g.drawImage(image, 0, 0, null);
        g.dispose();

        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        // 设置为居中
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(imageIo, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    // 校验图片文件头的定义的
    // public static String imgType(InputStream inputStream) throws IOException {
    //     // 读取文件前几位
    //     byte[] fileHeader = new byte[4];
    //     int read = inputStream.read(fileHeader, 0, fileHeader.length);
    //     inputStream.close();
    //
    //     // 转为十六进制字符串
    //     String header = ByteUtil.bytes2Hex(fileHeader);
    //
    //     if (header.contains("FFD8FF")) {
    //         return "jpg";
    //     } else if (header.contains("89504E47")) {
    //         return "png";
    //     } else if (header.contains("47494638")) {
    //         return "gif";
    //     } else if (header.contains("424D")) {
    //         return "bmp";
    //     } else if (header.contains("52494646")) {
    //         return "webp";
    //     } else if (header.contains("49492A00")) {
    //         return "tif";
    //     } else {
    //         return "unknown";
    //     }
    //
    // }
    //
    // public static String bytes2Hex(byte[] bytes) {
    //     StringBuilder sb = new StringBuilder();
    //     for (byte b : bytes) {
    //         String hex = Integer.toHexString(b & 0xff);
    //         sb.append(hex.length() == 2 ? hex : ("0"+hex));
    //     }
    //     return sb.toString();
    // }


}