package com.wgcloud.mapper;

import com.wgcloud.entity.CmdInfo;
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
public interface CmdInfoMapper {

    public List<CmdInfo> selectAllByParams(Map<String, Object> map) throws Exception;
    public List<CmdInfo>  selectNoExecCmd(String hostname) throws Exception;

    public List<CmdInfo> selectByParams(Map<String, Object> params) throws Exception;

    public CmdInfo selectById(String id) throws Exception;

    public List<CmdInfo> selectByAccountId(String accountId) throws Exception;

    public void save(CmdInfo AppInfo) throws Exception;

    public void insertList(List<CmdInfo> recordList) throws Exception;

    public void updateList(List<CmdInfo> recordList) throws Exception;

    public int deleteById(String[] id) throws Exception;

    public int deleteByHostName(Map<String, Object> map) throws Exception;

    public int deleteByDate(Map<String, Object> map) throws Exception;

    public int countByParams(Map<String, Object> params) throws Exception;

    public int updateById(CmdInfo AppInfo) throws Exception;
}
