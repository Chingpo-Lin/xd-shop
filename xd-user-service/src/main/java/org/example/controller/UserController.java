package org.example.controller;


import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

}

