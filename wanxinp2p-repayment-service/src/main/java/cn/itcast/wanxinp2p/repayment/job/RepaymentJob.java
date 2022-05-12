package cn.itcast.wanxinp2p.repayment.job;

import cn.itcast.wanxinp2p.repayment.service.RepaymentService;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class RepaymentJob implements SimpleJob {
    @Autowired
    private RepaymentService repaymentService;
    @Override
    public void execute(ShardingContext shardingContext) {
        //调用业务层执行任务
        repaymentService.executeRepayment(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem());
    }
}
