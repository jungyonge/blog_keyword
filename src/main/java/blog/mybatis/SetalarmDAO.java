package blog.mybatis;

import blog.model.BlogModel;
import blog.model.SportModel;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetalarmDAO {

    private SqlSessionFactory sqlSessionFactory = null;
    public SetalarmDAO(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<Setalarm> getKeywordMaster(){
        List<Setalarm> list = null;
        SqlSession session = sqlSessionFactory.openSession();

        try {
            list = session.selectList("Setalarm.getKeywordMaster");
        } finally {
            session.close();
        }
        return list;
    }

    public List<Setalarm> getKeywordRelate1(){
        List<Setalarm> list = null;
        SqlSession session = sqlSessionFactory.openSession();

        try {
            list = session.selectList("Setalarm.getKeywordRelate1");
        } finally {
            session.close();
        }
        return list;
    }


    public List<Setalarm> getMakeKeywordRelate(){
        List<Setalarm> list = null;
        SqlSession session = sqlSessionFactory.openSession();

        try {
            list = session.selectList("Setalarm.getMakeKeywordRelate");
        } finally {
            session.close();
        }
        return list;
    }

    public List<Setalarm> getKeywordMasterForMake(Map map){
        List<Setalarm> list = null;
        SqlSession session = sqlSessionFactory.openSession();

        try {
            list = session.selectList("Setalarm.getKeywordMasterForMake",map);
        } finally {
            session.close();
        }
        return list;
    }


    public List<Setalarm> getKeywordRelateForMake(Map map){
        List<Setalarm> list = null;
        SqlSession session = sqlSessionFactory.openSession();

        try {
            list = session.selectList("Setalarm.getKeywordRelateForMake",map);
        } finally {
            session.close();
        }
        return list;
    }

    public void insertKeyword_Master(Map map){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertKeyword_Master", map);
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
    public void insertMakeKeyword_Relate(Map map){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertMakeKeyword_Relate", map);
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketStat(SportModel sportModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketStat", sportModel);
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertKeywordStat(Map map){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertKeywordStat", map);
        } finally {
            session.commit();
            session.close();
        }
    }

    public void deleteKeyword_Master(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.deleteKeyword_Master");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void deleteKeyword_Relete(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.deleteKeyword_Relete");
        } finally {
            session.commit();
            session.close();
        }
    }
    public void updateUsed_Master(Map map){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.updateUsed_Master",map);
        } finally {
            session.commit();
            session.close();
        }
    }
    public void updateUsed_Relate(Map map){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("Setalarm.updateUsed_Relate",map);
        } finally {
            session.commit();
            session.close();
        }
    }


}



