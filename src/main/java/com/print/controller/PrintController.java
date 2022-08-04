package com.print.controller;


import com.alibaba.fastjson.JSONObject;
import com.print.utils.utils.PrintUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <Description> <br>
 *
 * @author hanqy<br>
 * @version 1.0<br>
 * @CreateDate 2019-05-29 13:26<br>
 */
@RestController
@RequestMapping("/print")
@CrossOrigin
public class PrintController {

    @CrossOrigin(allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET,
            RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS,
            RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.PATCH}, origins = "*")
    @GetMapping("test")
    public String test(HttpServletRequest requeste) {
        JSONObject result = new JSONObject();
        result.put("msg", "success");
        return result.toJSONString();
    }

    @CrossOrigin(allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET,
            RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS,
            RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.PATCH}, origins = "*")
    @PostMapping("print")
    public String print(@RequestBody String jsonData, HttpServletRequest requeste) {
        PrintUtil.print(jsonData);
        JSONObject result = new JSONObject();
        result.put("msg", "success");
        return result.toJSONString();
    }
}
