package com.jktickets.service;



import com.jktickets.req.${domain}.${Domain}QueryReq;
import com.jktickets.req.${domain}.${Domain}SaveReq;
import com.jktickets.res.PageRes;
import com.jktickets.res.${domain}.${Domain}QueryRes;

import java.util.List;

public interface ${Domain}Service {
   void save${Domain}(${Domain}SaveReq req);
   PageRes<${Domain}QueryRes> query${Domain}List(${Domain}QueryReq req);

   void deleteById(Long id);
}