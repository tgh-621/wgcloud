package com.wgcloud.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wgcloud.entity.GroupInfo;
import com.wgcloud.mapper.GroupInfoMapper;
import com.wgcloud.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:LogInfoService.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: LogInfoService.java
 *
 */
@Service
public class GroupInfoService {

    private static final Logger logger = LoggerFactory.getLogger(GroupInfoService.class);

    public PageInfo selectByParams(Map<String, Object> params, int currPage, int pageSize) throws Exception {
        PageHelper.startPage(currPage, pageSize);
        List<GroupInfo> list = groupInfoMapper.selectByParams(params);
        PageInfo<GroupInfo> pageInfo = new PageInfo<GroupInfo>(list);
        return pageInfo;
    }

    public void saveRecord(List<GroupInfo> recordList) throws Exception {
        if (recordList.size() < 1) {
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for (GroupInfo as : recordList) {
            as.setId(UUIDUtil.getUUID());
        }
        groupInfoMapper.insertList(recordList);
    }

    public void save(String name) {
        GroupInfo logInfo = new GroupInfo();
        logInfo.setName(name);
        logInfo.setId(UUIDUtil.getUUID());
        try {
            groupInfoMapper.save(logInfo);
        } catch (Exception e) {
            logger.error("保存日志信息异常：", e);
        }
    }

    public int countByParams(Map<String, Object> params) throws Exception {
        return groupInfoMapper.countByParams(params);
    }

    public int deleteById(String[] id) throws Exception {
        return groupInfoMapper.deleteById(id);
    }

    public GroupInfo selectById(String id) throws Exception {
        return groupInfoMapper.selectById(id);
    }

    public List<GroupInfo> selectAllByParams(Map<String, Object> params) throws Exception {
        return groupInfoMapper.selectAllByParams(params);
    }
    public List<GroupInfo> selectAll() throws Exception {
        return groupInfoMapper.selectByAccountId(null);
    }

    @Autowired
    private GroupInfoMapper groupInfoMapper;


}
