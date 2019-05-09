package blog.rest;

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
    private SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    @GetMapping("/keyword/{table}")
    public void getKeywordForMake(@PathVariable("table") String table){
        Map<String, Object> map = new HashMap<String, Object>();
        List masterList = null;
        List relateList = null;
        map.put("table",table);
        masterList = setalarmDAO.getKeywordMasterForMake(map);
        relateList = setalarmDAO.getKeywordRelateForMake(map);
        for(int i = 0 ; i < masterList.size(); i++){
            HashMap<String,Object> masterMap = (HashMap<String, Object>) masterList.get(i);
            String masterKeyword = String.valueOf(masterMap.get("st1"));
            for(int j = 0; j < relateList.size(); j++){
                HashMap<String,Object> relateMap = (HashMap<String, Object>) relateList.get(j);
                String relateKeyword = String.valueOf(relateMap.get("st2"));
                String finalKeywrd = masterKeyword + relateKeyword;
                map.put("keyword",finalKeywrd);
                setalarmDAO.insertKeyword_Relate(map);
                System.out.println(finalKeywrd);
            }
        }
        System.out.println("끝");
    }
}
