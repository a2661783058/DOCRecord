package com.cr.web;

import com.cr.entity.LoginUserBean;
import com.cr.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * create by lishengbo on 2018-01-02 17:42
 */
@Controller//放入Spring容器
@RequestMapping("/User")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String list(@RequestBody Map<String,String> map, Model model) {
        LoginUserBean loginUserBean=userService.login(map);


//        //列表获取页
//        //list.jsp +model=ModelAndView
//        List<SeckillBean> seckillList = seckillService.getSeckillList();
//        model.addAttribute("list", seckillList);
        return "index";//web-inf/jsp/list.jsp
    }

//    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
//    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
//        if (seckillId == null) {
//            return "redirect:/seckill/list";
//        }
//        SeckillBean byId = seckillService.getById(seckillId);
//        if (byId == null) {
//            return "forward:/seckill/list";
//        }
//        model.addAttribute("seckill", byId);
//        return "detail";
//    }
//
//    //ajax  json  开启秒杀
//    @RequestMapping(value = "/{seckillId}/exposer",
//            method = RequestMethod.POST,
//            produces = {"application/json;charset=UTF-8"}
//    )
//    @ResponseBody
//    public SeckillResult<Exposer> exposer(@PathVariable("seckillId")Long seckillId) {
//        SeckillResult<Exposer> seckillResult;
//        try {
//            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
//            seckillResult = new SeckillResult<Exposer>(true, exposer);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            seckillResult = new SeckillResult<Exposer>(false, e.getMessage());
//        }
//        return seckillResult;
//    }
//
//
//    @RequestMapping(value = "/{seckillId}/{md5}/execution",
//            method = RequestMethod.POST,
//            produces = {"application/json;charset=UTF-8"}
//    )
//    @ResponseBody
//    public SeckillResult<SeckillExecution> execution(@PathVariable("seckillId") Long seckillId,
//                                                     @PathVariable("md5") String md5,
//                                                     @CookieValue(value = "killPhone", required = false/*false表示非必要*/) Long phone) {
//        //可以使用springmvc 的验证vaild
//        if (phone == null) {
//            return new SeckillResult<SeckillExecution>(false, "未注册");
//        }
//        try {
//            //换成存储过程执行秒杀
////            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
//            SeckillExecution seckillExecution = seckillService.executeSeckillByProceduce(seckillId, phone, md5);
//            return new SeckillResult<SeckillExecution>(true, seckillExecution);
//        } catch (SeckillCloseException e) {
//            return new SeckillResult<SeckillExecution>(true, new SeckillExecution(seckillId, SeckillStateEnum.END));
//        } catch (RepeatKillException e) {
//            return new SeckillResult<SeckillExecution>(true,  new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL));
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            return new SeckillResult<SeckillExecution>(true, new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR));
//        }
//    }
//
//    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
//    @ResponseBody
//    public SeckillResult<Long> time(){
//        return new SeckillResult<Long>(true,new Date().getTime());
//    }

}
