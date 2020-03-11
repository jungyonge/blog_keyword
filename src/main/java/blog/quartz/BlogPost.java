package blog.quartz;

import blog.model.TempDealVO;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import blog.rest.KeywordStat;
import com.naver.rpc.XmlRpcNaverBlog;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.Map;

public class BlogPost implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());


        XmlRpcNaverBlog xmlRpcNaverBlog = new XmlRpcNaverBlog();
        KeywordStat keywordStat = new KeywordStat();
        Map<String, Object> statMap = new HashMap<String, Object>();
        String rsString = "";
        TempDealVO tempDealVO = new TempDealVO();
        try {


            for(int i = 0 ; i < 10000 ; i++){
                tempDealVO = setalarmDAO.selectCoupangDeal();
                System.out.println(i + "번째");
                statMap = keywordStat.getCoupangDealStat(xmlRpcNaverBlog.replaceDealName(tempDealVO.getDealName()));
                System.out.println("monthlyPcQcCnt : " + statMap.get("monthlyPcQcCnt"));
                System.out.println("monthlyMobileQcCnt : " + statMap.get("monthlyMobileQcCnt"));
                System.out.println("totalPost : " + statMap.get("totalPost"));

//              &&  (!statMap.get("monthlyPcQcCnt").toString().equals("< 10") || !statMap.get("monthlyMobileQcCnt").toString().equals("< 10"))

                if(!statMap.get("make").toString().equals("error") && Integer.parseInt(statMap.get("totalPost").toString()) < 20000 ){
                    rsString = xmlRpcNaverBlog.writeBlogPost(tempDealVO);
                    System.out.println(rsString);
                    break;
                } else {
                    System.out.println("보류");
                    tempDealVO.setPostid("보류");
                    setalarmDAO.updateCoupangDeal(tempDealVO);
                }

            }

        }catch(Exception e) {
            e.printStackTrace();
            tempDealVO.setPostid("에러");
            setalarmDAO.updateCoupangDeal(tempDealVO);
        }
    }
}
