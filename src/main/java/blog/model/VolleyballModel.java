package blog.model;

import lombok.Data;

@Data
public class VolleyballModel {
    String gameId;
    String league;
    String ground;

    String date;
    String dayOfWeek;
    String time;

    String aTeam;
    Integer aTeamSetScore;
    Integer bTeamSetScore;
    String bTeam;

    Double handiCap;
    String handiCapResult;

    Double setHandiCap;
    String setHandiCapResult;


    Double pointLine;
    String pointLineResult;

    Integer aTeamFirstQPoint;
    Integer bTeamFirstQPoint;

    Double firstQHandiCap;
    String firstQHandiCapResult;

    Double firstQPointLine;
    String firstQPointLineResult;

    Boolean firstPoint;
    Boolean firstBlock;
    Boolean firstServe;

    Boolean first5Point;
    Boolean first7Point;
    Boolean first10Point;

    Integer firstQPoint;
    Integer secondQPoint;
    Integer thirdQPoint;
    Integer fourthQPoint;
    Integer fifthQPoint;

    Integer firstQTotalPoint;
    Integer secondQTotalPoint;
    Integer thirdQTotalPoint;
    Integer fourthQTotalPoint;
    Integer fifthQTotalPoint;


    public int getTotalPoint(){
        int total = firstQPoint +  secondQPoint + thirdQPoint +  fourthQPoint +   fifthQPoint;
        return total;
    }
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
