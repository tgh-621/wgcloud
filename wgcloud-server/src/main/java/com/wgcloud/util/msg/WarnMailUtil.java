package com.wgcloud.util.msg;

import com.wgcloud.common.ApplicationContextHelper;
import com.wgcloud.config.MailConfig;
import com.wgcloud.entity.*;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version v2.3
 * @ClassName:WarnMailUtil.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: WarnMailUtil.java
 *
 */
public class WarnMailUtil {

    private static final Logger logger = LoggerFactory.getLogger(WarnMailUtil.class);

    public static final String content_suffix = "<p><a target='_blank' href='http://www.bigdatacd.com'>大数据监控系统</a>敬上";

    private static LogInfoService logInfoService = (LogInfoService) ApplicationContextHelper.getBean(LogInfoService.class);
    private static MailConfig mailConfig = (MailConfig) ApplicationContextHelper.getBean(MailConfig.class);

    public static int mW = 0;//内存
    public static int cW = 0;//cpu
    public static int dW = 0;//磁盘
    public static int aW = 0;//应用
    public static int hW = 0;//主机
    public static int sW = 0;//接口
    public static int dbW = 0;//数据


    /**
     * 发送总结邮件
     *
     * @return
     */
    public static boolean sendTotalInfo() {
            try {
                String title = "服务器监控日报：" + DateUtil.getCurrentDate();
                String commContent = "昨日报警情况如下:<br/>内存报警:"+mW+"次<br/>" +
                        "CPU报警:"+cW+"次<br/>" +
                        "磁盘报警:"+dW+"次<br/>" +
                        "应用报警:"+aW+"次<br/>" +
                        "主机报警:"+hW+"次<br/>" +
                        "接口报警:"+sW+"次<br/>" +
                        "数据报警:"+dbW+"次<br/>" ;
                //发送邮件
                sendMail(null, title, commContent);
                mW=cW=dW=aW=hW=sW=dbW=0;
            } catch (Exception e) {
                logger.error("发送服务器监控日报失败：", e);
                logInfoService.save("发送服务器监控日报错误", e.toString(), StaticKeys.LOG_ERROR);
            }


        return true;
    }

    /**
     * 判断系统内存使用率是否超过98%，超过则发送告警邮件
     *
     * @param memState
     * @return
     */
    public static boolean sendWarnInfo(MemState memState) {
        String key = memState.getHostname();
        Integer ct = WarnPools.MEM_WARN_MAP.get(key);
        if(ct == null)ct = 0;
        if (memState.getUsePer() != null && memState.getUsePer() >= mailConfig.getMemWarnVal()) {
            mW++;
            ct++;
            WarnPools.MEM_WARN_MAP.put(key, ct);
            if ( (ct > 5) && (ct % 5 !=0)) {
                return false;
            }
            try {
                String title = "内存告警：" + memState.getHostname();
                String commContent = "服务器：" + memState.getHostname() + ",内存使用率为" + Double.valueOf(memState.getUsePer()) + "%，连续警告"+ct+"次,可能存在异常，请查看";
                //发送邮件
                sendMail(null, title, commContent);
                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送内存告警邮件失败：", e);
                logInfoService.save("发送内存告警邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        }
        else if(ct > 0){

            ct=0;
            WarnPools.MEM_WARN_MAP.put(key, ct);
            try {
                String title = "内存恢复通知：" + memState.getHostname();
                String commContent = "服务器：" + memState.getHostname() + ",内存使用率为" + Double.valueOf(memState.getUsePer()) + "%，已经恢复到警戒线以下";
                //发送邮件
                sendMail(null, title, commContent);
                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送内存告警邮件失败：", e);
                logInfoService.save("发送内存恢复邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }

        }

        return false;
    }

    /**
     * 判断系统cpu使用率是否超过98%，超过则发送告警邮件
     *
     * @param cpuState
     * @return
     */
    public static boolean sendCpuWarnInfo(CpuState cpuState) {
        String key = cpuState.getHostname();
        Integer ct = WarnPools.CPU_WARN_MAP.get(key);
        if(ct== null)ct =0;
        if (cpuState.getSys() != null && cpuState.getSys() >= mailConfig.getCpuWarnVal()) {
            try {
                ct++;
                cW++;
                WarnPools.CPU_WARN_MAP.put(key, ct);
                if ( (ct > 5) && (ct % 5 !=0)) {
                    return false;
                }
                String title = "CPU告警：" + cpuState.getHostname();
                String commContent = "服务器：" + cpuState.getHostname() + ",CPU使用率为" + Double.valueOf(cpuState.getSys()) + "%，连续警告"+ct+"次，可能存在异常，请查看";
                //发送邮件
                sendMail(null, title, commContent);

                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送内存告警邮件失败：", e);
                logInfoService.save("发送内存告警邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        }
        else if(ct > 0){
            try {
                ct=0;
                WarnPools.CPU_WARN_MAP.put(key, ct);
                String title = "CPU报警解除：" + cpuState.getHostname();
                String commContent = "服务器：" + cpuState.getHostname() + ",CPU使用率为" + Double.valueOf(cpuState.getSys()) + "%，恢复到报警线以下";
                //发送邮件
                sendMail(null, title, commContent);
                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送内存告警邮件失败：", e);
                logInfoService.save("发送内存报警解除邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        }

        return false;
    }

    /**
     * 判断系统cpu使用率是否超过98%，超过则发送告警邮件
     *
     * @param deskState
     * @return
     */
    public static boolean sendDeskWarnInfo(DeskState deskState) {
        String key = deskState.getHostname();
        Integer ct = WarnPools.DESK_WARN_MAP.get(key);
        if(ct == null)ct = 0;
        try{
            Double num2 = NumberFormat.getPercentInstance().parse(deskState.getUsePer()).doubleValue();
                if (num2 >= 0.9) {
                    dW++;
                    ct++;
                    WarnPools.DESK_WARN_MAP.put(key, ct);
                    if ( (ct > 5) && (ct % 20 !=0)) {
                        return false;
                    }
                    String title = "硬盘告警：" + deskState.getHostname();
                    String commContent = "服务器：" + deskState.getHostname() + ",硬盘"+deskState.getFileSystem()+"使用率为" +deskState.getUsePer() + "，可能存在异常，请尽快处理";
                    //发送邮件
                    sendMail(null, title, commContent);
                    //记录发送信息
                    logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
                }
                else if(ct > 0){
                    ct =0;
                    WarnPools.DESK_WARN_MAP.put(key, ct);
                    String title = "硬盘空间恢复：" + deskState.getHostname();
                    String commContent = "服务器：" + deskState.getHostname() + ",硬盘"+deskState.getFileSystem()+"使用率为" +deskState.getUsePer() + "，已经恢复到警报水平以下，请留意观察";
                    //发送邮件
                    sendMail(null, title, commContent);
                    logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);

                }
            } catch (Exception e) {
                logger.error("发送磁盘告警邮件失败：", e);
                logInfoService.save("发送磁盘告警邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        return false;
    }


    /**
     * 服务接口不通发送告警邮件
     *
     * @param heathMonitor
     * @param isDown
     * @return
     */
    public static boolean sendHeathInfo(HeathMonitor heathMonitor, boolean isDown) {
        String key = heathMonitor.getId();
        if (isDown) {
            Integer ct = WarnPools.API_WARN_MAP.get(key);
            if(ct == null)ct = 0;
            try {
                sW++;
                ct++;
                //标记已发送过告警信息
                WarnPools.API_WARN_MAP.put(key, ct);
                if ( (ct > 5) && (ct % 5 !=0)) {
                    return false;
                }
                String title = "服务接口检测告警：" + heathMonitor.getAppName();
                String commContent = "服务接口：" + heathMonitor.getHeathUrl() + "，响应状态码为" + heathMonitor.getHeathStatus() + "，可能存在异常，请查看<br/>返回结果："+heathMonitor.getLastResult();
                //发送邮件
                sendMail(null, title, commContent);

                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送服务健康检测告警邮件失败：", e);
                logInfoService.save("发送服务健康检测告警邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        } else {
            WarnPools.API_WARN_MAP.remove(key);
            try {
                String title = "服务接口恢复正常通知：" + heathMonitor.getAppName();
                String commContent = "服务接口恢复正常通知：" + heathMonitor.getHeathUrl() + "，响应状态码为" + heathMonitor.getHeathStatus() + "";
                //发送邮件
                sendMail(null, title, commContent);
                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送服务接口恢复正常通知邮件失败：", e);
                logInfoService.save("发送服务接口恢复正常通知邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        }
        return false;
    }

    private static String makeEmail(String email){
        if(email == null || email.length()<5){
            return StaticKeys.mailSet.getToMail();
        }
        if(StaticKeys.mailSet == null || StaticKeys.mailSet.getToMail() == null || StaticKeys.mailSet.getToMail().length() < 5){
            return email;
        }
        return StaticKeys.mailSet.getToMail()+";"+email;
    }

    /**
     * 数据不正常发送告警邮件
     *
     * @param tableInfo 主机信息
     * @return
     */
    public static boolean sendDbTableDataCountError(DbInfo dbInfo,DbTable tableInfo,String info) {
        int count = tableInfo.getWarnCount();
        if(count > 5 && count%5 != 0)return true;

            try {
                dbW++;
                String title = "表数据异常警告：数据库=" + dbInfo.getAliasName()+";表="+ tableInfo.getRemark();
                String commContent = "表数据存在异常，请检查数据库=" + dbInfo.getAliasName()+";表="+ tableInfo.getRemark()+"（"+tableInfo.getTableName()+"）" + "，<br/>备注：where=" + tableInfo.getWhereVal()+",<br/>sql="+tableInfo.getSql()
                        + "，<br/>累计错误次数="+tableInfo.getWarnCount()+"<br/>额外信息:"+info+"<br/>";
                if(tableInfo.getWarnCountL() != null){
                    if(tableInfo.getTableCount() < tableInfo.getWarnCountL()){
                        //报警
                        commContent+="当前查询值为:"+tableInfo.getTableCount() +";小于报警阀值:"+tableInfo.getWarnCountL();
                    }

                }
                if(tableInfo.getWarnCountH() != null){
                    if(tableInfo.getTableCount() > tableInfo.getWarnCountH()){
                        //报警
                        commContent+="当前查询值为:"+tableInfo.getTableCount() +";大于报警阀值:"+tableInfo.getWarnCountH();
                    }

                }
                //发送邮件
                sendMail(null, title, commContent);
                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("表数据异常警告邮件失败：", e);
                logInfoService.save("表数据异常警告邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }


        return true;
    }

    public static boolean sendDbTableDataCountRight(DbInfo dbInfo,DbTable tableInfo) {
        MailSet mailSet = StaticKeys.mailSet;
        try {
            String title = "表数据恢复正常：数据库=" + dbInfo.getAliasName()+";表="+tableInfo.getRemark();
            String commContent = "表数据恢复正常:数据库=" + dbInfo.getAliasName()+";表="+ tableInfo.getRemark()+"（"+tableInfo.getTableName()+"）" + "，<br/>备注：where=" + tableInfo.getWhereVal()+"<br/>sql="+tableInfo.getSql()
                    + "<br/>";
            //发送邮件
            sendMail(null, title, commContent);
            //记录发送信息
            logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
        } catch (Exception e) {
            logger.error("表数据恢复正常邮件失败：", e);
            logInfoService.save("表数据恢复正常邮件错误", e.toString(), StaticKeys.LOG_ERROR);
        }


        return false;
    }


    /**
     * 主机下线发送告警邮件
     *
     * @param systemInfo 主机信息
     * @param isDown     是否是下线告警，true下线告警，false上线恢复
     * @return
     */
    public static boolean sendHostDown(SystemInfo systemInfo, boolean isDown) {
        String key = systemInfo.getId();
        Integer ct = WarnPools.HOST_WARN_MAP.get(key);
        if(ct == null)ct=0;
        if (isDown) {
            hW++;
            ct++;
            WarnPools.HOST_WARN_MAP.put(key, ct);
            if ( (ct > 5) && (ct % 5 !=0)) {
                return true;
            }
            try {
                String title = "主机下线告警：" + systemInfo.getHostname();
                String commContent = "主机已经超过10分钟未上报数据，可能已经下线：" + systemInfo.getHostname() + "，备注：" + systemInfo.getHostRemark()
                        + "。如果不再监控该主机在列表删除即可，同时不会再收到该主机告警邮件";
                //发送邮件
                sendMail(null, title, commContent);

                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送主机下线告警邮件失败：", e);
                logInfoService.save("发送主机下线告警邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        } else {
            WarnPools.HOST_WARN_MAP.remove(key);
            try {
                String title = "主机恢复上线通知：" + systemInfo.getHostname();
                String commContent = "主机已经恢复上线：" + systemInfo.getHostname() + "，备注：" + systemInfo.getHostRemark()
                        + "。";
                //发送邮件
                sendMail(null, title, commContent);
                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送主机恢复上线通知邮件失败：", e);
                logInfoService.save("发送主机恢复上线通知邮件错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        }
        return false;
    }

    /**
     * 进程下线发送告警邮件
     *
     * @param appInfo 进程信息
     * @param isDown  是否是下线告警，true下线告警，false上线恢复
     * @return
     */
    public static boolean sendAppDown(AppInfo appInfo, boolean isDown) {
        String key = appInfo.getId();
        Integer ct = WarnPools.APP_WARN_MAP.get(key);
        if(ct == null)ct=0;
        if (isDown) {
            try {
                aW++;
                ct++;
                WarnPools.APP_WARN_MAP.put(key, ct);
                if ( (ct > 5) && (ct % 5 !=0)) {
                    return true;
                }
                String title = "进程下线告警：" + appInfo.getHostname() + "，" + appInfo.getAppName();
                String commContent = "进程已经超过10分钟未上报数据，可能已经下线：" + appInfo.getHostname() + "，" + appInfo.getAppName()
                        + "。如果不再监控该进程在列表删除即可，同时不会再收到该进程告警邮件";
                //发送邮件
                sendMail(null, title, commContent);

                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送进程下线告警邮件失败：", e);
                logInfoService.save("发送进程下线告警错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        } else {
            WarnPools.APP_WARN_MAP.remove(key);
            try {
                String title = "进程恢复上线通知：" + appInfo.getHostname() + "，" + appInfo.getAppName();
                String commContent = "进程恢复上线通知：" + appInfo.getHostname() + "，" + appInfo.getAppName();
                //发送邮件
                sendMail(null, title, commContent);
                //记录发送信息
                logInfoService.save(title, commContent, StaticKeys.LOG_ERROR);
            } catch (Exception e) {
                logger.error("发送进程恢复上线通知邮件失败：", e);
                logInfoService.save("发送进程恢复上线通知错误", e.toString(), StaticKeys.LOG_ERROR);
            }
        }
        return false;
    }

    public static String sendMail(String mails, String mailTitle, String mailContent) {
/*
        try {
            Jsoup.connect("https://10.1.235.31:18097/robot/send?access_token=21caa07fbf37aca46bf39c7d0acaab6acdbfbc6da8ecda80cdfe717063ec97b2").ignoreContentType(true).ignoreHttpErrors(true).
                    requestBody("{\"msgtype\": \"text\",\"text\": {\"content\":\"四川省环保厅政务云大数据平台:"+(mailContent.replace("\"","\\\"").replace("<br/>","\n").substring(0,350)+"\"}}"))
                    .header("Content-Type", "application/json")
                    .post();
        }
        catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        */
        return "success";

    }


}
