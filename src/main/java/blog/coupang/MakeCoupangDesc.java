package blog.coupang;

import blog.model.TempDealVO;

public class MakeCoupangDesc {

    public String desc1(String todayProductCate, TempDealVO tempDealVO, String productPriceInfo, String coupangUrl, String imgUrl, String productInfoDetail, String productReview ,String time2){
        String resultDesc = "";

        resultDesc = "<div>\n" +
                "  <center>\n" +
                "    <p class=\"se_textarea\">오늘은 " + todayProductCate + " 관련 상품을 준비해보았습니다<br>\n" +
                "      많은 분들이 행사 기다리시던 상품 " +
                "    <div class=\"se_component se_quotation default\">" +
                "                        <div class=\"se_sectionArea\">" +
                "                           <div class=\"se_editArea\">" +
                "                               <div class=\"se_viewArea se_fs_T2\">" +
                "                               <div class=\"se_editView\">" +
                "                                   <div class=\"se_textView\">" +
                "                                       <blockquote class=\"se_textarea\"><!-- SE3-TEXT { --><b>" + replaceDealName(tempDealVO.getDealName()) + "</b><!-- } SE3-TEXT --></blockquote>" +
                "                                   </div>" +
                "                              </div>" +
                "                            </div>" +
                "                         </div>" +
                "                      </div>" +
                "                    </div>" +
                "     입니다!!<br><br>" +
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
                "       <a href=\"" + coupangUrl + "\"><img src=\"" + imgUrl + "\" data-lazy-src=\"\" data-width=\"500\" data-height=\"500\" width=\"500\" height=\"500\"></a>\n" +
                "  </center>\n" +
                "</div>\n" +
                "\n" +

                "<div>\n" +
                "  <center>\n" +
                "            <p >상세정보 참고하여 구매하세요<br>\n" +
                productInfoDetail +
                "        <a href=\"" + coupangUrl +" \" target=\"_blank\">" +
                "<span style=\"background-color:null;border:1px solid rgb(52, 106, 255);color:rgb(52, 106, 255);display:inline-block;padding:12px 20px;text-decoration-line:none;font-size:14.5px;font-weight:bold;user-select:auto;\">∇ 상세정보 더보기 ∇</span></a>\n" +
                "  </center>\n" +
                "</div>\n" +
                "\n" +
                "    \t\t<br>\n" +
                "    \t\t<br>\n" +

                "<div>\n" +
                "  <center>\n" +

                "             <p >로켓와우 회원이실 경우 캐시백 적립가능합니다.\n" +
                "             <p >또한 로켓와우를 아직 사용안해보신 분은 <b><span style=\" color:rgb(255, 125, 0) ;font-size :20pt\"> 1달 무료 체험 </span></b>할 수 있습니다.\n" +
                "             <p >무료사용기간 후에 맘에 드시면 <b><span style=\" color:rgb(255, 125, 0) ;font-size :20pt\">2,900원 </span></b>만 내시면 많은 해택도 이용 하실 수 있으니 참고하세요.\n<br>" +
                "             <a href=\"https://coupa.ng/brzmh7\" target=\"_blank\">▶ 로켓와우 1달 무료 체험하기◀</a>\n" +

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
                "<a href=\"" + coupangUrl + "\" target=\"_blank\">" +
                "<span style=\"background-color:null;border:1px solid rgb(52, 106, 255);color:rgb(52, 106, 255);display:inline-block;padding:12px 20px;text-decoration-line:none;font-size:14.5px;font-weight:bold;user-select:auto;\">▽ 구매 전 상세리뷰 더보기 ▽</span></a>\n" +
                "\n" +
                "  </center>\n" +
                "</div>\n" +
                "\t\t\t<br>\n" +
                "\t\t\t<br>\n" +

                "<div>\n" +
                "  <center>\n" +
                "             <p >쿠팡에서 <b><span style=\" color:rgb(255, 0, 0) ;font-size :20pt\"> 초특급 할인 </span></b>하는 상품만 모았습니다.\n" +
                "             <p >코로나19로 인해 품절사태가 일어나고 있으니 품절되기 전에 빨리 구입하세요.\n" +
                "             <p >  <b><span style=\" color:rgb(255, 0, 0) ;font-size :20pt\">특가 상품 </span></b>은 조기 품절 될 수 있으니 참고하세요.\n" +
                "    \t\t<br>\n" +
                "<a href=\"https://coupa.ng/brzoEo\" target=\"_blank\">" +
                "<span style=\"background-color:null;border:1px solid rgb(52, 106, 255);color:rgb(52, 106, 255);display:inline-block;padding:12px 20px;text-decoration-line:none;font-size:14.5px;font-weight:bold;user-select:auto;\">▽ 특가 상품 더보기 ▽</span></a>\n</a>" +
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
                "    \t\t<br>\n" ;

        
        return resultDesc;
    }

    public String desc2(String todayProductCate, TempDealVO tempDealVO, String productPriceInfo, String coupangUrl, String imgUrl, String productInfoDetail, String productReview ,String time2) {
        String resultDesc = "";

        return resultDesc;
    }


    private String replaceDealName (String dealName){
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

}
