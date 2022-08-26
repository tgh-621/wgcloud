package com.wgcloud.mapper;

import com.wgcloud.entity.GroupInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:AppInfoDao.java
 * @author: http://www.bigdatacd.com
 * @date: 2019年11月16日
 * @Description: AppInfoDao.java
 *
 */
@Repository
public interface GroupInfoMapper {

    public List<GroupInfo> selectAllByParams(Map<String, Object> map) throws Exception;

    public List<GroupInfo> selectByParams(Map<String, Object> params) throws Exception;

    public GroupInfo selectById(String id) throws Exception;

    public List<GroupInfo> selectByAccountId(String accountId) throws Exception;

    public void save(GroupInfo AppInfo) throws Exception;

    public void insertList(List<GroupInfo> recordList) throws Exception;

    public void updateList(List<GroupInfo> recordList) throws Exception;

    public int deleteById(String[] id) throws Exception;

    public int deleteByName(Map<String, Object> map) throws Exception;

    public int deleteByDate(Map<String, Object> map) throws Exception;

    public int countByParams(Map<String, Object> params) throws Exception;

    public int updateById(GroupInfo AppInfo) throws Exception;
}
