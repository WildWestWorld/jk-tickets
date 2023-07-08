package com.jktickets.controller;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.${domain}.${Domain}QueryReq;
import com.jktickets.req.${domain}.${Domain}SaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.${domain}.${Domain}QueryRes;
import com.jktickets.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${domain}")
public class ${Domain}Controller {
    @Resource
    ${Domain}Service ${domain}Service;

    @PostMapping("/save")
    public CommonRes<Object> save${Domain}(@Valid @RequestBody ${Domain}SaveReq req) {

        ${domain}Service.save${Domain}(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加乘客成功");
       }else{
           return new CommonRes<>("编辑乘客成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<${Domain}QueryRes>> query${Domain}List(@Valid ${Domain}QueryReq req) {

        //       获取当前用户的MemberID
        req.setMemberId(LoginMemberContext.getId());
        PageRes<${Domain}QueryRes> ${domain}QueryResList = ${domain}Service.query${Domain}List(req);
        return new CommonRes<>(${domain}QueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            ${domain}Service.deleteById(id);
        return new CommonRes<>("删除乘客成功");

    }

}
