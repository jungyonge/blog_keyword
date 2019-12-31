package blog.model;

import lombok.Data;

@Data
public class BasketballModel {
    String gameId;
    String league;
    String ground;

    String date;
    String dayOfWeek;
    String time;

    String aTeam;
    Integer aTeamTotalPoint;
    Integer bTeamTotalPoint;
    String bTeam;

    Double handiCap;
    String handiCapResult;

    Double pointLine;
    String pointLineResult;

    Integer aTeamFirstQPoint;
    Integer aTeamSecondQPoint;
    Integer aTeamThirdQPoint;
    Integer aTeamFourthQPoint;
    Integer aTeamExtendQPoint;

    Integer bTeamFirstQPoint;
    Integer bTeamSecondQPoint;
    Integer bTeamThirdQPoint;
    Integer bTeamFourthQPoint;
    Integer bTeamExtendQPoint;

    Double firstQHandiCap;
    String firstQHandiCapResult;
    Double secondQHandiCap;
    String secondQHandiCapResult;
    Double thirdQHandiCap;
    String thirdQHandiCapResult;
    Double fourthQHandiCap;
    String fourthQHandiCapResult;

    Double firstQPointLine;
    String firstQPointLineResult;
    Double secondQPointLine;
    String secondQPointLineResult;
    Double thirdQPointLine;
    String thirdQPointLineResult;
    Double fourthQPointLine;
    String fourthQPointLineResult;

    String firstQFirstPoint;
    String firstQFirstFreeTwo;
    String firstQFirstTwoPoint;
    String firstQFirstThreePoint;

    String secondQFirstPoint;
    String secondQFirstFreeTwo;
    String secondQFirstTwoPoint;
    String secondQFirstThreePoint;

    String thirdQFirstPoint;
    String thirdQFirstFreeTwo;
    String thirdQFirstTwoPoint;
    String thirdQFirstThreePoint;

    String fourthQFirstPoint;
    String fourthQFirstFreeTwo;
    String fourthQFirstTwoPoint;
    String fourthQFirstThreePoint;


    Integer firstQTotalPoint;
    Integer secondQTotalPoint;
    Integer thirdQTotalPoint;
    Integer fourthQTotalPoint;
    Integer extendQTotalPoint;

//    Integer aTeamFirstQPoint;
//    Integer aTeamSecondQPoint;
//    Integer aTeamThirdQPoint;
//    Integer aTeamFourthQPoint;
//    Integer aTeamExtendQPoint;
//
//    Integer bTeamFirstQPoint;
//    Integer bTeamSecondQPoint;
//    Integer bTeamThirdQPoint;
//    Integer bTeamFourthQPoint;
//    Integer bTeamExtendQPoint;

}
