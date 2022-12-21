package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.enums.BizCodeEnum;
import org.example.mapper.UserMapper;
import org.example.model.UserDO;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "test controller")
@RestController
@RequestMapping("/api/test/v1")
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @ApiOperation("get all user")
    @GetMapping("user")
    public JsonData uploadUserImg() {
        List<UserDO> userDOList = userMapper.selectList(new QueryWrapper<UserDO>());
        return JsonData.buildSuccess(userDOList);
    }
}
