package blog.rest;

import blog.model.RelKwdStatModel;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MakeKeyword {

    SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    RelKwdStat relKwdStat = new RelKwdStat();
    @GetMapping("/keyword/{table}")
    public void getKeywordForMake(@PathVariable("table") String table){
        Map<String, Object> map = new HashMap<String, Object>();
        List masterList = null;
        List relateList = null;
        map.put("table",table);
        masterList = setalarmDAO.getKeywordMasterForMake(map);
        relateList = setalarmDAO.getKeywordRelateForMake(map);
        for(int i = 0 ; i < masterList.size(); i++){
            HashMap<String,Object> test = (HashMap<String, Object>) masterList.get(i);
            String test3 = String.valueOf(test.get("st1"));
            for(int j = 0; j < relateList.size(); j++){
                HashMap<String,Object> test2 = (HashMap<String, Object>) relateList.get(j);
                String test4 = String.valueOf(test2.get("st2"));
                String test5 = test3+test4;
                map.put("keyword",test5);
                setalarmDAO.insertKeyword_Relate(map);
                System.out.println(test5);
            }
        }
        System.out.println("끝");
    }
}
