package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.AppInfo;
import com.wgcloud.entity.CmdInfo;
import com.wgcloud.mapper.AppInfoMapper;
import com.wgcloud.mapper.AppStateMapper;
import com.wgcloud.mapper.CmdInfoMapper;
import com.wgcloud.util.DateUtil;
import com.wgcloud.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:AppInfoService.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: AppInfoService.java
 *
 */
@Service
public class CmdInfoService {

    public PageInfo selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<CmdInfo> list = cmdInfoMapper.selectByParams(params);
        PageInfo<CmdInfo> pageInfo = new PageInfo<CmdInfo>(list);
        return pageInfo;
    }

    public void save(CmdInfo AppInfo) throws Exception {
        if(AppInfo.getId() != null && AppInfo.getId().isEmpty()){
            AppInfo.setId(null);
        }
        AppInfo.setCreateTime(DateUtil.getNowTime());
        cmdInfoMapper.save(AppInfo);
    }

    public int deleteByHostName(Map<String, Object> map) throws Exception {
        return cmdInfoMapper.deleteByHostName(map);
    }

    @Transactional
    public void saveRecord(List<CmdInfo> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        for (CmdInfo as : recordList) {
            as.setId(UUIDUtil.getUUID());
        }
        cmdInfoMapper.insertList(recordList);
    }

    public int countByParams(Map<String, Object> params) throws Exception {
        return cmdInfoMapper.countByParams(params);
    }

    @Transactional
    public int deleteById(String[] id) throws Exception {

        return cmdInfoMapper.deleteById(id);
    }

    @Transactional
    public void updateRecord(List<CmdInfo> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        cmdInfoMapper.updateList(recordList);
    }

    public void updateById(CmdInfo AppInfo)
            throws Exception {
        cmdInfoMapper.updateById(AppInfo);
    }

    public CmdInfo selectById(String id) throws Exception {
        return cmdInfoMapper.selectById(id);
    }

    public List<CmdInfo> selectAllByParams(Map<String, Object> params) throws Exception {
        return cmdInfoMapper.selectAllByParams(params);
    }
    public List<CmdInfo> selectNoExecCmd(String hostname) throws Exception {
        return cmdInfoMapper.selectNoExecCmd(hostname);
    }


    @Autowired
    private CmdInfoMapper cmdInfoMapper;


}
