package blog.model;

import lombok.Data;

@Data
public class SportModel {
    String league;
    String date;
    String time;

    String aTeam;
    String bTeam;

    Integer aTeamTotalPoint;
    Integer bTeamTotalPoint;

    Double handiCap;
    Double pointLine;

    String handiCapResult;
    String pointLineResult;

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

    Integer FirstQPoint;
    Integer SecondQPoint;
    Integer ThirdQPoint;
    Integer FourthQPoint;
    Integer ExtendQPoint;

    Double firstQHandiCap;
    Double firstQPointLine;

    String firstQHandiCapResult;
    String firstQPointLineResult;

    Integer firstQTotalPoint;
    Integer secondQTotalPoint;
    Integer thirdQTotalPoint;
    Integer fourthQTotalPoint;
    Integer extendQTotalPoint;

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




}
