package blog.model;

import lombok.Data;

@Data
public class SportModel {
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
    Integer bTeamFirstQPoint;

    Double firstQHandiCap;
    String firstQHandiCapResult;

    Double firstQPointLine;
    String firstQPointLineResult;

    Boolean firstQFirstPoint;
    Boolean firstQFirstFreeTwo;
    Boolean firstQFirstTwoPoint;
    Boolean firstQFirstThreePoint;

    Boolean secondQFirstPoint;
    Boolean secondQFirstFreeTwo;
    Boolean secondQFirstTwoPoint;
    Boolean secondQFirstThreePoint;

    Boolean thirdQFirstPoint;
    Boolean thirdQFirstFreeTwo;
    Boolean thirdQFirstTwoPoint;
    Boolean thirdQFirstThreePoint;

    Boolean fourthQFirstPoint;
    Boolean fourthQFirstFreeTwo;
    Boolean fourthQFirstTwoPoint;
    Boolean fourthQFirstThreePoint;

    Integer FirstQPoint;
    Integer SecondQPoint;
    Integer ThirdQPoint;
    Integer FourthQPoint;
    Integer ExtendQPoint;

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
