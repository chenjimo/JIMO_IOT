package jimo.iot.ctrl_module.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.OderLog;
import jimo.iot.ctrl_module.mapper.OderLogMapper;
import jimo.iot.ctrl_module.service.IOderLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Service
public class OderLogServiceImpl extends ServiceImpl<OderLogMapper, OderLog> implements IOderLogService {

    /***
     * 写入控制记录
     * @param oderLog
     * @return
     */
    @Override
    public boolean writeLog(OderLog oderLog) {
        return baseMapper.insert(oderLog)>0;
    }

    /***
     * 模块读取未执行且可以执行的指令，并记录读取时间
     * @param moduleId
     * @return
     */
    @Override
    public OderLog readLog(Integer moduleId) {
        LocalDateTime now = LocalDateTime.now();
        OderLog oderLog = baseMapper.selectOne(Wrappers.<OderLog>lambdaQuery()
                .eq(OderLog::getModuleId, moduleId)
                .eq(OderLog::getStatus, 0)//等待执行的状态
                .isNull(OderLog::getReadTime)//指令暂未被读取
                .le(OderLog::getWriteTime,now) // 小于等于当前时间
                .orderByAsc(OderLog::getWriteTime));//按升序排列先查最早的数据
        if (oderLog != null){
            baseMapper.updateById(new OderLog(oderLog.getId(), now));
        }
        return oderLog;
    }

    /***
     * 根据执行的指令记录ID进行更新status
     * @param oderLogId
     * @param status
     * @return
     */
    @Override
    public boolean updateLog(Integer oderLogId, Integer status) {
        return baseMapper.updateById(new OderLog(oderLogId, status)) > 0;
    }

    /***
     * 用户获取全部指令记录信息（根据status和ReadTime分类为等待中、执行中、执行成功、与执行失败、已撤销）
     * 把该指令记录有关的信息写入
     * 撤销的命令isReadTime: true, status: -1由于撤销会记录撤销时间
     * @param isReadOrder
     * @param status
     * @return
     */
    @Override
    public List<OderLog> getLogs(boolean isReadOrder, Integer status) {
        List<OderLog> oderLogs = new ArrayList<OderLog>();
        if (isReadOrder){//表示查询模块已经度过数据的记录（status执行中0、执行成功1、与执行失败2）
            oderLogs = baseMapper.selectList(
                    Wrappers.<OderLog>lambdaQuery()
                            .eq(OderLog::getStatus, status)
                            .isNotNull(OderLog::getReadTime)
                            .orderByDesc(OderLog::getWriteTime)
            );
        }else {//表示查询模块还未读取数据的记录（status等待中0、撤销-1）
            oderLogs = baseMapper.selectList(
                    Wrappers.<OderLog>lambdaQuery()
                            .eq(OderLog::getStatus, status)
                            .isNull(OderLog::getReadTime)
                            .orderByDesc(OderLog::getWriteTime));
        }
        return oderLogs;
    }

    /***
     * 根据status和ReadTime分类为等待中(0)、执行中(1)、执行成功(2)、与执行失败(3)、已撤销（4）,判断是否可以撤销这条指令！
     * @param oderLogId
     * @return (大于0则表示不可以再撤销了)
     */
    @Override
    public Integer isUpdate(Integer oderLogId) {
        int idUpa  = 4;
        OderLog oderLog = baseMapper.selectById(oderLogId);
        if (oderLog != null){
            if (oderLog.getReadTime()!=null){//这里已经表示不可以在进行撤回了
                Integer status = oderLog.getStatus();
                idUpa = status==-1?4:status==0?1:status==1?2:status==2?3:4;
            }else {
                idUpa = 0;
            }
        }
        return idUpa;
    }
}
