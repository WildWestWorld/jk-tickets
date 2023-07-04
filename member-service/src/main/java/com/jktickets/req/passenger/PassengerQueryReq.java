package com.jktickets.req.passenger;

import com.jktickets.req.PageReq;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
@Data
public class PassengerQueryReq extends PageReq {
    private Long memberId;


}