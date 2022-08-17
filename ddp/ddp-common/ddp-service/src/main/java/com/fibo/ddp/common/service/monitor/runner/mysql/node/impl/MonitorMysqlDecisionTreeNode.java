package com.fibo.ddp.common.service.monitor.runner.mysql.node.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fibo.ddp.common.dao.monitor.decisionflow.TMonitorStrategyMapper;
import com.fibo.ddp.common.model.monitor.decisionflow.TMonitorNode;
import com.fibo.ddp.common.model.monitor.decisionflow.TMonitorStrategy;
import com.fibo.ddp.common.service.monitor.runner.mysql.node.MonitorMysqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MonitorMysqlDecisionTreeNode implements MonitorMysqlService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorMysqlDecisionTreeNode.class);
    @Autowired
    private TMonitorStrategyMapper monitorStrategyMapper;
    @Override
    public void createMonitorStrategy(TMonitorNode monitorNode, Map<String, Object> outMap) {
        //根据快照中数据的个数进行确定存取条数。目前决策表只能选择一个，此处数组是预留防止以后多个的情况
        String decisionTreeStrategyIdKey = "decisionTreeStrategy-"+monitorNode.getNodeId();
        //决策表
        logger.info("MonitorMysqlDecisionTreeNode============================「监控中心-策略监控信息」参数:{},{}",monitorNode, JSONObject.toJSONString(outMap.get(decisionTreeStrategyIdKey)));
        if(!outMap.containsKey(decisionTreeStrategyIdKey)){
            return;
        }
        JSONArray jsonArray = JSONArray.parseArray(JSONObject.parseObject(outMap.get(decisionTreeStrategyIdKey)+"").get("snapshot")+"");
        outMap.remove(decisionTreeStrategyIdKey);
        for (Object object:jsonArray) {
            if(object==null){
                continue;
            }
            TMonitorStrategy monitorStrategy1 = new TMonitorStrategy();
            //1.节点监控JSONObject信息中拿出策略层面的信息
            JSONObject jsonObject = JSONObject.parseObject(object+"");
            //策略快照
            monitorStrategy1.setSnapshot(jsonObject.toString());
            //全量入参
            monitorStrategy1.setInput(monitorNode.getInput());
            //决策表结果
            monitorStrategy1.setOutput(monitorNode.getOutput());
            logger.info("MonitorMysqlDecisionTreeNode============================「监控中心-策略监控信息」monitorInfo:{}",monitorStrategy1);
            //策略ID 即 决策表版本id
            String ruleVersionId = jsonObject.get("id")+"";
            monitorStrategy1.setStrategyId(Long.valueOf(ruleVersionId));
            //策略名称 决策表名称
            monitorStrategy1.setStrategyName(JSONObject.parseObject(monitorNode.getSnapshot()).getString("name"));
            //策略类型
            monitorStrategy1.setStrategyType(monitorNode.getNodeType());
            //策略版本
            monitorStrategy1.setStrategyVersionCode(jsonObject.getString("versionCode"));
            //业务id
            monitorStrategy1.setBusinessId(monitorNode.getBusinessId());
            //节点id
            monitorStrategy1.setNodeId(monitorNode.getNodeId());
            //节点类型
            monitorStrategy1.setNodeType(monitorNode.getNodeType());
            //引擎版本id
            monitorStrategy1.setEngineVersionId(monitorNode.getEngineVersionId());
            logger.info("MonitorMysqlDecisionTreeNode============================「监控中心-策略监控信息」baseInfo:{}",monitorStrategy1);
            //组织id
            monitorStrategy1.setOrganId(monitorNode.getOrganId());
            //时间
            monitorStrategy1.setCreateTime(LocalDateTime.now());
            monitorStrategy1.setMonitorParentId(monitorNode.getId()+"");
            monitorStrategy1.setEngineId(monitorNode.getEngineId());
            monitorStrategyMapper.insert(monitorStrategy1);
        }
    }
}
