package com.wgcloud.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wgcloud.entity.*;
import com.wgcloud.service.CmdInfoService;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.service.SystemInfoService;
import com.wgcloud.util.TokenUtils;
import com.wgcloud.util.msg.WarnMailUtil;
import com.wgcloud.util.staticvar.BatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version v2.3
 * @ClassName:AgentController.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: AgentController.java
 *
 */
@Controller
@RequestMapping("/agent")
public class AgentController {


    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 40, 2, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    @Autowired
    private TokenUtils tokenUtils;
    @Resource
    private CmdInfoService cmdInfoService;

    @ResponseBody
    @RequestMapping("/minTask")
    public JSONObject minTask(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        JSONObject resultJson = new JSONObject();
        if (!tokenUtils.checkAgentToken(agentJsonObject)) {
            logger.error("token is invalidate");
            resultJson.put("result", "error：token is invalidate");
            return resultJson;
        }
        JSONObject cpuState = agentJsonObject.getJSONObject("cpuState");
        JSONObject memState = agentJsonObject.getJSONObject("memState");
        JSONObject sysLoadState = agentJsonObject.getJSONObject("sysLoadState");
        JSONArray appInfoList = agentJsonObject.getJSONArray("appInfoList");
        JSONArray appStateList = agentJsonObject.getJSONArray("appStateList");
        JSONObject logInfo = agentJsonObject.getJSONObject("logInfo");
        JSONObject systemInfo = agentJsonObject.getJSONObject("systemInfo");
        JSONObject netIoState = agentJsonObject.getJSONObject("netIoState");
        JSONArray deskStateList = agentJsonObject.getJSONArray("deskStateList");
        JSONObject netConnetInfo = agentJsonObject.getJSONObject("netConnetInfo");

        try {

            if (logInfo != null) {
                LogInfo bean = new LogInfo();
                BeanUtil.copyProperties(logInfo, bean);
                BatchData.LOG_INFO_LIST.add(bean);
            }
            if (cpuState != null) {
                CpuState bean = new CpuState();
                BeanUtil.copyProperties(cpuState, bean);
                BatchData.CPU_STATE_LIST.add(bean);
                Runnable runnable = () -> {
                    WarnMailUtil.sendCpuWarnInfo(bean);
                };
                executor.execute(runnable);
            }
            if (memState != null) {
                MemState bean = new MemState();
                BeanUtil.copyProperties(memState, bean);
                BatchData.MEM_STATE_LIST.add(bean);
                Runnable runnable = () -> {
                    WarnMailUtil.sendWarnInfo(bean);
                };
                executor.execute(runnable);
            }
            if (netConnetInfo != null) {
                NetConnetInfo bean = new NetConnetInfo();
                BeanUtil.copyProperties(netConnetInfo, bean);
                BatchData.NET_STATE_LIST.add(bean);
                Runnable runnable = () -> {
                    WarnMailUtil.sendNetWarnInfo(bean);
                };
                executor.execute(runnable);
            }
            if (sysLoadState != null) {
                SysLoadState bean = new SysLoadState();
                BeanUtil.copyProperties(sysLoadState, bean);
                BatchData.SYSLOAD_STATE_LIST.add(bean);
            }
            if (netIoState != null) {
                NetIoState bean = new NetIoState();
                BeanUtil.copyProperties(netIoState, bean);
                BatchData.NETIO_STATE_LIST.add(bean);
            }
            if (appInfoList != null && appStateList != null) {
                List<AppInfo> appInfoResList = JSONUtil.toList(appInfoList, AppInfo.class);
                for (AppInfo appInfo : appInfoResList) {
                    BatchData.APP_INFO_LIST.add(appInfo);
                }
                List<AppState> appStateResList = JSONUtil.toList(appStateList, AppState.class);
                for (AppState appState : appStateResList) {
                    BatchData.APP_STATE_LIST.add(appState);
                }
            }
            Double maxValue = 0d;
            if (deskStateList != null) {
                DeskState maxBean = null;
                for (Object jsonObjects : deskStateList) {
                    DeskState bean = new DeskState();
                    BeanUtil.copyProperties(jsonObjects, bean);
                    BatchData.DESK_STATE_LIST.add(bean);
                    try {
                        Double num2 = NumberFormat.getPercentInstance().parse(bean.getUsePer()).doubleValue();
                        if (num2 >= maxValue) {
                            maxValue = num2;
                            maxBean = bean;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                DeskState finalMaxBean = maxBean;
                Runnable runnable = () -> {
                    WarnMailUtil.sendDeskWarnInfo(finalMaxBean);
                };
                executor.execute(runnable);
            }
            if (systemInfo != null) {
                SystemInfo bean = new SystemInfo();
                BeanUtil.copyProperties(systemInfo, bean);
                bean.setDiskPer(maxValue.doubleValue()*100);
                BatchData.SYSTEM_INFO_LIST.add(bean);
            }
            resultJson.put("result", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("result", "error：" + e.toString());
        } finally {
            return resultJson;
        }
    }
    @ResponseBody
    @RequestMapping("/cmd")
    public JSONObject cmd(@RequestBody String paramBean) {
        JSONObject agentJsonObject = (JSONObject) JSONUtil.parse(paramBean);
        if(agentJsonObject != null && agentJsonObject.getJSONArray("cmds") != null){
            JSONArray arrary = agentJsonObject.getJSONArray("cmds");
            for(int i = 0; i < arrary.size();i++){
                CmdInfo cmdInfo =  arrary.get(i,CmdInfo.class);
                if(cmdInfo != null &&cmdInfo.getId() != null){
                    if(cmdInfo.getExecTime() == null){
                        cmdInfo.setExecTime(new Date());
                    }
                    if(cmdInfo.getResult() == null)continue;
                    if(cmdInfo.getResult().length() > 9999){
                        cmdInfo.setResult(cmdInfo.getResult().substring(0,9999));
                    }
                    try {
                        cmdInfoService.updateById(cmdInfo);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        if(agentJsonObject.getStr("hostname") != null){
            String hosname = agentJsonObject.getStr("hostname");
            try {
                    List<CmdInfo> list = cmdInfoService.selectNoExecCmd(hosname);
                    agentJsonObject.put("cmds",JSONUtil.parseArray(list));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        agentJsonObject.put("code",0);
        return agentJsonObject;
    }

}
