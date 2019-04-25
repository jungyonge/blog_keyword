package blog.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Map;

public class SetalarmDAO {

    private SqlSessionFactory sqlSessionFactory = null;
    public SetalarmDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<Setalarm> getOrderStatusWeek(Map map){
        List<Setalarm> list = null;
        SqlSession session = sqlSessionFactory.openSession();

        try {
            list = session.selectList("Setalarm.getOrderStatusWeek",map);
        } finally {
            session.close();
        }
        System.out.println("getOrderStatusWeek(" + map + ")--> "+list);
        return list;
    }

    public List<Setalarm> getKeywordForMake(Map map){
        List<Setalarm> list = null;
        SqlSession session = sqlSessionFactory.openSession();

        try {
            list = session.selectList("Setalarm.getKeywordForMake",map);
        } finally {
            session.close();
        }
        return list;
    }

    public void insertKeyword_Master(String Keyword){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertKeyword_Master", Keyword);
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertKeyword_Relate(Map map){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertKeyword_Relate", map);
        } finally {
            session.commit();
            session.close();
        }
    }


}



