package com.jktickets.req.merber;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberRegisterReq {
//    @NotNull 来自springboot的自带的validation 组件 需要在Controller添加@Valid
    @NotBlank(message = "【手机号】不能为空")
    private String mobile;


}
