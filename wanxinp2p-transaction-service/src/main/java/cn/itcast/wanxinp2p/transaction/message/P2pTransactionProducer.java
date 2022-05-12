package cn.itcast.wanxinp2p.transaction.message;

import cn.itcast.wanxinp2p.api.repayment.model.ProjectWithTendersDTO;
import cn.itcast.wanxinp2p.transaction.entity.Project;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class P2pTransactionProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void updateProjectStatusAndStartRepayment(Project project,
                                                     ProjectWithTendersDTO projectWithTendersDTO) {
        // 1.构造消息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("project",project);
        jsonObject.put("projectWithTendersDTO",projectWithTendersDTO);
        Message<String> msg = MessageBuilder.withPayload(jsonObject.toJSONString()).build();

        // 2.发送消息
        rocketMQTemplate.sendMessageInTransaction("PID_START_REPAYMENT",
                "TP_START_REPAYMENT",msg,null);
    }
}
