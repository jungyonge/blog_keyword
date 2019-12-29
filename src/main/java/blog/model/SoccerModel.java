package blog.model;

import lombok.Data;

@Data
public class SoccerModel {

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

}
