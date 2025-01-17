package com.wgcloud.controller;

import com.github.pagehelper.PageInfo;
import com.wgcloud.common.BaseOp;
import com.wgcloud.entity.HeathMonitor;
import com.wgcloud.service.GroupInfoService;
import com.wgcloud.service.HeathMonitorService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.task.ScheduledTask;
import com.wgcloud.util.PageUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:HeathMonitorController.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: HeathMonitorController.java
 *
 */
@Controller
@RequestMapping("/heathMonitor")
public class HeathMonitorController {


    private static final Logger logger = LoggerFactory.getLogger(HeathMonitorController.class);

    @Resource
    private HeathMonitorService heathMonitorService;
    @Resource
    private LogInfoService logInfoService;
    @Resource
    private GroupInfoService groupInfoService;


    /**
     * 根据条件查询心跳监控列表
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "list")
    public String heathMonitorList(HeathMonitor heathMonitor, Model model) {
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            if(heathMonitor.getHeathGroup() == null || heathMonitor.getHeathGroup().isEmpty()){

            }
            else{
                params.put("heathGroup",heathMonitor.getHeathGroup());
            }

            PageInfo pageInfo = heathMonitorService.selectByParams(params, heathMonitor.getPage(), heathMonitor.getPageSize());
            PageUtil.initPageNumber(pageInfo, model);
            model.addAttribute("pageUrl", "/heathMonitor/list?1=1");
            model.addAttribute("page", pageInfo);
            model.addAttribute("group",groupInfoService.selectAll());
        } catch (Exception e) {
            logger.error("查询服务心跳监控错误", e);
            logInfoService.save("查询心跳监控错误", e.toString(), StaticKeys.LOG_ERROR);

        }
        return "heath/list";
    }


    /**
     * 保存心跳监控信息
     *
     * @param HeathMonitor
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "save")
    public String saveHeathMonitor(HeathMonitor HeathMonitor, Model model, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(HeathMonitor.getId())) {
                heathMonitorService.save(HeathMonitor);
            } else {
                heathMonitorService.updateById(HeathMonitor);
            }

        } catch (Exception e) {
            logger.error("保存服务心跳监控错误：", e);
            logInfoService.save(HeathMonitor.getAppName(), "保存心跳监控错误：" + e.toString(), StaticKeys.LOG_ERROR);
        }
        return "redirect:/heathMonitor/list";
    }

    /**
     * 测试心跳监控信息
     *
     * @param HeathMonitor
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "test")
    public String testHeathMonitor(HeathMonitor HeathMonitor, Model model, HttpServletRequest request) {
        try {

            Map<String, Object> params = new HashMap<>();
            List<HeathMonitor> heathMonitorAllList = heathMonitorService.selectAllByParams(params);
            Boolean ret =  ScheduledTask.execHeathMonitorTask(heathMonitorAllList,HeathMonitor);
            HeathMonitor.setHeathStatus("code="+HeathMonitor.getHeathStatus() + ";   "+(ret?"脚本验证成功":"脚本验证失败"));
            model.addAttribute("heathMonitor", HeathMonitor);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "heath/view";
    }


    /**
     * 查看该心跳监控
     *
     * @param HeathMonitor
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "edit")
    public String edit(Model model, HttpServletRequest request) throws Exception {
        String errorMsg = "编辑服务心跳监控：";
        String id = request.getParameter("id");
        HeathMonitor heathMonitor = new HeathMonitor();
        if (StringUtils.isEmpty(id)) {
            model.addAttribute("heathMonitor", heathMonitor);
            model.addAttribute("group",groupInfoService.selectAll());
            return "heath/add";
        }

        try {
            heathMonitor = heathMonitorService.selectById(id);
            model.addAttribute("heathMonitor", heathMonitor);
            model.addAttribute("group",groupInfoService.selectAll());
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(heathMonitor.getAppName(), errorMsg + e.toString(), StaticKeys.LOG_ERROR);
        }
        return "heath/add";
    }

    /**
     * 查看该心跳监控
     *
     * @param HeathMonitor
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "view")
    public String view(Model model, HttpServletRequest request) {
        String errorMsg = "查看服务心跳监控：";
        String id = request.getParameter("id");
        String date = request.getParameter("date");
        HeathMonitor heathMonitor = new HeathMonitor();
        try {
            heathMonitor = heathMonitorService.selectById(id);
            model.addAttribute("heathMonitor", heathMonitor);
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(heathMonitor.getAppName(), errorMsg + e.toString(), StaticKeys.LOG_ERROR);
        }
        return "heath/view";
    }


    /**
     * 删除心跳监控
     *
     * @param id
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "del")
    public String delete(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String errorMsg = "删除服务心跳监控错误：";
        HeathMonitor HeathMonitor = new HeathMonitor();
        try {
            if (!StringUtils.isEmpty(request.getParameter("id"))) {
                HeathMonitor = heathMonitorService.selectById(request.getParameter("id"));
                logInfoService.save("删除服务心跳监控：" + HeathMonitor.getAppName(), "删除服务心跳监控：" + HeathMonitor.getAppName() + "：" + HeathMonitor.getHeathUrl(), StaticKeys.LOG_ERROR);
                heathMonitorService.deleteById(request.getParameter("id").split(","));
            }
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(HeathMonitor.getAppName(), errorMsg + e.toString(), StaticKeys.LOG_ERROR);
        }

        return "redirect:/heathMonitor/list";
    }

    /**
     * 根据条件查询心跳监控列表
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "fun")
    public String funView( Model model) {
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            model.addAttribute("page", BaseOp.getFuns());
        } catch (Exception e) {
            logger.error("查询服务心跳监控错误", e);
            logInfoService.save("查询心跳监控错误", e.toString(), StaticKeys.LOG_ERROR);

        }
        return "heath/fun";
    }


}
