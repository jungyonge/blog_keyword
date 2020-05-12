package blog.model;

import lombok.Data;

@Data
public class BaseballModel {

    String gameId;
    String league;
    String ground;

    String date;
    String dayOfWeek;
    String time1;

    Integer aTeamRestDay;
    String aTeamPitcher;
    String aTeam;
    Integer aTeamTotalPoint;
    Integer bTeamTotalPoint;
    String bTeam;
    String bTeamPitcher;
    Integer bTeamRestDay;

    Double handiCap;
    String handiCapResult;
    String odd;

    Double pointLine;
    String pointLineResult;

    Integer aTeamHalfPoint;
    Integer bTeamHalfPoint;

    Double halfHandiCap;
    String halfHandiCapResult;

    Double halfPointLine;
    String halfPointLineResult;

    String firstStrikeOut;
    String firstHomerun;
    String firstBaseOnBall;

    Integer firstScore;
    Integer secondScore;
    Integer thirdScore;
    Integer fourthScore;
    Integer fifthScore;
    Integer sixthScore;
    Integer seventhScore;
    Integer eighthScore;
    Integer ninthScore;


}
