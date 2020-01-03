package blog.util;

import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.lang.StringUtils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class JxlsExcelView extends AbstractExcelView {

    private static final String[] fileList = { "basketball","volleyball","hockey","soccer"};

    private static final Logger logger = LoggerFactory.getLogger(JxlsExcelView.class);
    private static final String FILE_EXT1 = ".xls";
    private static final String FILE_EXT2 = ".xlsx";
    private static final String SHEET_NAME = "sheet";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      HSSFWorkbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        List resultList = (List)model.get("list");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df1 = new SimpleDateFormat("yyyyMMdd");


        //logger.debug("resultList ========> " + resultList);
        logger.debug("resultList size ========> " + resultList.size());

        Map<String, Object> params = (Map<String, Object>)model.get("params");
        Map<String, Object> headerInfos = (Map<String, Object>)model.get("headerInfos");

        String excelTemplatePath = (String)params.get("EXCEL_TEMPLATE_PATH");
        String excelTemplateName = (String)params.get("excelTemplateName");
        String excelOutputName = (String)params.get("excelOutputName");

        if(!StringUtils.isEmpty(excelTemplatePath) && !StringUtils.isEmpty(excelTemplateName) && !StringUtils.isEmpty(excelOutputName)) {

            logger.debug("=======================================================>");
            logger.debug("엑셀 템플릿 경로 ========> " + excelTemplatePath);
            logger.debug("엑셀 템플릿 이름 ========> " + excelTemplateName);
            logger.debug("엑셀 템플릿 output 이름   ========> " + excelOutputName);
            logger.debug("=======================================================>");

            InputStream inputStream = null;
            String ext = FILE_EXT1;
            try {
                // 엑셀 템플릿을 스트림으로 읽어온다
                inputStream = new BufferedInputStream(new FileInputStream(excelTemplatePath + excelTemplateName + FILE_EXT1));
                ext = FILE_EXT1;
            } catch (Exception e) {
                inputStream = new BufferedInputStream(new FileInputStream(excelTemplatePath + excelTemplateName + FILE_EXT2));
                ext = FILE_EXT2;
            }

            List tempRowList = null;
            List<List> rowList = null;
            List<String> sheetNameList = null;

            if(resultList != null && resultList.size() > 0) {

                tempRowList = new ArrayList();
                rowList = new ArrayList<List>();
                sheetNameList = new ArrayList<String>();

                int sheetNumber = 1;
                int currentRow = 1;

                for(int index = 0; index < resultList.size(); index++) {
                    tempRowList.add(resultList.get(index));

                    if(currentRow == 1) { // 최초 시트
                        sheetNameList.add(SHEET_NAME + sheetNumber);
                        if(currentRow == resultList.size()) {
                            rowList.add(tempRowList);
                        }
                    } else if(currentRow % 65000 == 0) { // 한페이지 로우수를 65000개로 제한하고 나머지 로우를 다음시트에 add
                        rowList.add(tempRowList);
                        sheetNumber++;
                        sheetNameList.add(SHEET_NAME + sheetNumber);
                        tempRowList = new ArrayList();
                    } else if(currentRow == resultList.size()) { // 최종 시트
                        rowList.add(tempRowList);
                    }

                    currentRow++;
                }
            }

            XLSTransformer transformer = new XLSTransformer();
            Map<String, Object> resultMap = new HashMap<String, Object>();

            resultMap.put("headerInfos", headerInfos); // 템플릿 헤더에 사용할 정보

            if(resultList != null && resultList.size() > 0) { // 데이터가 존재하는 경우 템플릿 출력
                workbook = (HSSFWorkbook) transformer.transformMultipleSheetsList(inputStream, rowList, sheetNameList, "resultList", resultMap, 0);
            } else { // 데이터가 없는경우 빈템플릿 출력
                resultMap.put("resultList", resultList);
                workbook = (HSSFWorkbook) transformer.transformXLS(inputStream, resultMap);
            }

            String userAgent = request.getHeader("user-Agent");
            String fileName = null;

            if(userAgent.indexOf("MSIE") > -1) {
                fileName = URLEncoder.encode(excelOutputName, "utf-8");
            } else {
                fileName = new String((excelOutputName).getBytes("utf-8"), "iso-8859-1");
            }

            fileName = new StringBuilder(fileName).append("-").append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())).append(ext).toString();

            response.setContentType(getContentType());
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\";");

            OutputStream out = response.getOutputStream();
            String fileName1 = fileList[0] +"_"+ df1.format(cal.getTime()) + ".xlsx";
            FileOutputStream fos = new FileOutputStream("/Users/imc053/Desktop/xmlFile/test.xlsx");
            workbook.write(fos);
            workbook.write(out);
            out.flush();
        }

    }

}