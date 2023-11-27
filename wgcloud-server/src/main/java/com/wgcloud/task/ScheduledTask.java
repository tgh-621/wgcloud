package com.wgcloud.task;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.alibaba.fastjson.*;
import com.wgcloud.common.BaseOp;
import com.wgcloud.config.CommonConfig;
import com.wgcloud.entity.*;
import com.wgcloud.mapper.*;
import com.wgcloud.service.*;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.MD5Utils;
import com.wgcloud.util.RestUtil;
import com.wgcloud.util.jdbc.ConnectionUtil;
import com.wgcloud.util.jdbc.RDSConnection;
import com.wgcloud.util.msg.WarnMailUtil;
import com.wgcloud.util.msg.WarnPools;
import com.wgcloud.util.staticvar.BatchData;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version v2.3
 * @ClassName:ScheduledTask.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: ScheduledTask.java
 *
 */
@Component
public class ScheduledTask {

    private Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    /**
     * 线程池
     */
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 40, 2, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    @Autowired
    SystemInfoService systemInfoService;
    @Autowired
    DeskStateService deskStateService;
    @Autowired
    LogInfoService logInfoService;
    @Autowired
    AppInfoService appInfoService;
    @Autowired
    CpuStateService cpuStateService;
    @Autowired
    MemStateService memStateService;
    @Autowired
    NetIoStateService netIoStateService;
    @Autowired
    SysLoadStateService sysLoadStateService;
    @Autowired
    TcpStateService tcpStateService;
    @Autowired
    AppStateService appStateService;
    @Autowired
    MailSetService mailSetService;
    @Autowired
    IntrusionInfoService intrusionInfoService;
    @Autowired
    HostInfoService hostInfoService;
    @Autowired
    DbInfoService dbInfoService;
    @Autowired
    DbTableService dbTableService;
    @Autowired
    DbTableCountService dbTableCountService;
    @Autowired
    HeathMonitorService heathMonitorService;
    @Autowired
    private RestUtil restUtil;
    @Autowired
    ConnectionUtil connectionUtil;
    @Autowired
    CommonConfig commonConfig;

    @Autowired
    NetStateMapper netStateMapper;

    /**
     * 20秒后执行
     * 初始化操作
     */
    @Scheduled(initialDelay = 20000L, fixedRate = 600 * 60 * 1000)
    public void initTask() {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            List<MailSet> list = mailSetService.selectAllByParams(params);
            if (list.size() > 0) {
                StaticKeys.mailSet = list.get(0);
            }
        } catch (Exception e) {
            logger.error("初始化操作错误", e);
        }

    }


    /**
     * 300秒后执行
     * 检测主机是否已经下线，检测进程是否下线
     */
    @Scheduled(initialDelay = 300000L, fixedRate = 20 * 60 * 1000)
    public void hostDownCheckTask() {
        Date date = DateUtil.getNowTime();
        long delayTime = 900 * 1000;

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            List<SystemInfo> list = systemInfoService.selectAllByParams(params);
            if (!CollectionUtil.isEmpty(list)) {
                List<SystemInfo> updateList = new ArrayList<SystemInfo>();
                List<LogInfo> logInfoList = new ArrayList<LogInfo>();
                for (SystemInfo systemInfo : list) {

                    Date createTime = systemInfo.getCreateTime();
                    long diff = date.getTime() - createTime.getTime();
                    if (diff > delayTime) {
                        if (WarnPools.HOST_WARN_MAP.get(systemInfo.getId())!=null) {
                            continue;
                        }
                        systemInfo.setState(StaticKeys.DOWN_STATE);
                        LogInfo logInfo = new LogInfo();
                        logInfo.setHostname("主机下线：" + systemInfo.getHostname());
                        logInfo.setInfoContent("超过10分钟未上报状态，可能已下线：" + systemInfo.getHostname());
                        logInfo.setState(StaticKeys.LOG_ERROR);
                        logInfoList.add(logInfo);
                        updateList.add(systemInfo);
                        Runnable runnable = () -> {
                            WarnMailUtil.sendHostDown(systemInfo, true);
                        };
                        executor.execute(runnable);
                    } else {
                        if (WarnPools.HOST_WARN_MAP.get(systemInfo.getId())!=null) {
                            Runnable runnable = () -> {
                                WarnMailUtil.sendHostDown(systemInfo, false);
                            };
                            executor.execute(runnable);
                        }
                    }
                }
                if (updateList.size() > 0) {
                    systemInfoService.updateRecord(updateList);
                }
                if (logInfoList.size() > 0) {
                    logInfoService.saveRecord(logInfoList);
                }
            }
        } catch (Exception e) {
            logger.error("检测主机是否下线错误", e);
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            List<AppInfo> list = appInfoService.selectAllByParams(params);
            if (!CollectionUtil.isEmpty(list)) {
                List<AppInfo> updateList = new ArrayList<AppInfo>();
                List<LogInfo> logInfoList = new ArrayList<LogInfo>();
                for (AppInfo appInfo : list) {

                    Date createTime = appInfo.getCreateTime();
                    long diff = date.getTime() - createTime.getTime();
                    if (diff > delayTime) {
                        if (WarnPools.APP_WARN_MAP.get(appInfo.getId())!=null) {
                            continue;
                        }
                        appInfo.setState(StaticKeys.DOWN_STATE);
                        LogInfo logInfo = new LogInfo();
                        logInfo.setHostname("进程下线IP：" + appInfo.getHostname() + "，名称：" + appInfo.getAppName());
                        logInfo.setInfoContent("超过10分钟未上报状态，可能已下线IP：" + appInfo.getHostname() + "，名称：" + appInfo.getAppName() + "，进程ID：" + appInfo.getAppPid());
                        logInfo.setState(StaticKeys.LOG_ERROR);
                        logInfoList.add(logInfo);
                        updateList.add(appInfo);
                        Runnable runnable = () -> {
                            WarnMailUtil.sendAppDown(appInfo, true);
                        };
                        executor.execute(runnable);
                    } else {
                        if (WarnPools.APP_WARN_MAP.get(appInfo.getId())!=null) {
                            Runnable runnable = () -> {
                                WarnMailUtil.sendAppDown(appInfo, false);
                            };
                            executor.execute(runnable);
                        }
                    }
                }
                if (updateList.size() > 0) {
                    appInfoService.updateRecord(updateList);
                }
                if (logInfoList.size() > 0) {
                    logInfoService.saveRecord(logInfoList);
                }
            }
        } catch (Exception e) {
            logger.error("检测进程是否下线错误", e);
        }


    }

    private static String  getJsFun(){
        String jsFun1 = "function parseZH(date){\n" +
                "var curr = new Date();\n" +
                "var yearPos =  date.indexOf('年');\n" +
                "var monthPos = date.indexOf('月');\n" +
                "var dayPos = date.indexOf('日');\n" +
                "var hourPos = date.indexOf('时');\n" +
                "var mutPos = date.indexOf('分');\n" +
                "var sPos = date.indexOf('秒');\n" +
                "var start = 0;\n" +
                "if(yearPos > 0){curr.setUTCFullYear(date.substr(start,yearPos-start));start = yearPos+1;}\n" +
                "if(monthPos > 0){curr.setMonth(date.substr(start,monthPos-start)-1);start = monthPos+1;}\n" +
                "if(dayPos > 0){curr.setDate(date.substr(start,dayPos-start));start = dayPos+1;}\n" +
                "if(hourPos > 0){curr.setHours(date.substr(start,hourPos-start));start = hourPos+1;}else{curr.setHours(0);}\n" +
                "if(mutPos > 0){curr.setMinutes(date.substr(start,mutPos-start));start = mutPos+1;}else{curr.setMinutes(0);}\n" +
                "if(sPos > 0){curr.setSeconds(date.substr(start,sPos-start));start = sPos+1;}else{curr.setSeconds(0);}\n" +
                "curr.setMilliseconds(0);\n" +
                "return curr;\n" +
                "};\n";
        return jsFun1;

    }

    private static Boolean heathMonitorExeJs(String js,String html,HeathMonitor heathMonitor){
        try {
            JSONObject json = null;
            JSONArray jsona = null;
            if(JSONValidator.from(html).validate()){
                try {
                    json = JSON.parseObject(html);
                }catch (Exception e){
                    jsona = JSON.parseArray(html);
                }
            }
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript");
            engine.eval("function checkHour(str,d){\n" +
                    "    var dt = new Date(str).getTime();\n" +
                    "    var nw = new Date().getTime();\n" +
                    "    return nw - dt < d*1000*60*60;\n" +
                    "};\n" +
                    getJsFun()+
                    "function heathTest (html, json,jsona) {\n" +
                    js+"\n"+
                    "return true; };\n");
            Invocable jsInvoke = (Invocable) engine;
            Object res = jsInvoke.invokeFunction("heathTest", new Object[]{html,json,jsona});



            if(res instanceof Boolean){
                return (Boolean)res;
            }
            heathMonitor.setLastResult("返回数据校验失败:  "+res.toString()+"\r\n"+heathMonitor.getLastResult());
            return false;
        }catch (Exception ex){
            ex.printStackTrace();
            heathMonitor.setLastResult("脚本执行异常:"+heathMonitor.getLastResult() +"\r\n"+ex.getMessage()+"\r\n"+getErrorInfoFromException(ex));
            return false;
        }


    }

    private static String  getJsonValue(JSONObject json,String path){
        if(path == null || path.isEmpty()) return "";
        String[] keys =  path.split("\\.");
        for(int i = 0; i < keys.length-1;i++){
            json = json.getJSONObject(keys[i]);
            if(json == null)return "";
        }
        return json.getString(keys[keys.length-1]);
    }

    public static Boolean execHeathMonitorTask(List<HeathMonitor> heathMonitorAllList,HeathMonitor curHeathMonitor) throws IOException {
        if(curHeathMonitor.getExeState() !=0)return "200".equals(curHeathMonitor.getHeathStatus());
        HeathMonitor frontMonitor = null;
        if(!StringUtils.isEmpty(curHeathMonitor.getFrontId()) && heathMonitorAllList!= null){
            //优先执行依赖的任务
            for(int i = 0;i < heathMonitorAllList.size();i++){
                HeathMonitor h =  heathMonitorAllList.get(i);
                if(h.getAppName().equals(curHeathMonitor.getFrontId())){
                    execHeathMonitorTask(heathMonitorAllList,h);
                    frontMonitor = h;
                    break;
                }
            }
        }
        curHeathMonitor.setExeState(1);
        String url = curHeathMonitor.getHeathUrl();
        url = BaseOp.replaceVal(url);
        Connection connection = Jsoup.connect(url);
        connection.timeout(120000).ignoreContentType(true).ignoreHttpErrors(true);
        Map<String,String> cookies = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        if(frontMonitor != null){
            //cookie带走
             cookies =  JSON.parseObject(frontMonitor.getCookieInfo(),new TypeReference<HashMap<String,String>>() {});
             connection.cookies(cookies);
             String body =  frontMonitor.getLastResult();
             try{
                 jsonObject = JSON.parseObject(body);
             }catch (Exception e){

             }

        }
        String requestHeaders = curHeathMonitor.getHeaderParam();
        if(!StringUtils.isEmpty(requestHeaders)){
            Map<String,String> headers =  JSON.parseObject(requestHeaders,new TypeReference<HashMap<String,String>>() {});
            //数据替换
            for (Map.Entry<String,String> entry:headers.entrySet()){
                String value = entry.getValue();
                if(value != null && value.indexOf("${c_")>= 0){
                    //cookie替换
                    int start = value.indexOf("${c_");
                    String value1 = cookies.get(value.substring(4+start,value.length()-1));
                    if(start > 0){
                        entry.setValue(value.substring(0,start)+value1);
                    }
                    else{
                        entry.setValue(value1);
                    }

                } else if(value != null && value.indexOf("${p_") >= 0 && jsonObject != null && jsonObject.size() > 0){
                    //param替换
                    int start = value.indexOf("${p_");
                    String key = value.substring(4+start,value.length()-1);
                    String value1 = getJsonValue(jsonObject,key);
                    if(start > 0){
                        entry.setValue(value.substring(0,start)+value1);
                    }
                    else{
                        entry.setValue(value1);
                    }
                }


            }
            connection.headers(headers);
        }
        connection.header("Content-Type",curHeathMonitor.getParamType());
        //connection.header("Host","gdwry.bigdatacd.com:18091");
        String requestParams = curHeathMonitor.getRequestParam();
        if(!StringUtils.isEmpty(requestParams)){
            requestParams = BaseOp.replaceVal(requestParams);
            if(curHeathMonitor.getParamType().equals("application/x-www-form-urlencoded")){
                Map<String,String> params =  JSON.parseObject(requestParams,new TypeReference<HashMap<String,String>>() {});
                Set<String> keys =  params.keySet();
                for(String key:keys){
                    String value = params.get(key);
                    int index = value.indexOf("$MD5(");
                    if(index >= 0){
                        //提取MD5
                        try {
                            int endpos = value.indexOf(')', index + 5);
                            String md5 = value.substring(index + 5, endpos);
                            params.put(key, MD5Utils.GetMD5Code(md5).toUpperCase());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else if(value != null && value.startsWith("${c_")){
                        //cookie替换
                        value = cookies.get(value.substring(4,value.length()-1));
                        params.put(key,value);

                    } else if(value != null && value.startsWith("${p_") && jsonObject != null && jsonObject.size() > 0){
                        //param替换
                        String k = value.substring(4,value.length()-1);
                        value = getJsonValue(jsonObject,k);
                        params.put(key,value);
                    }
                }
                connection.data(params);
            }
            else{
                connection.requestBody(requestParams);
            }
        }

        //get&post
        if("post".equals(curHeathMonitor.getRequestType())){
            connection.method(Connection.Method.POST);
        }
        else{
            connection.method(Connection.Method.GET);
        }
        connection.ignoreHttpErrors(true).ignoreContentType(true).validateTLSCertificates(false);
        Connection.Response response = connection.execute();
        int code =  response.statusCode();
        curHeathMonitor.setHeathStatus(""+code);
        String result = response.body();
        cookies.putAll(response.cookies());
        curHeathMonitor.setCookieInfo(JSON.toJSONString(cookies));
        curHeathMonitor.setLastResult(result);
        curHeathMonitor.setLastStatus(code);
        curHeathMonitor.setLastTime(new Date());

        if(code !=200){
            //出错了，就不执行结果判断了
            curHeathMonitor.setLastResult("接口报错: code="+code+"\r\n"+curHeathMonitor.getLastResult());
            return false;
        }

        //script
        if(!StringUtils.isEmpty(curHeathMonitor.getTestScript())) {
            Boolean ret =  heathMonitorExeJs(curHeathMonitor.getTestScript(), result,curHeathMonitor);
            if(!ret)curHeathMonitor.setHeathStatus("-1");
            return ret;
        }
        //成功
        return true;
    }

    public static String getErrorInfoFromException(Exception e) {

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String retStr = sw.toString();
            sw.close();
            pw.close();
            return retStr;
        } catch (Exception e2) {
            return "ErrorInfoFromException";
        }

    }
    /**
     * 90秒后执行，之后每隔10分钟执行, 单位：ms。
     * 检测心跳
     */
    @Scheduled(initialDelay = 20000L, fixedDelayString = "${base.heathTimes}")
    public void heathMonitorTask() {
        logger.info("heathMonitorTask------------" + DateUtil.getDateTimeString(new Date()));
        Map<String, Object> params = new HashMap<>();
        List<HeathMonitor> heathMonitors = new ArrayList<HeathMonitor>();
        List<LogInfo> logInfoList = new ArrayList<LogInfo>();
        Date date = DateUtil.getNowTime();
        try {
            List<HeathMonitor> heathMonitorAllList = heathMonitorService.selectAllByParams(params);
            if (heathMonitorAllList.size() > 0) {
                for(int i = 0;i < heathMonitorAllList.size();i++){
                    HeathMonitor h =  heathMonitorAllList.get(i);
                    if(!"运行".equals(h.getScheduled()))continue;
                    Boolean bRet = false;
                    String error = "";
                    try {
                        logger.info("开始检测接口:"+h.getHeathUrl());
                         bRet =  execHeathMonitorTask(heathMonitorAllList, h);
                        logger.info("接口检测结果:"+bRet);
                    }catch (Exception ex){
                        logger.info("接口检测出错:"+ex.getMessage());
                        ex.printStackTrace();
                        error = (ex.getMessage());
                        h.setHeathStatus("-2");
                        h.setLastResult(error+"\r\n"+getErrorInfoFromException(ex));
                    }
                        if(!bRet){
                            LogInfo logInfo = new LogInfo();
                            logInfo.setHostname("服务接口检测异常：" + h.getAppName());
                            logInfo.setInfoContent("服务接口检测异常：" + h.getAppName() + "，" + h.getHeathUrl() + "，返回状态" + h.getHeathStatus()+"<br/>返回数据:"+h.getLastResult()+"<br/>"+error);
                            logInfo.setState(StaticKeys.LOG_ERROR);
                            logInfoList.add(logInfo);
                            Runnable runnable = () -> {
                                WarnMailUtil.sendHeathInfo(h, true);
                            };
                            executor.execute(runnable);
                        }
                        else{
                            if ((WarnPools.API_WARN_MAP.get(h.getId()))!= null) {
                                Runnable runnable = () -> {
                                    WarnMailUtil.sendHeathInfo(h, false);
                                };
                                executor.execute(runnable);
                            }
                        }

                    HeathMonitor heathMonitor = new HeathMonitor();
                    heathMonitor.setId(h.getId());
                    heathMonitor.setCookieInfo(h.getCookieInfo());
                    heathMonitor.setLastResult(h.getLastResult());
                    heathMonitor.setLastStatus(h.getLastStatus());
                    heathMonitor.setLastTime(h.getLastTime());
                    heathMonitor.setHeathStatus(h.getHeathStatus());
                    heathMonitors.add(heathMonitor);

                }
                heathMonitorService.updateRecord(heathMonitors);
                if (logInfoList.size() > 0) {
                    logInfoService.saveRecord(logInfoList);
                }
                /*
                for (HeathMonitor h : heathMonitorAllList) {
                    int status = 500;
                    status = restUtil.get(h.getHeathUrl());
                    h.setCreateTime(date);
                    h.setHeathStatus(status + "");
                    heathMonitors.add(h);
                    if (!"200".equals(h.getHeathStatus())) {
                        if ((WarnPools.API_WARN_MAP.get(h.getId())) != null) {
                            continue;
                        }
                        LogInfo logInfo = new LogInfo();
                        logInfo.setHostname("服务接口检测异常：" + h.getAppName());
                        logInfo.setInfoContent("服务接口检测异常：" + h.getAppName() + "，" + h.getHeathUrl() + "，返回状态" + h.getHeathStatus());
                        logInfo.setState(StaticKeys.LOG_ERROR);
                        logInfoList.add(logInfo);
                        Runnable runnable = () -> {
                            WarnMailUtil.sendHeathInfo(h, true);
                        };
                        executor.execute(runnable);
                    } else {
                        if ((WarnPools.API_WARN_MAP.get(h.getId()))!= null) {
                            Runnable runnable = () -> {
                                WarnMailUtil.sendHeathInfo(h, false);
                            };
                            executor.execute(runnable);
                        }
                    }
                }

                heathMonitorService.updateRecord(heathMonitors);
                if (logInfoList.size() > 0) {
                    logInfoService.saveRecord(logInfoList);
                }
                */
            }
        } catch (Exception e) {
            logger.error("服务接口检测任务错误", e);
            logInfoService.save("服务接口检测错误", e.toString(), StaticKeys.LOG_ERROR);
        }
    }


    /**
     * 60秒后执行，之后每隔10分钟执行, 单位：ms。
     * 数据表监控
     */
    @Scheduled(initialDelay = 60000L, fixedDelayString = "${base.dbTableTimes}")
    public void tableCountTask() {
        Map<String, Object> params = new HashMap<>();
        List<DbTable> dbTablesUpdate = new ArrayList<DbTable>();
        List<DbTableCount> dbTableCounts = new ArrayList<DbTableCount>();
        Date date = DateUtil.getNowTime();
        String sql = "";
        Long tableCount = 0l;
        try {
            List<DbInfo> dbInfos = dbInfoService.selectAllByParams(params);
            for (DbInfo dbInfo : dbInfos) {
                params.put("dbInfoId", dbInfo.getId());
                List<DbTable> dbTables = dbTableService.selectAllByParams(params);
                for (DbTable dbTable : dbTables) {

                    if(dbTable.getInUse()!=1)continue;//不启动
                    if(dbTable.getSql() != null && dbTable.getSql().length() > 10){
                        sql=dbTable.getSql();//直接使用sql
                    }
                    else{
                        String whereAnd = "";
                        if (!StringUtils.isEmpty(dbTable.getWhereVal())) {
                            whereAnd = " and ";
                        }
                        if ("postgresql".equals(dbInfo.getDbType())) {
                            sql = RDSConnection.query_table_count_pg.replace("{tableName}", dbTable.getTableName()) + whereAnd + dbTable.getWhereVal();
                        } else {
                            sql = RDSConnection.query_table_count.replace("{tableName}", dbTable.getTableName()) + whereAnd + dbTable.getWhereVal();
                        }
                    }
                    tableCount = connectionUtil.queryTableCount(dbInfo, sql);
                    DbTableCount dbTableCount = new DbTableCount();
                    dbTableCount.setCreateTime(date);
                    dbTableCount.setDbTableId(dbTable.getId());
                    dbTableCount.setTableCount(tableCount);
                    dbTableCounts.add(dbTableCount);
                    dbTable.setDateStr(DateUtil.getDateTimeString(date));
                    dbTable.setTableCount(tableCount);
                    dbTablesUpdate.add(dbTable);
                    if((dbTable.getWarnCountL() != null && tableCount < dbTable.getWarnCountL()) ||
                            (dbTable.getWarnCountH() != null && tableCount > dbTable.getWarnCountH())){
                            //报警
                        String info ="无";
                        dbTable.setWarnCount(dbTable.getWarnCount()+1);
                        if(dbTable.getfSql()!= null && dbTable.getfSql().length() > 15){
                            info = connectionUtil.queryTableString(dbInfo,dbTable.getfSql());
                        }
                        WarnMailUtil.sendDbTableDataCountError(dbInfo,dbTable,info);

                    }
                    else{

                        if(dbTable.getWarnCount() > 0){
                            dbTable.setWarnCount(0);
                            WarnMailUtil.sendDbTableDataCountRight(dbInfo,dbTable);
                        }
                    }

                }
            }
            if (dbTableCounts.size() > 0) {
                dbTableCountService.saveRecord(dbTableCounts);
                dbTableService.updateRecord(dbTablesUpdate);
            }
        } catch (Exception e) {
            logger.error("数据表监控任务错误", e);
            if(StaticKeys.mailSet != null)WarnMailUtil.sendMail(StaticKeys.mailSet.getToMail(),"数据库异常:"+e.getMessage(),e.toString());
            logInfoService.save("数据表监控任务错误", e.toString(), StaticKeys.LOG_ERROR);

        }
    }


    public static Set<String> whiteIP = new ConcurrentHashSet<>();
    private static Map<String,Integer> cntIP = new ConcurrentHashMap<>();

    private static NetStateMapper pNetStateMapper = null;

    public static void updateWhiteIP(){
        whiteIP.clear();
        List<NetState> list =  pNetStateMapper.selectAllData();
        for(NetState netState : list){
            whiteIP.add(netState.getBip()+":"+netState.getBport());
        }

    }

    private  void addIpPort(String ip,Long port){
        String ipPort = ip+":"+port;
        Integer ct = cntIP.get(ipPort);
        if(ct == null){
            ct = 1;
        }
        else{
            ct++;
        }
        if(ct > 10){
            whiteIP.add(ipPort);
            cntIP.remove(ipPort);
            //更新数据库
            NetState netState = new NetState();
            netState.setBip(ip);
            netState.setBport(port.intValue());
            netState.setBr(2);
            netStateMapper.save(netState);

        }
        else{
            cntIP.put(ipPort,ct);
        }
    }


    /**
     * 30秒后执行，之后每隔1分钟执行, 单位：ms。
     * 批量提交数据
     */
    @Scheduled(initialDelay = 30000L, fixedRate = 1 * 60 * 1000)
    public synchronized void commitTask() {
        logger.info("批量提交监控数据任务开始----------" + DateUtil.getCurrentDateTime());
        try {
            if (BatchData.APP_STATE_LIST.size() > 0) {
                List<AppState> APP_STATE_LIST = new ArrayList<AppState>();
                APP_STATE_LIST.addAll(BatchData.APP_STATE_LIST);
                BatchData.APP_STATE_LIST.clear();
                appStateService.saveRecord(APP_STATE_LIST);
            }
            if (BatchData.CPU_STATE_LIST.size() > 0) {
                List<CpuState> CPU_STATE_LIST = new ArrayList<CpuState>();
                CPU_STATE_LIST.addAll(BatchData.CPU_STATE_LIST);
                BatchData.CPU_STATE_LIST.clear();
                cpuStateService.saveRecord(CPU_STATE_LIST);
            }
            if (BatchData.MEM_STATE_LIST.size() > 0) {
                List<MemState> MEM_STATE_LIST = new ArrayList<MemState>();
                MEM_STATE_LIST.addAll(BatchData.MEM_STATE_LIST);
                BatchData.MEM_STATE_LIST.clear();
                memStateService.saveRecord(MEM_STATE_LIST);
            }
            //更新ip和端口的白名单
            if(BatchData.NET_STATE_LIST.size() > 0){
                try {
                    if(pNetStateMapper == null){
                        pNetStateMapper = netStateMapper;
                        updateWhiteIP();
                    }

                    List<NetConnetInfo> NET_STATE_LIST = new ArrayList<NetConnetInfo>();
                    NET_STATE_LIST.addAll(BatchData.NET_STATE_LIST);
                    BatchData.NET_STATE_LIST.clear();
                    for (NetConnetInfo netConnetInfo : NET_STATE_LIST) {
                        if (netConnetInfo.getmList() == null || netConnetInfo.getmList().isEmpty() ||
                                (!netConnetInfo.getXuexi())) {
                            continue;
                        }
                        List<NetConnetItem> items = netConnetInfo.getmList();
                        for (NetConnetItem item : items) {
                            String local = item.getLocalAddress() + ":" + item.getLocalPort();
                            String remoet = item.getRemoteAddress() + ":" + item.getRemotePort();
                            if (whiteIP.contains(local) || whiteIP.contains(remoet)) {
                                cntIP.remove(local);
                                cntIP.remove(remoet);
                                continue;
                            }
                            addIpPort(item.getRemoteAddress(), item.getRemotePort());
                            addIpPort(item.getLocalAddress(), item.getLocalPort());

                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            if (BatchData.NETIO_STATE_LIST.size() > 0) {
                List<NetIoState> NETIO_STATE_LIST = new ArrayList<NetIoState>();
                NETIO_STATE_LIST.addAll(BatchData.NETIO_STATE_LIST);
                BatchData.NETIO_STATE_LIST.clear();
                netIoStateService.saveRecord(NETIO_STATE_LIST);
            }
            if (BatchData.SYSLOAD_STATE_LIST.size() > 0) {
                List<SysLoadState> SYSLOAD_STATE_LIST = new ArrayList<SysLoadState>();
                SYSLOAD_STATE_LIST.addAll(BatchData.SYSLOAD_STATE_LIST);
                BatchData.SYSLOAD_STATE_LIST.clear();
                sysLoadStateService.saveRecord(SYSLOAD_STATE_LIST);
            }
                /*if(BatchData.TCP_STATE_LIST.size()>0){
                    tcpStateService.saveRecord(BatchData.TCP_STATE_LIST);
                }
                if(BatchData.INTRUSION_INFO_LIST.size()>0){
                    intrusionInfoService.saveRecord(BatchData.INTRUSION_INFO_LIST);
                }*/
            if (BatchData.LOG_INFO_LIST.size() > 0) {
                List<LogInfo> LOG_INFO_LIST = new ArrayList<LogInfo>();
                LOG_INFO_LIST.addAll(BatchData.LOG_INFO_LIST);
                BatchData.LOG_INFO_LIST.clear();
                logInfoService.saveRecord(LOG_INFO_LIST);
            }
            if (BatchData.DESK_STATE_LIST.size() > 0) {
                Map<String, Object> paramsDel = new HashMap<String, Object>();

                List<DeskState> DESK_STATE_LIST = new ArrayList<DeskState>();
                DESK_STATE_LIST.addAll(BatchData.DESK_STATE_LIST);
                BatchData.DESK_STATE_LIST.clear();
                List<String> hostnameList = new ArrayList<String>();
                for (DeskState deskState : DESK_STATE_LIST) {
                    if (!hostnameList.contains(deskState.getHostname())) {
                        hostnameList.add(deskState.getHostname());
                    }
                }
                for (String hostname : hostnameList) {
                    paramsDel.put("hostname", hostname);
                    deskStateService.deleteByAccHname(paramsDel);
                }
                deskStateService.saveRecord(DESK_STATE_LIST);
            }
            if (BatchData.SYSTEM_INFO_LIST.size() > 0) {
                Map<String, Object> paramsDel = new HashMap<String, Object>();
                List<SystemInfo> SYSTEM_INFO_LIST = new ArrayList<SystemInfo>();
                SYSTEM_INFO_LIST.addAll(BatchData.SYSTEM_INFO_LIST);
                BatchData.SYSTEM_INFO_LIST.clear();
                List<SystemInfo> updateList = new ArrayList<SystemInfo>();
                List<SystemInfo> insertList = new ArrayList<SystemInfo>();
                List<SystemInfo> savedList = systemInfoService.selectAllByParams(paramsDel);
                for (SystemInfo systemInfo : SYSTEM_INFO_LIST) {
                    boolean issaved = false;
                    for (SystemInfo systemInfoS : savedList) {
                        if (systemInfoS.getHostname().equals(systemInfo.getHostname())) {
                            systemInfo.setId(systemInfoS.getId());
                            updateList.add(systemInfo);
                            issaved = true;
                            break;
                        }
                    }
                    if (!issaved) {
                        insertList.add(systemInfo);
                    }
                }
                systemInfoService.updateRecord(updateList);
                systemInfoService.saveRecord(insertList);
            }
            if (BatchData.APP_INFO_LIST.size() > 0) {
                Map<String, Object> paramsDel = new HashMap<String, Object>();
                List<AppInfo> APP_INFO_LIST = new ArrayList<AppInfo>();
                APP_INFO_LIST.addAll(BatchData.APP_INFO_LIST);
                BatchData.APP_INFO_LIST.clear();

                List<AppInfo> updateList = new ArrayList<AppInfo>();
                List<AppInfo> insertList = new ArrayList<AppInfo>();
                List<AppInfo> savedList = appInfoService.selectAllByParams(paramsDel);
                for (AppInfo systemInfo : APP_INFO_LIST) {
                    boolean issaved = false;
                    for (AppInfo systemInfoS : savedList) {
                        if (systemInfoS.getHostname().equals(systemInfo.getHostname()) && systemInfoS.getAppPid().equals(systemInfo.getAppPid())) {
                            systemInfo.setId(systemInfoS.getId());
                            updateList.add(systemInfo);
                            issaved = true;
                            break;
                        }
                    }
                    if (!issaved) {
                        insertList.add(systemInfo);
                    }
                }
                appInfoService.updateRecord(updateList);
                appInfoService.saveRecord(insertList);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("批量提交监控数据错误----------", e);
            logInfoService.save("commitTask", "批量提交监控数据错误：" + e.toString(), StaticKeys.LOG_ERROR);
        }
        logger.info("批量提交监控数据任务结束----------" + DateUtil.getCurrentDateTime());
    }

    @Autowired
    SystemInfoMapper systemInfoMapper;
    @Autowired
    CpuStateMapper cpuStateMapper;
    @Autowired
    DeskStateMapper deskStateMapper;
    @Autowired
    MemStateMapper memStateMapper;
    @Autowired
    NetIoStateMapper netIoStateMapper;
    @Autowired
    SysLoadStateMapper sysLoadStateMapper;
    @Autowired
    TcpStateMapper tcpStateMapper;
    @Autowired
    AppInfoMapper appInfoMapper;
    @Autowired
    AppStateMapper appStateMapper;
    @Autowired
    MailSetMapper mailSetMapper;
    @Autowired
    IntrusionInfoMapper intrusionInfoMapper;
    @Autowired
    LogInfoMapper logInfoMapper;

    /**
     * 每天凌晨1:10执行
     * 删除历史数据，15天
     */
    @Scheduled(cron = "0 10 1 * * ?")
    public void clearHisdataTask() {
        logger.info("定时清空历史数据任务开始----------" + DateUtil.getCurrentDateTime());
        Runnable runnable = () -> {
            WarnMailUtil.sendTotalInfo();
        };
        executor.execute(runnable);
        WarnPools.clearOldData();//清空发告警邮件的记录
        String nowTime = DateUtil.getCurrentDateTime();
        //15天前时间
        String thrityDayBefore = DateUtil.getDateBefore(nowTime, 300);
        Map<String, Object> paramsDel = new HashMap<String, Object>();
        try {
            paramsDel.put(StaticKeys.SEARCH_END_TIME, thrityDayBefore);
            cntIP.clear();
            //执行删除操作begin
            if (paramsDel.get(StaticKeys.SEARCH_END_TIME) != null && !"".equals(paramsDel.get(StaticKeys.SEARCH_END_TIME))) {
                cpuStateMapper.deleteByAccountAndDate(paramsDel);//删除cpu监控信息
                deskStateMapper.deleteByAccountAndDate(paramsDel);//删除磁盘监控信息
                memStateMapper.deleteByAccountAndDate(paramsDel);//删除内存监控信息
                netIoStateMapper.deleteByAccountAndDate(paramsDel);//删除吞吐率监控信息
                sysLoadStateMapper.deleteByAccountAndDate(paramsDel);//删除负载状态监控信息
                tcpStateMapper.deleteByAccountAndDate(paramsDel);//删除tcp监控信息
                appInfoMapper.deleteByDate(paramsDel);
                appStateMapper.deleteByDate(paramsDel);
                systemInfoMapper.deleteByAccountAndDate(paramsDel);
                intrusionInfoMapper.deleteByAccountAndDate(paramsDel);
                //删除15天前的日志信息
                logInfoMapper.deleteByDate(paramsDel);
                //删除15天前数据库表统计信息
                dbTableCountService.deleteByDate(paramsDel);

                logInfoService.save("定时清空历史数据完成", "定时清空历史数据完成：", StaticKeys.LOG_ERROR);
            }
            //执行删除操作end

        } catch (Exception e) {
            logger.error("定时清空历史数据任务出错：", e);
            logInfoService.save("定时清空历史数据错误", "定时清空历史数据错误：" + e.toString(), StaticKeys.LOG_ERROR);
        }
        logger.info("定时清空历史数据任务结束----------" + DateUtil.getCurrentDateTime());
    }


}
