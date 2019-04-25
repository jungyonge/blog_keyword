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

    SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    @GetMapping("/keyword/{table}")
    public void getKeywordForMake(@PathVariable("table") String table){
        Map<String, Object> map = new HashMap<String, Object>();
        List resultList = null;
        map.put("table",table);
        resultList = setalarmDAO.getKeywordForMake(map);
        System.out.println(resultList);

    }
}
