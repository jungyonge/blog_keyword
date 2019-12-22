package blog.model;

import lombok.Data;

@Data
public class SportModel {
    String league;
    String time;

    String homeTeam;
    String awayTeam;

    Integer homeTotalPoint;
    Integer awayTotalPoint;

    Double handiCap;
    Double pointLine;

    String handiCapResult;
    String pointLineResult;

    Integer homeFirstQuarterPoint;
    Integer awayFirstQuarterPoint;

    Double firstQuarterHandiCap;
    Double firstQuarterPointLine;

    String firstQuarterHandiCapResult;
    String firstQuarterPointLineResult;

    Integer firstQuarterPoint;
    Integer secondQuarterPoint;
    Integer thirdQuarterPoint;
    Integer fourthQuarterPoint;
    Integer extendQuarterPoint;

    Boolean firstQuarterfirstPoint;
    Boolean firstQuarterfirstFreeTwo;
    Boolean firstQuarterfirstTwoPoint;
    Boolean firstQuarterfirstThreePoint;

    Boolean secondQuarterfirstPoint;
    Boolean secondQuarterfirstFreeTwo;
    Boolean secondQuarterfirstTwoPoint;
    Boolean secondQuarterfirstThreePoint;

    Boolean thirdQuarterfirstPoint;
    Boolean thirdQuarterfirstFreeTwo;
    Boolean thirdQuarterfirstTwoPoint;
    Boolean thirdQuarterfirstThreePoint;

    Boolean fourthQuarterfirstPoint;
    Boolean fourthQuarterfirstFreeTwo;
    Boolean fourthQuarterfirstTwoPoint;
    Boolean fourthQuarterfirstThreePoint;




}
