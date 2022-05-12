import org.springframework.context.ApplicationContext;

import java.util.Map;

public class Demo1 {
    public static void main(String[] args) {
        dealReferenceService(new TeleCommunicating());
    }

    public static void dealReferenceService(TeleCommunicating tc){
        ApplicationContext ac = SpringUtil.getContext();
        Map<String,ITeleCommunicatinService> result = ac.getBeansOfType(ITeleCommunicatinService.class);
        if(null!=result && !result.isEmpty()){
            for(Map.Entry<String,ITeleCommunicatinService> entry:result.entrySet()){
                try{
                    ITeleCommunicatinService it = SpringUtil.getBeanByType(entry.getValue().getClass());
                    it.dealYaoXin(tc);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
