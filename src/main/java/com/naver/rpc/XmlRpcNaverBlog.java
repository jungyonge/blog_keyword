package com.naver.rpc;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import blog.coupang.CoupangAPI;
import blog.coupang.CoupangCateParse;
import blog.coupang.MakeCoupangDesc;
import blog.model.TempDealVO;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import blog.rest.KeywordStat;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;


public class XmlRpcNaverBlog {

    static final String API_URL = "https://api.blog.naver.com/xmlrpc";
//    static final String API_ID = "qjsro1204";
//    static final String API_PASSWORD = "51030047387b5a1e2483c2d7f6e82e19";
    static final String API_ID = "jungyong_e";
    static final String API_PASSWORD = "904bbe94bf3af4d06b48e97a1e64c49e";
    SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());


    public String
    writeBlogPost(TempDealVO tempDealVO) {
        // TODO Auto-generated method stub

        CoupangCateParse coupangCateParse = new CoupangCateParse();
        CoupangAPI coupangAPI = new CoupangAPI();
        MakeCoupangDesc makeCoupangDesc = new MakeCoupangDesc();
        String rsString ="";

        try {

            Map<Object,Object> tempProductImgMap = new HashMap<>();
            Map<Object,Object> productImgMap = new HashMap<>();
            String prodcutImg = "";
            String imgUrl = makeImgUrl(tempDealVO);
            tempProductImgMap = coupangCateParse.getDealImg(tempDealVO.getProductUrl());
            if (!tempProductImgMap.get(0).toString().contains("<img")) {
                for(int i = 0 ; i < tempProductImgMap.size() ; i++){
                    tempDealVO.setImgUrl(tempProductImgMap.get(i).toString().replaceAll("//","http://"));
                    String tempImgUrl = makeImgUrl(tempDealVO);
                    productImgMap.put(i,tempImgUrl);
                }
                prodcutImg = coupangCateParse.getProductImg(productImgMap);
            } else {
                prodcutImg = tempProductImgMap.get(0).toString();
            }


            TempDealVO resultDealVO = new TempDealVO();
            resultDealVO.setIdx(tempDealVO.getIdx());
            String productInfoDetail = coupangCateParse.getDealInfoDetal(tempDealVO.getProductUrl());

            String coupangUrl = coupangAPI.getCoupangUrl(tempDealVO.getProductUrl());
            String productReview = coupangCateParse.getDealReview(tempDealVO.getSdid());
            String cate = tempDealVO.getCate();
            String todayProductCate = "";
            String[] cateArr = cate.split("/");
            for(int i = 1 ; i < cateArr.length ; i++){
                todayProductCate += cateArr[i] + " ";
            }
            String productPriceInfo = "";
            String productTitle = "";

            DecimalFormat df = new DecimalFormat("#,###");

            SimpleDateFormat format2 = new SimpleDateFormat( "yyyy년 MM월dd일 HH시mm분");

            Date time = new Date();

            String time2 = format2.format(time);



            if(Integer.parseInt(tempDealVO.getDcRatio()) > 0){
                productPriceInfo =
                        "      <b><span style=\"color:rgb(255, 0, 0); font-size : 15pt\">할인률 : "+ Integer.parseInt(tempDealVO.getDcRatio()) + "%<br></span></b>\n" +
                                "    <b><span style=\" color:rgb(255, 125, 0) ;font-size :15pt\">본가격 : " + df.format(Integer.parseInt(tempDealVO.getNmPrice())) +"원 </span></b>\n<br>" +
                                "\t<b><span style=\" color:rgb(0, 0, 0) ;font-size :15pt\"> ↓↓↓↓  </span></b><br>\n" +
                                "\t<b><span style=\" color:rgb(255, 0, 0) ;font-size :15pt\">할인 가격 : " + df.format(Integer.parseInt(tempDealVO.getDcPrice())) + "원</span></b><br>\n" +
                        "\n" +
                         "          <b><span style=\"color:rgb(255, 0, 0); font-size :15pt\"> 할인가 행사 </span></b> 마감 전에 구매하세요.<br>" +
                                "        <a href=\"" + coupangUrl +" \" target=\"_blank\">" +
                                "       <span style=\"background-color:rgb(52, 106, 255);border:1px solid rgb(52, 106, 255);color:rgb(255, 255, 255);display:inline-block;padding:12px 20px;text-decoration-line:none;font-size:14.5px;font-weight:bold;user-select:auto;\">▶ 할인가 구매하러 가기 ◀</span></a>\n" +
//                        "      <a href=\"" + coupangUrl + " \" target=\"_blank\" style=\" font-size : 35pt\" >▶ 할인가 구매하러 가기 ◀</a><br>\n" +
                        "      \n" +
                        "      <br>\n" ;
                productTitle = "[할인행사 상품] " + replaceDealName(tempDealVO.getDealName());
                if(!productReview.contains("아쉽게 해당상품의 등록된 상품평이 없네요")){
                    productTitle = "[특가 리뷰 상품] " + replaceDealName(tempDealVO.getDealName());
                }
            }else {
                productPriceInfo =
                        "      <br>\n" +
                        "      \n" +
                        "      <b><span style=\"color:rgb(255, 0, 0); font-size :15pt\">최저가격 : "+  df.format(Integer.parseInt(tempDealVO.getNmPrice())) +"원<br></span></b>\n" +
                        "          <b><span style=\"color:rgb(255, 0, 0); font-size :15pt\"> 최저가 행사</span></b> 마감 전에 구입하세요.<br>" +
                                "        <a href=\"" + coupangUrl +" \" target=\"_blank\">" +
                                "       <span style=\"background-color:rgb(52, 106, 255);border:1px solid rgb(52, 106, 255);color:rgb(255, 255, 255);display:inline-block;padding:12px 20px;text-decoration-line:none;font-size:14.5px;font-weight:bold;user-select:auto;\">▶ 할인가 구매하러 가기 ◀</span></a>\n" +
//                        "       <a href=\"" + coupangUrl + " \" target=\"_blank\" style=\" font-size : 35pt\" >▶ 최저가 구매하러 가기 ◀</a><br>\n" +
                        "\n<br>" +
                        "\n" ;
                productTitle = "[최저가 상품] " + replaceDealName(tempDealVO.getDealName());
                if(!productReview.contains("아쉽게 해당상품의 등록된 상품평이 없네요")){
                    productTitle = "[최저가 리뷰후기] " + replaceDealName(tempDealVO.getDealName());
                }

            }


            Map<String, String> contents = new HashMap<String, String>();

            String desc = makeCoupangDesc.desc1(todayProductCate,tempDealVO,productPriceInfo,coupangUrl,imgUrl,prodcutImg,productInfoDetail,productReview,time2);



            contents.put("categories", "일상"); // 카테고리 텍스트
            contents.put("title", productTitle); // 제목
            contents.put("description", desc);
            contents.put("tags", "최저가, 쿠팡, 쿠팡파트너스, 로켓와우"); // 태크 콤마로 구분한다.


            List<Object> params = new ArrayList<Object>();
            params.add("아무거나 넣어도 된다");
            params.add(API_ID);
            params.add(API_PASSWORD);
            params.add(contents);
            params.add(new Boolean(true));

            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(API_URL));

            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            rsString = (String) client.execute("metaWeblog.newPost", params);

            resultDealVO.setPostid(rsString);
            setalarmDAO.updateCoupangDeal(resultDealVO);
//            System.out.println(rsString);

        }catch(Exception e) {
            e.printStackTrace();
            tempDealVO.setPostid("이미지이상");
            setalarmDAO.updateCoupangDeal(tempDealVO);
        }
        return rsString;

    }

    private String makeImgUrl(TempDealVO tempDealVO){
        Map<String, Object> contents = new HashMap<String, Object>();
        SimpleDateFormat format2 = new SimpleDateFormat( "yyyy_MMdd_HHmmss");
        Date time = new Date();
        String time2 = format2.format(time);
        Map<String, Object> retMap = new HashMap<>();

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(API_URL));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            String fileName = tempDealVO.getSdid() + "_" + time2 + ".jpg";
            String filePath = "D:test/" + fileName;
            File outputFile = new File(filePath);
            URL url = null;
            BufferedImage bi = null;
            url = new URL(tempDealVO.getImgUrl());
            bi = ImageIO.read(url);
            ImageIO.write(bi, "jpg", outputFile);

            File file = new File(filePath);

            contents.put("name", file.getName());
            contents.put("type", new MimetypesFileTypeMap().getContentType(file));
            contents.put("bits", getFileData(file));

            retMap = (HashMap)client.execute("metaWeblog.newMediaObject", new Object[] {API_ID, API_ID, API_PASSWORD, contents});

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }

        return retMap.get("url").toString();
    }

    private static byte[] getFileData( File file ) throws IOException {
        int total = 0;
        int length = (int) file.length();
        byte[] ret = new byte[length];
        FileInputStream reader = new FileInputStream( file );
        while ( total < length ) {
            int read = reader.read( ret );
            if ( read < 0 ) throw new IOException( "fail read file: " + file );
            total += read;
        }
        return ret;
    }

    public String replaceDealName(String dealName){
        String resultDealName = "";

        resultDealName = dealName.replaceAll(",","");
        resultDealName = resultDealName.replaceAll("상세페이지참조()","");
        resultDealName = resultDealName.replaceAll("상세설명참조","");
        resultDealName = resultDealName.replaceAll("단일상품","");
        resultDealName = resultDealName.replaceAll("단일 색상","");
        resultDealName = resultDealName.replaceAll("해당없음","");
        resultDealName = resultDealName.replaceAll("상세페이지 참조","");
        resultDealName = resultDealName.replaceAll("상세 설명 참조0","");
        resultDealName = resultDealName.replaceAll("상세 설명 참조","");
        resultDealName = resultDealName.replaceAll("선택하세요","");
        resultDealName = resultDealName.replaceAll("본 상품 선택","");
        resultDealName = resultDealName.replaceAll("단일옵션","");
        resultDealName = resultDealName.replaceAll("단품","");
        resultDealName = resultDealName.replaceAll("기본형","");
        resultDealName = resultDealName.replaceAll("일반","");
        resultDealName = resultDealName.replaceAll("상세","");
        resultDealName = resultDealName.replaceAll("설명","");
        resultDealName = resultDealName.replaceAll("참조","");
        resultDealName = resultDealName.replaceAll("단일","");
        resultDealName = resultDealName.replaceAll("단품","");
        resultDealName = resultDealName.replaceAll("옵션","");
        resultDealName = resultDealName.replaceAll("선택","");
        resultDealName = resultDealName.replaceAll("1개","");



        return resultDealName;
    }



    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());


        XmlRpcNaverBlog xmlRpcNaverBlog = new XmlRpcNaverBlog();
        KeywordStat keywordStat = new KeywordStat();
        Map<String, Object> statMap = new HashMap<String, Object>();
        try {

            for(int i = 0 ; i < 10000 ; i++){
                TempDealVO tempDealVO = setalarmDAO.selectCoupangDeal();

                statMap = keywordStat.getCoupangDealStat(xmlRpcNaverBlog.replaceDealName(tempDealVO.getDealName()));
                System.out.println("monthlyPcQcCnt : " + statMap.get("monthlyPcQcCnt"));
                System.out.println("monthlyMobileQcCnt : " + statMap.get("monthlyMobileQcCnt"));
                System.out.println("totalPost : " + statMap.get("totalPost"));

//              &&  (!statMap.get("monthlyPcQcCnt").toString().equals("< 10") || !statMap.get("monthlyMobileQcCnt").toString().equals("< 10"))

                if(!statMap.get("make").toString().equals("error") && Integer.parseInt(statMap.get("totalPost").toString()) < 20000 ){
                    xmlRpcNaverBlog.writeBlogPost(tempDealVO);
                    Thread.sleep(180000);
                } else {
                    System.out.println("보류");
                    tempDealVO.setPostid("보류");
                    setalarmDAO.updateCoupangDeal(tempDealVO);
                }

            }

        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}
//{naverCnt=10,
// compIdx=낮음,
// whereMobileWeb=0,
// monthlyPcQcCnt=< 10,
// monthlyMobileQcCnt=< 10,
// whereWeb=1,
// relKeyword=쿠팡 브랜드 - 베이스알파 에센셜 남녀공용 30수 라운드 반팔티 3p,
// monthlyAveMobileCtr=0.0,
// plAvgDepth=0,
// totalPost=8221, m
// onthlyAvePcCtr=0.0, t
// istoryCnt=0, elseCnt=0, whereMobileBlog=0, monthlyAveMobileClkCnt=0.0, monthlyAvePcClkCnt=0.0, make=make, whereBlog=2}



