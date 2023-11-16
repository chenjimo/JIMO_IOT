package jimo.iot.ctrl_module.service;

import jimo.iot.ctrl_module.entity.OderLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 *  1、用户写入控制记录
 *  2、模块读取控制记录API
 *  3、读取模块返回过来的执行情况
 *  4、用户获取全部指令记录信息（分类为执行成功、等待执行中、与执行失败）
 *  5、用户撤回指令（status=-1）先判断指令未执行且未读取（及时查询类型）
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
public interface IOderLogService extends IService<OderLog> {
    /***
     * 写入控制记录
     * @param oderLog
     * @return
     */
    public boolean writeLog(OderLog oderLog);

    /***
     * 模块读取未执行且可以执行的指令，并记录读取时间
     * @param moduleId
     * @return
     */
    public OderLog readLog(Integer moduleId);

    /***
     * 根据执行的指令记录ID进行更新status
     * @param oderLogId
     * @param status
     * @return
     */
    public boolean updateLog(Integer oderLogId,Integer status);

    /***
     * 用户获取全部指令记录信息（根据status和ReadTime分类为等待中、执行中、执行成功、与执行失败）
     * @param isReadOrder
     * @param status
     * @return
     */
    public List<OderLog> getLogs(boolean isReadOrder,Integer status);

    /***
     * 根据status和ReadTime分类为等待中(0)、执行中(1)、执行成功(2)、与执行失败(3),判断是否可以撤销这条指令！
     * @param orderId
     * @return
     */
    public Integer isUpdate(Integer orderId);
}
