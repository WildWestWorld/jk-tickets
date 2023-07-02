package com.jktickets.req.passenger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
@Data
public class PassengerQueryReq {
    private Long memberId;


}