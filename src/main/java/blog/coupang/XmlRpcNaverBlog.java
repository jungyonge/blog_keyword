package com.naver.rpc;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class XmlRpcNaverBlog {

    static final String API_URL = "https://api.blog.naver.com/xmlrpc";

    static final String API_ID = "jungyong_e";
    static final String API_PASSWORD = "904bbe94bf3af4d06b48e97a1e64c49e";




    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try {

            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(API_URL));

            Map<String, String> contents = new HashMap<String, String>();
            contents.put("categories", "일상"); // 카테고리 텍스트
            contents.put("title", "네이버 테스트"); // 제목
            contents.put("description", "<div id=\"post-view221743483857\" class=\"wrap_rabbit pcol2 _param(1) _postViewArea221743483857\">\n" +
                    "\t\t\t\t\t\t\t<!-- Rabbit HTML --><div class=\"se-viewer se-theme-default\" lang=\"ko-KR\">\n" +
                    "    <!-- SE_DOC_HEADER_START -->\n" +
                    "\n" +
                    "    <div class=\"se-component se-documentTitle se-l-default \" id=\"SE-614f442c-ba9c-4637-9336-cae141c7f56c\">\n" +
                    "        <div class=\"se-component-content\">\n" +
                    "            <div class=\"se-section se-section-documentTitle se-l-default se-section-align-left\">\n" +
                    "                <!-- --> \n" +
                    "<div class=\"blog2_series\">\n" +
                    "\t<a href=\"/PostList.nhn?blogId=dungen&amp;categoryNo=7&amp;from=postList&amp;parentCategoryNo=7\" class=\"pcol2\" onclick=\"clickcr(this,'pst.category','','',event);\">핫딜,상품</a>\n" +
                    "</div>\n" +
                    "<div class=\"pcol1\"> \n" +
                    "<!-- -->\n" +
                    "                <div class=\"se-module se-module-text se-title-text\">\n" +
                    "                    <p class=\"se-text-paragraph se-text-paragraph-align-\" style=\"\" id=\"SE-8582327b-b402-4350-8877-25ad53abb0d9\"><span style=\"\" class=\"se-fs- se-ff-\" id=\"SE-be919a41-454e-4f5b-8079-600e77eb22e6\"><!-- -->(후기) 갤럭시 GALAX 지포스 GTX 1660 SUPER EX OC D6 6GB 그래픽카드 WHITE<!-- --></span></p>                </div>\n" +
                    "                <!-- -->\n" +
                    "</div>\n" +
                    "<div class=\"blog2_container\">\n" +
                    "    <span class=\"writer\">\n" +
                    "        <span class=\"area_profile\"><a href=\"https://blog.naver.com/dungen\" class=\"link\" onclick=\"clickcr(this,'pst.profile','','',event);\" target=\"_top\"><img src=\"https://blogpfthumb-phinf.pstatic.net/20150326_238/dungen_1427379212594fRj5r_JPEG/ugcCA6LOSJ7.jpg?type=s1\" class=\"img\" alt=\"프로파일\"></a></span>\n" +
                    "        <span class=\"nick\"><a href=\"https://blog.naver.com/dungen\" class=\"link pcol2\" onclick=\"clickcr(this,'pst.username','','',event);\" target=\"_top\">Ali Aly</a></span>\n" +
                    "    </span>\n" +
                    "    <i class=\"dot\"> ・ </i>\n" +
                    "\t<span class=\"se_publishDate pcol2\">2019. 12. 20. 10:29</span>\n" +
                    "\n" +
                    "</div>\n" +
                    "<div class=\"blog2_post_function\">\n" +
                    "\t<a href=\"#\" id=\"copyBtn_221743483857\" class=\"url pcol2 _setClipboard _returnFalse _se3copybtn _transPosition\" title=\"https://blog.naver.com/dungen/221743483857\" style=\"cursor:pointer;\">URL 복사</a>\n" +
                    "\n" +
                    "            <a href=\"#\" class=\"btn_buddy btn_addbuddy pcol2 _buddy_popup_btn _returnFalse\" onclick=\"clickcr(this,'pst.addnei','','',event);\"><i class=\"ico\"></i> 이웃추가<i class=\"aline\"></i></a>\n" +
                    "\t<div class=\"overflow_menu\">\n" +
                    "        <a href=\"#\" class=\"btn_overflow_menu _open_overflowmenu pcol2 _param(221743483857) _returnFalse\" role=\"button\" area-haspopup=\"true\" area-expanded=\"false\"><span class=\"blind\">본문 기타 기능</span></a>\n" +
                    "\t\t<div id=\"overflowmenu-221743483857\" class=\"lyr_overflow_menu\" area-hidden=\"true\">\n" +
                    "          \t <a href=\"#\" class=\"naverTranslateNavigationBox _goTran _param(221743483857) _returnFalse\">번역보기<span class=\"ico_translation _goTran _param(221743483857)\"></span></a>\n" +
                    "\t\t</div>\n" +
                    "\t</div>\n" +
                    "    <input type=\"text\" value=\"https://blog.naver.com/dungen/221743483857\" alt=\"url\" class=\"copyTargetUrl\" style=\"display:none;\" title=\"URL 복사\">\n" +
                    "</div>\n" +
                    "<!-- -->\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "    <div class=\"location_component\">\n" +
                    "    <div class=\"location\">\n" +
                    "            </div>\n" +
                    "</div>\n" +
                    "<!-- {{{$SE3-CONTENTS_HEADER}}} -->\n" +
                    "    <!-- SE_DOC_HEADER_END -->\n" +
                    "    <div class=\"se-main-container\">\n" +
                    "                <div class=\"se-component se-image se-l-default\" id=\"SE-1c8381a6-26c0-4feb-ab22-ed6e8eeb3b88\">\n" +
                    "                    <div class=\"se-component-content se-component-content-fit\">\n" +
                    "                        <div class=\"se-section se-section-image se-l-default se-section-align-\">\n" +
                    "\n" +
                    "                                <a href=\"#\" class=\"se-module se-module-image __se_image_link __se_link\" style=\" \" onclick=\"return false;\" data-linktype=\"img\" data-linkdata=\"{&quot;id&quot; : &quot;SE-1c8381a6-26c0-4feb-ab22-ed6e8eeb3b88&quot;, &quot;src&quot; : &quot;https://postfiles.pstatic.net/MjAxOTEyMjBfMTk0/MDAxNTc2ODA1MzgzNDU3.gAR_pAE5sz5bkseEpiXHUZNFaUuMiRLTfLgVEOnorBwg.oJXNoRAyk7zUELX9zThgpf0EChoPkp0UP4Pga1VOtRcg.PNG.dungen/SE-d21570b3-13b9-4ca1-ad2e-7e44a2f4c89c.png&quot;, &quot;linkUse&quot; : &quot;true&quot;, &quot;link&quot; : &quot;https://coupa.ng/bkZFcE&quot;}\">\n" +
                    "                                    <img src=\"https://postfiles.pstatic.net/MjAxOTEyMjBfMTk0/MDAxNTc2ODA1MzgzNDU3.gAR_pAE5sz5bkseEpiXHUZNFaUuMiRLTfLgVEOnorBwg.oJXNoRAyk7zUELX9zThgpf0EChoPkp0UP4Pga1VOtRcg.PNG.dungen/SE-d21570b3-13b9-4ca1-ad2e-7e44a2f4c89c.png?type=w773\" data-lazy-src=\"\" data-width=\"577\" data-height=\"577\" alt=\"\" class=\"se-image-resource\">\n" +
                    "                                </a>                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>                <div class=\"se-component se-text se-l-default\" id=\"SE-a3ee2f80-5685-4302-a933-622a8f45d28c\">\n" +
                    "                    <div class=\"se-component-content\">\n" +
                    "                        <div class=\"se-section se-section-text se-l-default\">\n" +
                    "                            <div class=\"se-module se-module-text\">\n" +
                    "\n" +
                    "                                    <!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-549e38b2-ba34-42e4-bf78-0257a85a6ee9\"><span style=\"\" class=\"se-fs-fs19 se-ff-   \" id=\"SE-94bf7f63-afc2-4634-bf54-6b68d897253c\"><b>갤럭시 GALAX 지포스 GTX 1660 SUPER EX OC D6 6GB 그래픽카드 WHITE</b></span></p><!-- } SE-TEXT -->\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>                <div class=\"se-component se-text se-l-default\" id=\"SE-85016d21-b748-4df4-abad-9c3caaa1c669\">\n" +
                    "                    <div class=\"se-component-content\">\n" +
                    "                        <div class=\"se-section se-section-text se-l-default\">\n" +
                    "                            <div class=\"se-module se-module-text\">\n" +
                    "\n" +
                    "                                    <!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-89731bb1-b4ce-4bb8-97a1-ee1895009dd4\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-ccbd2902-3bc3-4a0d-8f75-7c68af1920eb\">품질보증기준(은)는 제품 이상시 공정거래위원회 고시 소비자분쟁해결기준에 의거 보상합니다..</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-7a885b7d-914d-41b3-8258-793320b0417c\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-09c0d5d7-b7c4-4f6d-af30-98d62a6981f0\">에너지소비효율등급(은)는 에너지이용합리화법 상 의무대상상품에 한함.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-77114a0b-7392-457c-92be-7cf717122f30\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-b56c29dd-525a-44e3-928b-8c79dd29bc86\">무게(은)는 무게는 노트북에 한함.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-3a75a3c5-9984-4763-b48c-afea0f14f147\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-e9cd852e-adf7-482b-bcf8-3e2ce9848541\">주요 사양(은)는 컴퓨터와 노트북은 성능, 용량 등  프린터는 인쇄 속도 등.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-d2af6c7c-1d5a-4156-b78a-83f4ce93b38c\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-1542a97f-6fc3-43de-87ee-2033bd0241b7\">출시년월(은)는 상세페이지 참조.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-d0ae53d2-2cc8-4e4f-a01f-555b636e69e8\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-d9b26c78-ef8c-4c0c-9011-ecf48444bdff\">제조국(은)는 상세페이지 참조.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-fcaf639a-cec8-43e5-b3f1-e781a719cba6\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-6ac336fa-6008-4c70-8e01-26c1e54437d2\">KC 인증 필 유무(은)는 전파법 인증대상품에 한함, MIC 인증 필 혼용 가능.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-df95b7a5-99ba-4ba5-b21f-d784c167c935\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-eb410acb-1736-49fc-b6fc-344f322eba75\">정격전압, 소비전력(은)는 상세페이지 참조.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-5a62ed18-9e9a-4433-9886-451bf1513280\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-d251dea9-acc5-4573-94ef-c91eb4a73e93\">제조자(수입자)(은)는 병행수입의 경우 병행수입 여부로 대체가능.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-0b0591c2-6e21-4599-af7a-9c00b569c9ca\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-ef14250c-f4f5-4ae2-9c34-f3308c86b80f\">품명 및 모델명(은)는 GALAX 지포스 GTX 1660 SUPER EX WHITE OC D6 6GB.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-a93d1801-ed89-4538-a725-3372a40efc74\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-68036b9e-34f2-4109-aa25-7ac375585af4\">크기(은)는 상세페이지 참조.</span></p><!-- } SE-TEXT -->\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>                <div class=\"se-component se-text se-l-default\" id=\"SE-9bced5bd-2039-489d-9c96-1c865e6742cb\">\n" +
                    "                    <div class=\"se-component-content\">\n" +
                    "                        <div class=\"se-section se-section-text se-l-default\">\n" +
                    "                            <div class=\"se-module se-module-text\">\n" +
                    "\n" +
                    "                                    <!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-f2003025-9169-4a96-b29c-202d7a4b278c\"><span style=\"\" class=\"se-fs-fs19 se-ff-   \" id=\"SE-5493f97f-675c-4aff-8a3b-a51b3538a33a\"><b>현재가 </b></span><span style=\"color:#ae0000;\" class=\"se-fs-fs19 se-ff-   \" id=\"SE-47e7b613-50f2-48d1-b8bf-a429db277160\"><b>305,000원</b></span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-14dcf40e-48d1-42fb-9271-b5b274dcbaec\"><span style=\"\" class=\"se-fs-fs15 se-ff-   \" id=\"SE-ceaa6d98-019b-4df6-8304-b069f4b9689a\">위 가격은 2019년 12월 20일 10시 28분 기준으로 작성되었습니다.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-3b25fe93-eca2-4c8e-aee0-7187443b2d77\"><span style=\"\" class=\"se-fs-fs11 se-ff-   \" id=\"SE-a0766c15-4048-49d1-9cee-db8b38fc6946\">(작성기준의 가격이므로, 정확한 가격은 방문을 통해 확인해주세요)</span></p><!-- } SE-TEXT -->\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>                <div class=\"se-component se-oglink se-l-image\" id=\"SE-5ce0fe83-1ea0-42aa-b852-0f2eef1cdf2d\">\n" +
                    "                    <div class=\"se-component-content\">\n" +
                    "                        <div class=\"se-section se-section-oglink se-l-image se-section-align-\">\n" +
                    "                            <div class=\"se-module se-module-oglink\">\n" +
                    "                                <a href=\"https://coupa.ng/bkZFcE\" class=\"se-oglink-thumbnail\" target=\"_blank\">\n" +
                    "                                    <img src=\"https://dthumb-phinf.pstatic.net/?src=%22https%3A%2F%2Fthumbnail11.coupangcdn.com%2Fthumbnails%2Fremote%2F230x230ex%2Fimage%2Fretail%2Fimages%2F2019%2F11%2F29%2F14%2F0%2F141ae859-e049-4b80-a887-4f614405aed5.jpg%22&amp;type=ff120\" class=\"se-oglink-thumbnail-resource\" alt=\"\">\n" +
                    "                                </a>\n" +
                    "                                <a href=\"https://coupa.ng/bkZFcE\" class=\"se-oglink-info\" target=\"_blank\">\n" +
                    "                                    <div class=\"se-oglink-info-container\">\n" +
                    "                                        <strong class=\"se-oglink-title\">갤럭시 GALAX 지포스 GTX 1660 SUPER EX OC D6 6GB 그래픽카드 WHITE</strong>\n" +
                    "\n" +
                    "                                        <p class=\"se-oglink-summary\">COUPANG</p>\n" +
                    "                                        <p class=\"se-oglink-url\">coupa.ng</p>\n" +
                    "                                    </div>\n" +
                    "                                </a>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                    <script type=\"text/data\" class=\"__se_module_data\" data-module=\"{&quot;type&quot;:&quot;v2_oglink&quot;, &quot;id&quot; :&quot;SE-5ce0fe83-1ea0-42aa-b852-0f2eef1cdf2d&quot;, &quot;data&quot; : {&quot;link&quot; : &quot;https://coupa.ng/bkZFcE&quot;, &quot;isVideo&quot; : &quot;false&quot;, &quot;thumbnail&quot; : &quot;https://dthumb-phinf.pstatic.net/?src=%22https%3A%2F%2Fthumbnail11.coupangcdn.com%2Fthumbnails%2Fremote%2F230x230ex%2Fimage%2Fretail%2Fimages%2F2019%2F11%2F29%2F14%2F0%2F141ae859-e049-4b80-a887-4f614405aed5.jpg%22&amp;type=ff120&quot;}}\"></script>\n" +
                    "                </div>                <div class=\"se-component se-text se-l-default\" id=\"SE-db39e7f7-ad22-4893-b9be-d2065ae249b1\">\n" +
                    "                    <div class=\"se-component-content\">\n" +
                    "                        <div class=\"se-section se-section-text se-l-default\">\n" +
                    "                            <div class=\"se-module se-module-text\">\n" +
                    "\n" +
                    "                                    <!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-32fd8fe6-98eb-4736-92fd-43a71600d083\"><span style=\"\" class=\"se-fs-fs28 se-ff-   \" id=\"SE-9d4db1e5-d401-4096-8657-fb1cb0166e84\"><a href=\"https://coupa.ng/bkZFcE\" class=\"se-link\" target=\"_blank\"><b>▶ 상품평 확인하기 ◀</b></a></span></p><!-- } SE-TEXT -->\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>                <div class=\"se-component se-sticker se-l-default\" id=\"SE-7ba592b6-e6e6-4881-8177-dcaf783deed2\">\n" +
                    "                    <div class=\"se-component-content\">\n" +
                    "                        <div class=\"se-section se-section-sticker se-section-align- se-l-default\">\n" +
                    "                            <div class=\"se-module se-module-sticker\">\n" +
                    "                                <a href=\"#\" onclick=\"return false;\" class=\"__se_sticker_link __se_link\" data-linktype=\"sticker\" data-linkdata=\"{&quot;src&quot; : &quot;https://storep-phinf.pstatic.net/linesoft_01/original_6.gif&quot;, &quot;packCode&quot; : &quot;linesoft_01&quot;, &quot;seq&quot; : &quot;6&quot;, &quot;width&quot; : &quot;185&quot;, &quot;height&quot; : &quot;160&quot;}\">\n" +
                    "                                    <img src=\"https://storep-phinf.pstatic.net/linesoft_01/original_6.gif?type=pa50_50\" alt=\"\" class=\"se-sticker-image\">\n" +
                    "                                </a>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>                <div class=\"se-component se-text se-l-default\" id=\"SE-29076bb3-d218-4551-baad-3b768e29bc3b\">\n" +
                    "                    <div class=\"se-component-content\">\n" +
                    "                        <div class=\"se-section se-section-text se-l-default\">\n" +
                    "                            <div class=\"se-module se-module-text\">\n" +
                    "\n" +
                    "                                    <!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-ced4160d-4115-4830-8fa8-a56513b3c7ac\"><span style=\"\" class=\"se-fs- se-ff-   \" id=\"SE-3174bf3c-a4fd-4adf-b8c7-92a6cdd0bae1\">해당 포스팅은 작성시간 기준의 정보로 작성되었으며, 시간 경과에 따라 일부 내용에 차이가 있을 수 있습니다.</span></p><!-- } SE-TEXT --><!-- SE-TEXT { --><p class=\"se-text-paragraph se-text-paragraph-align- \" style=\"\" id=\"SE-c279656f-a73e-486e-ad70-168a5da721e9\"><span style=\"color:#bc61ab;\" class=\"se-fs- se-ff-   \" id=\"SE-c1ea4536-1bf0-481c-bf02-72c5b21711fb\">해당 포스팅은 쿠팡 파트너스 활동의 일환으로, 이에 따른 일정액의 수수료를 제공받고 있습니다.</span></p><!-- } SE-TEXT -->\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>                <div class=\"se-component se-image se-l-default\" id=\"SE-6be1bc06-8b38-491f-8924-736ef26eece8\">\n" +
                    "                    <div class=\"se-component-content se-component-content-normal\">\n" +
                    "                        <div class=\"se-section se-section-image se-l-default se-section-align-\" style=\"max-width:577px;\">\n" +
                    "\n" +
                    "                                <a href=\"#\" class=\"se-module se-module-image __se_image_link __se_link\" style=\" \" onclick=\"return false;\" data-linktype=\"img\" data-linkdata=\"{&quot;id&quot; : &quot;SE-6be1bc06-8b38-491f-8924-736ef26eece8&quot;, &quot;src&quot; : &quot;https://postfiles.pstatic.net/MjAxOTEyMjBfMTkg/MDAxNTc2ODA1Mzg4OTMw.hIZNebaW3FCcmHc3L0dTf1OgHP-UZZJJtTmne6PPC8Ug.I4Y9LpwziQ7TghH-zPSR3fsV5s5TKhtFyhMyIEEa2i0g.PNG.dungen/SE-d21570b3-13b9-4ca1-ad2e-7e44a2f4c89c.png&quot;, &quot;linkUse&quot; : &quot;false&quot;, &quot;link&quot; : &quot;&quot;}\">\n" +
                    "                                    <img src=\"https://postfiles.pstatic.net/MjAxOTEyMjBfMTkg/MDAxNTc2ODA1Mzg4OTMw.hIZNebaW3FCcmHc3L0dTf1OgHP-UZZJJtTmne6PPC8Ug.I4Y9LpwziQ7TghH-zPSR3fsV5s5TKhtFyhMyIEEa2i0g.PNG.dungen/SE-d21570b3-13b9-4ca1-ad2e-7e44a2f4c89c.png?type=w773\" data-lazy-src=\"\" data-width=\"577\" data-height=\"577\" alt=\"\" class=\"se-image-resource\">\n" +
                    "                                </a>                        </div>\n" +
                    "                    </div>\n" +
                    "                </div>    </div>\n" +
                    "</div>\n" +
                    "\t\t\t\t\t\t</div>") ;//내용
            contents.put("tags", "망고, 쿠팡, 네이버"); // 태크 콤마로 구분한다.


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

            System.out.println(rsString);

        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}



