package com.fibo.ddp.common.service.strategyx.guiderule.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fibo.ddp.common.dao.canal.TableEnum;
import com.fibo.ddp.common.dao.datax.datamanage.FieldMapper;
import com.fibo.ddp.common.dao.strategyx.guiderule.RuleVersionMapper;
import com.fibo.ddp.common.model.authx.system.SysUser;
import com.fibo.ddp.common.model.common.requestParam.StatusParam;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleBlock;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleConditionInfo;
import com.fibo.ddp.common.model.strategyx.guiderule.RuleVersion;
import com.fibo.ddp.common.model.strategyx.guiderule.vo.RuleBlockVo;
import com.fibo.ddp.common.model.strategyx.guiderule.vo.RuleConditionVo;
import com.fibo.ddp.common.model.strategyx.guiderule.vo.RuleVersionVo;
import com.fibo.ddp.common.model.strategyx.strategyout.StrategyOutput;
import com.fibo.ddp.common.service.common.SessionManager;
import com.fibo.ddp.common.service.redis.RedisManager;
import com.fibo.ddp.common.service.redis.RedisUtils;
import com.fibo.ddp.common.service.strategyx.guiderule.RuleBlockService;
import com.fibo.ddp.common.service.strategyx.guiderule.RuleConditionService;
import com.fibo.ddp.common.service.strategyx.guiderule.RuleVersionService;
import com.fibo.ddp.common.service.strategyx.strategyout.StrategyOutputService;
import com.fibo.ddp.common.utils.constant.Constants;
import com.fibo.ddp.common.utils.constant.strategyx.StrategyType;
import com.fibo.ddp.common.utils.util.strategyx.CustomValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RuleVersionServiceImpl extends ServiceImpl<RuleVersionMapper, RuleVersion> implements RuleVersionService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RuleVersionMapper versionMapper;
    @Autowired
    private RuleBlockService ruleBlockService;
    @Autowired
    private RuleConditionService conditionService;
    @Autowired
    private StrategyOutputService outputService;
    @Autowired
    private FieldMapper fieldMapper;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private RedisManager redisManager;
    @Value("${switch.use.cache}")
    private String cacheSwitch;

//    @Override
//    public RuleVersionVo queryById(Long id) {
//        RuleVersion ruleVersion = versionMapper.selectById(id);
//        RuleVersionVo result = new  RuleVersionVo();
//        if (ruleVersion==null){
//            return result;
//        }
//        BeanUtils.copyProperties(ruleVersion,result);
//        //查询ruleCondition组装成树形结构
//        RuleConditionVo ruleConditionVo = conditionService.queryByVersionId(id);
//        List<StrategyOutput> strategyOutputList = outputService.queryByTactics(new StrategyOutput(id, StrategyType.COMPLEX_RULE, StrategyType.OutType.SUCCESS_OUT));
//        List<StrategyOutput>  failOutputList = outputService.queryByTactics(new StrategyOutput(id, StrategyType.COMPLEX_RULE, StrategyType.OutType.FAIL_OUT));
//        result.setRuleConditionVo(ruleConditionVo);
//        result.setStrategyOutputList(strategyOutputList);
//        result.setFailOutputList(failOutputList);
//        return result;
//    }

    @Override
    public RuleVersionVo queryById(Long id) {
        RuleVersion ruleVersion = null;
        if (Constants.switchFlag.ON.equals(cacheSwitch)) {
            String key = RedisUtils.getPrimaryKey(TableEnum.T_RULE_VERSION, id);
            ruleVersion = redisManager.getByPrimaryKey(key, RuleVersion.class);
        } else {
            ruleVersion = versionMapper.selectById(id);
        }

        RuleVersionVo result = new RuleVersionVo();
        if (ruleVersion == null) {
            return result;
        }
        BeanUtils.copyProperties(ruleVersion, result);

        List<RuleBlock> ruleBlockList = ruleBlockService.listObjectsByVersionId(id);
        List<RuleBlockVo> ruleBlockVoList = new ArrayList<>();
        for(RuleBlock ruleBlock : ruleBlockList){
            RuleBlockVo ruleBlockVo = new RuleBlockVo();
            BeanUtils.copyProperties(ruleBlock, ruleBlockVo);
            //查询ruleCondition组装成树形结构
            RuleConditionVo ruleConditionVo = conditionService.queryByBlockId(ruleBlock.getId());
            List<StrategyOutput> strategyOutputList = outputService.queryByTactics(new StrategyOutput(ruleBlock.getId(), StrategyType.COMPLEX_RULE, StrategyType.OutType.SUCCESS_OUT));
            List<StrategyOutput>  failOutputList = outputService.queryByTactics(new StrategyOutput(ruleBlock.getId(), StrategyType.COMPLEX_RULE, StrategyType.OutType.FAIL_OUT));
            ruleBlockVo.setRuleConditionVo(ruleConditionVo);
            ruleBlockVo.setStrategyOutputList(strategyOutputList);
            ruleBlockVo.setFailOutputList(failOutputList);
            ruleBlockVoList.add(ruleBlockVo);
        }
        result.setRuleBlockVoList(ruleBlockVoList);

        return result;
    }

//    @Override
//    public List<RuleVersionVo> queryVersionListByRuleId(Long RuleId) {
//        LambdaQueryWrapper<RuleVersion> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(RuleVersion::getRuleId,RuleId);
//        queryWrapper.eq(RuleVersion::getStatus,1);
//        queryWrapper.orderByDesc(RuleVersion::getId);
//        List<RuleVersion> ruleVersionList = versionMapper.selectList(queryWrapper);
//        List<RuleVersionVo> ruleVersionVoList = new ArrayList<>();
//        for (RuleVersion ruleVersion :  ruleVersionList) {
//            RuleVersionVo versionVo = new RuleVersionVo();
//            BeanUtils.copyProperties(ruleVersion,versionVo);
//            ruleVersionVoList.add(versionVo);
//        }
//        return ruleVersionVoList;
//    }

    @Override
    public List<RuleVersionVo> queryVersionListByRuleId(Long ruleId) {
        LambdaQueryWrapper<RuleVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RuleVersion::getRuleId,ruleId);
        queryWrapper.eq(RuleVersion::getStatus,1);
        queryWrapper.orderByDesc(RuleVersion::getUpdateTime);
        List<RuleVersion> ruleVersionList = versionMapper.selectList(queryWrapper);
        List<RuleVersionVo> ruleVersionVoList = new ArrayList<>();
        for (RuleVersion ruleVersion :  ruleVersionList) {
            RuleVersionVo versionVo = new RuleVersionVo();
            BeanUtils.copyProperties(ruleVersion,versionVo);
            ruleVersionVoList.add(versionVo);
        }
        return ruleVersionVoList;
    }

    @Override
    public List<String> queryFieldEnByVersionId(Long versionId) {
        Set<String> fieldEns = new HashSet<>();
        Set<Long> fieldIds = new HashSet<>();
        List<RuleBlock> ruleBlockList = ruleBlockService.listObjectsByVersionId(versionId);
        for (RuleBlock ruleBlock : ruleBlockList) {
            RuleConditionVo ruleConditionVo = conditionService.queryByBlockId(ruleBlock.getId());
            List<RuleConditionInfo> ruleConditionInfoList = conditionService.disassemble(ruleConditionVo, ruleBlock.getRuleId(), false);
            for (RuleConditionInfo info : ruleConditionInfoList) {
                if (info.getFieldEn() != null && info.getFieldType() != 1 && !info.getFieldEn().startsWith("%")) {
                    if (info.getFieldEn().contains(".")) {
                        fieldEns.add(info.getFieldEn().split("\\.")[0]);
                    } else {
                        fieldEns.add(info.getFieldEn());
                    }
                } else if (info.getFieldId() != null) {
                    fieldIds.add(info.getFieldId());
                }
                if (info.getVariableType() != null) {
                    if (info.getVariableType() == 2 && info.getFieldValue() != null && !info.getFieldValue().contains("%")) {
                        fieldEns.add(info.getFieldValue());
                    } else if (info.getVariableType() == 3) {
                        fieldEns.addAll(CustomValueUtils.getFieldEnSet(info.getFieldValue()));
                    }
                }
            }
        }
        for (Long fieldId : fieldIds) {
            String fieldName = fieldMapper.findFieldNameById(fieldId);
            fieldEns.add(fieldName);
        }
        return new ArrayList<>(fieldEns);
    }

    @Override
    @Transactional
    public int addVersionList(List<RuleVersionVo> versionList) {
        int result = 0;
        for (RuleVersionVo versionVo : versionList) {
            boolean b = addVersion(versionVo);
            if (b){
                result++;
            }
        }
        return result;
    }

    @Override
    @Transactional
    public boolean addVersion(RuleVersionVo version) {
        SysUser loginSysUser = SessionManager.getLoginAccount();
        version.setOrganId(loginSysUser.getOrganId());
        version.setCreateUserId(loginSysUser.getUserId());
        version.setUpdateUserId(loginSysUser.getUserId());
        version.setCreateTime(null);
        version.setUpdateTime(null);
        version.setStatus(1);
        if (version.getVersionCode()==null){
            version.setVersionCode("V:0");
        }
        if (version.getDescription()==null){
            version.setDescription("初始版本");
        }
        int insert = versionMapper.insert(version);
        if (insert>0){
            this.addVersionDetail(version);
            this.saveSnapshot(version.getId());
            return true;
        }else {
            logger.error("新增规则版本失败{}",version);
        }
        return false;
    }

    @Transactional
    public boolean addVersionDetail(RuleVersionVo version){
        List<RuleBlockVo> ruleBlockVoList = version.getRuleBlockVoList();
        for (RuleBlockVo ruleBlockVo : ruleBlockVoList){
            RuleBlock ruleBlock = new RuleBlock();
            BeanUtils.copyProperties(ruleBlockVo, ruleBlock);
            ruleBlock.setRuleId(version.getRuleId());
            ruleBlock.setVersionId(version.getId());
            ruleBlockService.save(ruleBlock);

            RuleConditionVo ruleConditionVo = ruleBlockVo.getRuleConditionVo();
            ruleConditionVo.setVersionId(version.getId());
            ruleConditionVo.setBlockId(ruleBlock.getId());
            //添加条件信息
            conditionService.insertRuleCondition(ruleConditionVo,version.getRuleId(), ruleBlock.getId());
            //添加输出字段
            List<StrategyOutput> strategyOutputList = ruleBlockVo.getStrategyOutputList();
            if (strategyOutputList !=null&& strategyOutputList.size()>0){
                outputService.insertTacticsOutput(ruleBlock.getId(), strategyOutputList);
            }
            List<StrategyOutput> failOutputList = ruleBlockVo.getFailOutputList();
            if (failOutputList!=null&&failOutputList.size()>0){
                outputService.insertTacticsOutput(ruleBlock.getId(),failOutputList );
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean copyVersion(RuleVersionVo version) {
        RuleVersionVo versionVo = this.queryById(version.getId());
        versionVo.setId(null);
        versionVo.setVersionCode(version.getVersionCode());
        versionVo.setDescription(version.getDescription());
        return this.addVersion(versionVo);
    }

    @Override
    @Transactional
    public boolean updateVersion(RuleVersionVo version) {
        Long ruleId = version.getRuleId();
        Long versionId = version.getId();
        if (versionId==null){
            return false;
        }
        SysUser loginSysUser = SessionManager.getLoginAccount();
        version.setUpdateUserId(loginSysUser.getUserId());
        // 1. 修改版本主表
        versionMapper.updateById(version);

        // 2. 删除原版本下所有子表数据
        // 删除所有规则块
        ruleBlockService.removeObjects(ruleId, versionId);
        // 删除所有条件
        conditionService.deleteRuleCondition(ruleId, versionId);
        // 删除所有策略输出（需找出原来的所有块，然后逐一删除）
        List<RuleBlock> oldRuleBlockList = ruleBlockService.listObjectsByVersionId(versionId);
        for(RuleBlock ruleBlock : oldRuleBlockList){
            StrategyOutput strategyOutput = new StrategyOutput(ruleBlock.getId(), StrategyType.COMPLEX_RULE);
            outputService.deleteByTactics(strategyOutput);
        }

        // 3. 插入新版本下子表数据
        List<RuleBlockVo> ruleBlockVoList = version.getRuleBlockVoList();
        for(RuleBlockVo ruleBlockVo : ruleBlockVoList){
            // 修改规则块配置表
            ruleBlockVo.setRuleId(ruleId);
            ruleBlockVo.setVersionId(versionId);
            ruleBlockService.save(ruleBlockVo);
            Long blockId = ruleBlockVo.getId();
            //修改条件表
            conditionService.updateRuleCondition(ruleId, blockId, ruleBlockVo.getRuleConditionVo());
            //修改策略输出
            outputService.updateTacticsOutput(blockId,ruleBlockVo.getStrategyOutputList(),ruleBlockVo.getFailOutputList(), StrategyType.COMPLEX_RULE);
        }

        this.saveSnapshot(versionId);
        return true;
    }

    @Override
    @Transactional
    public boolean updateStatus(StatusParam statusParam) {
        LambdaQueryWrapper<RuleVersion> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.in(RuleVersion::getId,statusParam.getIds());
        updateWrapper.eq(RuleVersion::getRuleId,statusParam.getStrategyId());
        RuleVersion ruleVersion = new RuleVersion();
        ruleVersion.setStatus(statusParam.getStatus());
        boolean update = this.update(ruleVersion, updateWrapper);
        return update;
    }


    private boolean saveSnapshot(Long versionId){
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LambdaUpdateWrapper<RuleVersion> wrapper = new  LambdaUpdateWrapper<>();
                RuleVersionVo versionVo = queryById(versionId);
                versionVo.setSnapshot(null);
                wrapper.eq(RuleVersion::getId,versionId).set(RuleVersion::getSnapshot, JSON.toJSONString(versionVo));
                versionMapper.update(null,wrapper);
            }
        });
        return true;
    }
}
