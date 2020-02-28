package blog.mybatis;

import blog.model.*;
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

    public void insertBasketStat(BasketballModel basketballModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketStat", basketballModel);
        } finally {
            session.commit();
            session.close();
        }
    }



    public List selectBasketStat() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketStat");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }
    public void insertBasketMatch(BasketballModel basketballModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketMatch", basketballModel);
        } finally {
            session.commit();
            session.close();
        }
    }

    public void updateBasketStat(BasketballModel basketballModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.update("Setalarm.updateBasketStat", basketballModel);
        } finally {
            session.commit();
            session.close();
        }
    }


    public void updateTomorrowBasketStat(BasketballModel basketballModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.update("Setalarm.updateTomorrowBasketStat", basketballModel);
        } finally {
            session.commit();
            session.close();
        }
    }


    public void insertVolleyStat(VolleyballModel volleyballModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertVolleyStat", volleyballModel);
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertVolleyMatch(VolleyballModel volleyballModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertVolleyMatch", volleyballModel);
        } finally {
            session.commit();
            session.close();
        }
    }


    public void updateVolleyStat(VolleyballModel volleyballModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.update("Setalarm.updateVolleyStat", volleyballModel);
        } finally {
            session.commit();
            session.close();
        }
    }

    public List selectVolleyStat() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectVolleyStat");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public void insertHockeyStat(HockeyModel hockeyModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertHockeyStat", hockeyModel);
        } finally {
            session.commit();
            session.close();
        }
    }


    public void insertHockeyMatch(HockeyModel hockeyModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertHockeyMatch", hockeyModel);
        } finally {
            session.commit();
            session.close();
        }
    }

    public void updateHockeyStat(HockeyModel hockeyModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.update("Setalarm.updateHockeyStat", hockeyModel);
        } finally {
            session.commit();
            session.close();
        }
    }

    public List selectHockeyStat() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectHockeyStat");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public void insertSoccerStat(SoccerModel soccerModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertSoccerStat", soccerModel);
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertSoccerMatch(SoccerModel soccerModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertSoccerMatch", soccerModel);
        } finally {
            session.commit();
            session.close();
        }
    }



    public void updateSoccerStat(SoccerModel soccerModel){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.update("Setalarm.updateSoccerStat", soccerModel);
        } finally {
            session.commit();
            session.close();
        }
    }

    public List selectSoccerStat() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectSoccerStat");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }



    public List<HashMap<String, Object>> selectMemberList() {
        SqlSession session = sqlSessionFactory.openSession();
        List<HashMap<String, Object>> memberList ;

        try {
            memberList = session.selectList("Setalarm.selectMemberList");
        } finally {
            session.commit();
            session.close();
        }
        return memberList;
    }

    public void truncateBasketSpecialSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketSpecialSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketSpecialSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketSpecialSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketQuarterHandiOverSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketQuarterHandiOverSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketQuarterHandiOverSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketQuarterHandiOverSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketSpecialComboSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketSpecialComboSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketSpecialComboSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketSpecialComboSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketQuarterHandiComboSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketQuarterHandiComboSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketQuarterHandiComboSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketQuarterHandiComboSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public List selectBasketSpecialSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketSpecialSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public List selectBasketQuarterHandiOverSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketQuarterHandiOverSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public List selectBasketSpecialComboSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketSpecialComboSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public List selectBasketQuarterHandiComboSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketQuarterHandiComboSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }


    public void truncateBasketSpecialGroundSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketSpecialGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketSpecialGroundSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketSpecialGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public List selectBasketSpecialGroundSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketSpecialGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public void truncateBasketQuarterHandiOverGroundSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketQuarterHandiOverGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketQuarterHandiOverGroundSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketQuarterHandiOverGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public List selectBasketQuarterHandiOverGroundSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketQuarterHandiOverGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }


    public void truncateBasketHandiOverSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketHandiOverSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketHandiOverSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketHandiOverSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public List selectBasketHandiOverSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketHandiOverSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public void truncateBasketHandiOverGroundSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketHandiOverGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketHandiOverGroundSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketHandiOverGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public List selectBasketHandiOverGroundSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectBasketHandiOverGroundSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public List selectAllSummary() {
        SqlSession session = sqlSessionFactory.openSession();
        List list = null;

        try {
            list = session.selectList("Setalarm.selectAllSummary");
        } finally {
            session.commit();
            session.close();
        }
        return list;
    }

    public void truncateBasketSpecialOddSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketSpecialOddSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketQuarterHandiOverOddSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketQuarterHandiOverOddSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketHandiOverOddSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketHandiOverOddSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketSpecialOddSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketSpecialOddSummary");
        } finally {
            session.commit();
            session.close();
        }
    }



    public void insertBasketQuarterHandiOverOddSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketQuarterHandiOverOddSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketHandiOverOddSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketHandiOverOddSummary");
        } finally {
            session.commit();
            session.close();
        }
    }



    public void truncateBasketSpecialWeekSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketSpecialWeekSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketQuarterHandiOverWeekSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketQuarterHandiOverWeekSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketHandiOverWeekSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketHandiOverWeekSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketSpecialWeekSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketSpecialWeekSummary");
        } finally {
            session.commit();
            session.close();
        }
    }



    public void insertBasketQuarterHandiOverWeekSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketQuarterHandiOverWeekSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketHandiOverWeekSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketHandiOverWeekSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketSpecialRestSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketSpecialRestSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketQuarterHandiOverRestSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketQuarterHandiOverRestSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void truncateBasketHandiOverRestSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.truncateBasketHandiOverRestSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketSpecialRestSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketSpecialRestSummary");
        } finally {
            session.commit();
            session.close();
        }
    }



    public void insertBasketQuarterHandiOverRestSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketQuarterHandiOverRestSummary");
        } finally {
            session.commit();
            session.close();
        }
    }

    public void insertBasketHandiOverRestSummary(){
        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.insert("Setalarm.insertBasketHandiOverRestSummary");
        } finally {
            session.commit();
            session.close();
        }
    }






/////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////키워드 파싱

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


    public void insertCoupangDeal(TempDealVO tempDealVO){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("Setalarm.insertCoupangDeal",tempDealVO);
        } finally {
            session.commit();
            session.close();
        }
    }



    public TempDealVO selectCoupangDeal() {
        SqlSession session = sqlSessionFactory.openSession();
        TempDealVO tempDealVO ;

        try {
            tempDealVO = session.selectOne("Setalarm.selectCoupangDeal");
        } finally {
            session.commit();
            session.close();
        }
        return tempDealVO;
    }

    public void updateCoupangDeal(TempDealVO tempDealVO){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("Setalarm.updateCoupangDeal",tempDealVO);
        } finally {
            session.commit();
            session.close();
        }
    }


}



