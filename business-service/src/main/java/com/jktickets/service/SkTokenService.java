package com.jktickets.service;



import com.jktickets.req.skToken.SkTokenQueryReq;
import com.jktickets.req.skToken.SkTokenSaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.skToken.SkTokenQueryRes;

import java.util.Date;
import java.util.List;

public interface SkTokenService {
   void saveSkToken(SkTokenSaveReq req);
   PageRes<SkTokenQueryRes> querySkTokenList(SkTokenQueryReq req);

   void deleteById(Long id);



   //    初始化
    void genDaily(Date date, String trainCode);

//    获取令牌
    boolean validSkToken(Date date,String trainCode,Long memberId);

}