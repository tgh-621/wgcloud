package com.wgcloud.mapper;

import com.wgcloud.entity.NetState;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface NetStateMapper {


    public List<NetState> selectAllData() ;


    public void save(NetState MemState) ;

    public void insertList(List<NetState> recordList) ;



}
