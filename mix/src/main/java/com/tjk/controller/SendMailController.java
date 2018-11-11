package com.tjk.controller;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("mail")
public class SendMailController {

    //在spring中配置的邮件发送的bean
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @RequestMapping(value = "send", produces = "text/plain;charset=utf-8")
    public String sendMail() {
        //创建邮件对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        Properties prop = new Properties();
        String from;
        try {
            //从配置文件中拿到发件人邮箱地址
            prop.load(this.getClass().getResourceAsStream("/mail.properties"));
            from = prop.get("mail.smtp.username") + "";
            helper = new MimeMessageHelper(message, true);
            //发件人邮箱
            helper.setFrom(from);
            //收件人邮箱
            helper.setTo("779446297@qq.com");
            //邮件的主题
            helper.setSubject("Spring的邮件发送05");
            //邮件的文本内容，true表示文本以html格式打开
//            helper.setText("<p>这是使用spring的邮件功能发送的一封邮件</p><br/>" +
//                    "<a href='https://blog.csdn.net/Mr__Viking'>打开我的博客主页</a><br/>" +
//                    "<img src='cid:fengye'>", true);


            Map<String, Object> model = new HashMap<String, Object>();
            model.put("Name", "James");
            model.put("Text", "This is why we play");
            String emailText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "EmailTemplate.vm", "UTF-8", model);

            helper.setText(emailText, true);

            //在邮件中添加一张图片
            File file = new File("D:/img/枫叶.jpg");
            FileSystemResource resource = new FileSystemResource(file);
            //这里指定一个id,在上面引用
            helper.addInline("fengye", resource);
            //在邮件中添加一个附件
            helper.addAttachment("枫叶.jpg", resource);
            //发送邮件
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "发送成功";
    }
}

