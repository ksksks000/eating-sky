package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


//通用接口
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}", file);

        try {
            //先拿到原始文件名
            String originalFlieName = file.getOriginalFilename();
            //再从原始文件名上拿后缀
            String extension = originalFlieName.substring(originalFlieName.lastIndexOf("."));
            //再随机生成一个UUID转字符串并且把后缀拼接在一起
            String objectName = UUID.randomUUID().toString()+extension;
            //调用阿里云oss工具类上传修改名称之后的文件
            //文件对象转成数组getBytes()
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return  Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

        //整个try catch 就相当于是一个 if 语句
        //先执行try里的，OK没问题就return
        //有异常就catch抛出打印到程序控制台
        //但是如果返回给前端，那就得在下面也return
        //简而言之，try catch是给程序后端开饭看的，return是给前端看的
    }
}
