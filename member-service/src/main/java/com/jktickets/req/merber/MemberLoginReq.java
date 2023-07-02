package com.jktickets.req.merber;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberLoginReq {
//    @NotNull 来自springboot的自带的validation 组件 需要在Controller添加@Valid
    @NotBlank(message = "【手机号】不能为空")
    private String mobile;
    @NotBlank(message = "【短信验证码】不能为空")
    private String code;

}
