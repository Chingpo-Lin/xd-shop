package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.enums.BizCodeEnum;
import org.example.request.UserRegisterRequest;
import org.example.service.FileService;
import org.example.service.UserService;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

/**
 * <p>
 * user controller
 * </p>
 *
 * @author Bob
 * @since 2022-12-10
 */
@Api(tags = "user module")
@RestController
@RequestMapping("/api/user/v1")
public class UserController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    /**
     * upload user avatar
     *
     * default maximum 1MB
     *
     * @param file
     * @return
     */
    @ApiOperation("user avatar upload")
    @PostMapping("upload")
    public JsonData uploadUserImg(
            @ApiParam(value = "file upload", required = true)
            @RequestPart MultipartFile file) {
        String result = fileService.uploadUserImg(file);

        return result != null ? JsonData.buildSuccess(result) : JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }

    @ApiOperation("user register")
    @PostMapping("register")
    public JsonData register(@ApiParam("user register object") @RequestBody UserRegisterRequest registerRequest) {

        JsonData jsonData = userService.register(registerRequest);
        return jsonData;
    }
}

