package com.hatim.web;

import com.hatim.common.constant.Status;
import com.hatim.common.utils.Base64Image;
import com.hatim.service.QQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * Created by Hatim on 2017/4/22.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Value("${identifying.img.path}")
    private String identifying_img_path;

    @Autowired
    QQService smartQQService;

    @RequestMapping(value = "/qq", method = RequestMethod.GET)
    private String qqLogin(ModelMap map) {
        if (!Status.isWaittingLogin) {
            Status.imgUrl = identifying_img_path;
            smartQQService.startService();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        map.addAttribute("myTitle", "qq登录");
        map.addAttribute("imgStr", "data:image/png;base64," + Base64Image.GetImageStr(identifying_img_path));
        return "login";
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile(Long id)
            throws IOException {
        FileSystemResource file = new FileSystemResource(Status.imgUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }

    @RequestMapping(value = "/weChat", method = RequestMethod.GET)
    private String weChatLogin(ModelMap map) {
        map.addAttribute("title", "WeChat登录");
        return "login";
    }
}
