package cn.itcast.wanxinp2p.repayment.message;

import cn.itcast.wanxinp2p.api.depository.model.RepaymentRequest;
import cn.itcast.wanxinp2p.repayment.entity.RepaymentPlan;
import cn.itcast.wanxinp2p.repayment.mapper.PlanMapper;
import cn.itcast.wanxinp2p.repayment.service.RepaymentService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RocketMQTransactionListener(txProducerGroup = "PID_CONFIRM_REPAYMENT")
public class ConfirmRepaymentTransactionListener implements RocketMQLocalTransactionListener {
    @Autowired
    private RepaymentService repaymentService;
    @Autowired
    private PlanMapper repaymentPlanMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        //1.解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) msg.getPayload()));
        RepaymentPlan repaymentPlan = JSONObject.parseObject(jsonObject.getString("repaymentPlan"), RepaymentPlan.class);
        RepaymentRequest repaymentRequest = JSONObject.parseObject(jsonObject.getString("repaymentRequest"), RepaymentRequest.class);
        //2.执行本地事务
        final Boolean isCommit = repaymentService.confirmRepayment(repaymentPlan, repaymentRequest);
        //3.返回结果
        if (isCommit) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        //1.解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) msg.getPayload()));
        RepaymentPlan repaymentPlan =
                JSONObject.parseObject(jsonObject.getString("repaymentPlan"), RepaymentPlan.class);
        //2.事务状态回查
        RepaymentPlan newRepaymentPlan = repaymentPlanMapper.selectById(repaymentPlan.getId());
        //3.返回结果
        if (newRepaymentPlan != null && newRepaymentPlan.getRepaymentStatus().equals("1")) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
