package com.tzx.chat.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tzx.chat.annotation.AuthCheck;
import com.tzx.chat.common.BaseResponse;
import com.tzx.chat.common.ResultUtils;
import com.tzx.chat.config.AppConfig;
import com.tzx.chat.constants.Constants;
import com.tzx.chat.entiy.enums.DateTimePatternEnum;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.util.StringTools;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private AppConfig appConfig;


    /**
     * 上传文件
     * @param file
     * @return
     */
    @AuthCheck
    @PostMapping("/uploadImage")
    public BaseResponse<String> uploadImage(MultipartFile file) {
        try {
            //构建文件夹
            String day = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
            String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_AVATAR + day;
            File folderFile = new File(folder);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            //构建文件名
            String fileName = file.getOriginalFilename();
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            String realFIleName = StringTools.getRandomString(Constants.LENGTH_30) + fileSuffix;
            String filePath = folder + File.separator + realFIleName;
            file.transferTo(new File(filePath));
            return ResultUtils.success(filePath);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传文件失败");
        }
    }


}
