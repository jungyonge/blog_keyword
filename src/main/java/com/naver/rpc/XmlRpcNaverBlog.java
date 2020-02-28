package com.naver.rpc;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import blog.coupang.CoupangAPI;
import blog.coupang.CoupangCateParse;
import blog.model.TempDealVO;
import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class XmlRpcNaverBlog {

    static final String API_URL = "https://api.blog.naver.com/xmlrpc";

    static final String API_ID = "qjsro1204";
    static final String API_PASSWORD = "51030047387b5a1e2483c2d7f6e82e19";
    SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());


    public void writeBlogPost(TempDealVO tempDealVO) {
        // TODO Auto-generated method stub

        CoupangCateParse coupangCateParse = new CoupangCateParse();
        CoupangAPI coupangAPI = new CoupangAPI();
        try {

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

            DecimalFormat df = new DecimalFormat("#,###");

            SimpleDateFormat format2 = new SimpleDateFormat( "yyyy년 MM월dd일 HH시mm분");

            Date time = new Date();

            String time2 = format2.format(time);



            if(Integer.parseInt(tempDealVO.getDcRatio()) > 0){
                productPriceInfo =
                        "      <b><span style=\"color:rgb(255, 0, 0); font-size : 50pt\">할인률 : "+ Integer.parseInt(tempDealVO.getDcRatio()) + "%<br></span></b>\n" +
                        "            <b><span style=\" font-size :40pt\">할인 가격 : " + df.format(Integer.parseInt(tempDealVO.getDcPrice())) + "원</span></b><br>\n" +
                        "\n" +
                        "      <a href=\"" + coupangUrl + " \" target=\"_blank\" style=\" font-size : 35pt\" >▶ 할인가 구매하러 가기 ◀</a><br>\n" +
                        "      \n" +
                        "      <br>\n" ;
            }else {
                productPriceInfo =
                        "      <br>\n" +
                        "      \n" +
                        "      <b><span style=\"color:rgb(255, 0, 0); font-size :40pt\">최저가격 : "+  df.format(Integer.parseInt(tempDealVO.getNmPrice())) +"원<br></span></b>\n" +
                        "       <a href=\"" + coupangUrl + " \" target=\"_blank\" style=\" font-size : 35pt\" >▶ 최저가 구매하러 가기 ◀</a><br>\n" +
                        "\n" +
                        "\n" ;
            }



            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(API_URL));

            Map<String, String> contents = new HashMap<String, String>();

            String desc = "";

            desc = "<div>\n" +
                    "  <center>\n" +
                    "    <p class=\"se_textarea\">오늘은 " + todayProductCate + " 관련 상품을 준비해보았습니다<br>\n" +
                    "      바로!" +
                    "   <b><span style=\\\"font-size : 50pt\\\">" +  tempDealVO.getDealName()+ "%<br></span></b>\\n\" +\n" +
                    "     입니다!!<br><br>\n" +
                    "      구매는 아래 링크에서 가능합니다.<br>\n" +
                            productPriceInfo +
                    "  </center>\n" +
                    "</div>\n" +
                    "\n" +
                    "<div>\n" +
                    "  <center>\n" +
                    "    \n" +
                    "        <p >제품상세 이미지<br>\n" +
                    "\n" +
                    "        <img src=\"" + tempDealVO.getImgUrl() + "\" data-lazy-src=\"\" data-width=\"500\" data-height=\"500\" width=\"500\" height=\"500\">\n" +
                    "  </center>\n" +
                    "</div>\n" +
                    "\n" +

                    "<div>\n" +
                    "  <center>\n" +
                    "            <p >상세정보 참고하여 구매하세요<br>\n" +
                                productInfoDetail +
                    "        <a href=\"" + coupangUrl +" \" target=\"_blank\">∇ 상세정보 더보기 ∇</a>\n" +
                    "  </center>\n" +
                    "</div>\n" +
                    "\n" +
                    "    \t\t<br>\n" +
                    "    \t\t<br>\n" +

                    "<div>\n" +
                    "  <center>\n" +
                    "            <p >로켓와우 회원이실 경우 캐시백 적립가능합니다.<br>\n" +
                    "            <p >또한 로켓와우를 아직 사용안해보신 분은 한달 무료 사용할 수 있습니다.<br>\n" +
                    "            <p >무료사용기간 후에 맘에 드시면 2,900원만 내시면 많은 해택도 이용 하실 수 있으니 참고하세요.<br>\n" +
                    "            <a href=\"https://coupa.ng/brzmh7\" target=\"_blank\">▶ 로켓와우 1달 무료 체험하기◀</a>" +

                    "  </center>\n" +
                    "</div>\n" +
                    "\n" +
                    "    \t\t<br>\n" +
                    "    \t\t<br>\n" +
                    "<div>\n" +
                    "  <center>\n" +
                    "\n" +
                    "    \n" +
                                productReview +
                    "<a href=\"" + coupangUrl + "\" target=\"_blank\">▶ 구매 전 상세리뷰 더보기◀</a><br>\n" +
                    "\n" +
                    "  </center>\n" +
                    "</div>\n" +
                    "\t\t\t<br>\n" +
                    "\t\t\t<br>\n" +

                    "<div>\n" +
                    "  <center>\n" +
                    "            <p >특가 상품은 조기 품절 될 수 있으니 참고하세요.<br>\n" +
                    "<a href=\"" + coupangUrl + " \" target=\"_blank\">▶ 특가 상품 더보기◀</a>" +
                    "              <p>\n" +
                    "\n" +
                    "    \t\t<br>\n" +
                    "    \t\t<br>\n" +
                    "    \t\t<br>\n" +
                    "                 본 게시글은 " + time2 + "에 작성되었습니다.\n" +
                    "    \t\t</p>\n" +
                    "\n" +
                    "            <p >이 포스팅은 쿠팡 파트너스 활동의 일환으로, 이에 따른 일정액의 수수료를 제공받고 있습니다.<br>\n" +
                    "\n" +
                    "              \n" +
                    "  </center>\n" +
                    "</div>\n" +
                    "    \t\t<br>\n" +
                    "    \t\t<br>\n" +
                    "    \t\t<br>\n" +
                    "    \t\t<br>\n" +
                    "\n";

            contents.put("categories", "일상"); // 카테고리 텍스트
            contents.put("title", "[최저가 상품] " + tempDealVO.getDealName() ); // 제목
            contents.put("description", desc);
            contents.put("tags", "최저가, 쿠팡, 쿠팡파트너스, 로켓와우"); // 태크 콤마로 구분한다.


            List<Object> params = new ArrayList<Object>();

            // 블로그ID를 넣으라는데 공백으로 해도 된다.
            params.add("아무거나 넣어도 된다");

            // API ID
            params.add(API_ID);

            // API 암호
            params.add(API_PASSWORD);

            // 블로그 컨텐츠
            params.add(contents);

            // 공개여부 true이면 공개, false면 비공개
            params.add(new Boolean(true));

            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            String rsString = (String) client.execute("metaWeblog.newPost", params);
            resultDealVO.setPostid(rsString);
            setalarmDAO.updateCoupangDeal(resultDealVO);
            System.out.println(rsString);

        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());


        XmlRpcNaverBlog xmlRpcNaverBlog = new XmlRpcNaverBlog();

        try {

            for(int i = 0 ; i < 10000 ; i++){
                TempDealVO tempDealVO = setalarmDAO.selectCoupangDeal();
                xmlRpcNaverBlog.writeBlogPost(tempDealVO);
                Thread.sleep(120000);

            }


        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}



