package blog.model;

import lombok.Data;

@Data
public class HockeyModel {

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
    String firstPoint;
    Integer firstQPoint;
    Integer secondQPoint;
    Integer thirdQPoint;
    Integer extendQPoint;
    Integer shotoutQPoint;
    Integer firstQTotalPoint;
    Integer secondQTotalPoint;
    Integer thirdQTotalPoint;
    Integer extendQTotalPoint;
    Integer shotoutQTotalPoint;

}
