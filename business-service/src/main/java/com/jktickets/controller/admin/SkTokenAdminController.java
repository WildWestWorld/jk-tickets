package com.jktickets.controller.admin;


import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.jktickets.context.LoginMemberContext;
import com.jktickets.req.skToken.SkTokenQueryReq;
import com.jktickets.req.skToken.SkTokenSaveReq;
import com.jktickets.res.CommonRes;
import com.jktickets.res.PageRes;
import com.jktickets.res.skToken.SkTokenQueryRes;
import com.jktickets.service.SkTokenService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/skToken")
public class SkTokenAdminController {
    @Resource
    SkTokenService skTokenService;






    @PostMapping("/save")
    public CommonRes<Object> saveSkToken(@Valid @RequestBody SkTokenSaveReq req) {

        skTokenService.saveSkToken(req);
       if(ObjectUtil.isNull(req.getId())){
           return new CommonRes<>("添加SkToken成功");
       }else{
           return new CommonRes<>("编辑SkToken成功");
       }
    }

    @GetMapping("/queryList")
    public CommonRes<PageRes<SkTokenQueryRes>> querySkTokenList(@Valid SkTokenQueryReq req) {

        //       获取当前用户的MemberID
        //req.setMemberId(LoginMemberContext.getId());
        PageRes<SkTokenQueryRes> skTokenQueryResList = skTokenService.querySkTokenList(req);
        return new CommonRes<>(skTokenQueryResList);
    }


    @DeleteMapping("/delete/{id}")
    public CommonRes<Object> deleteById(@PathVariable Long id) {
            skTokenService.deleteById(id);
        return new CommonRes<>("删除SkToken成功");

    }

}
