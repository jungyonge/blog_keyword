package blog.util;

import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSheetConditionalFormatting;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.ui.ModelMap;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JxlsMakeExcel {

    private SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    public void statXlsDown(String type) throws Exception {
        ModelMap model = new ModelMap();
        String excelTemplatePath;

        excelTemplatePath ="C:/Users/qjsro/IdeaProjects/blog_keyword/src/main/resources/excelTemplate/";
        // 세션값 얻기
        List excelDataList = new ArrayList<>();

        excelDataList = setalarmDAO.selectBasketStat();
        Map<String, Object> paramMap = new HashMap<String, Object>();

        model.put("EXCEL_TEMPLATE_PATH", excelTemplatePath);

        model.put("excelTemplateName", "basketball");
        model.put("excelOutputName", "basketball");
        model.put("list",excelDataList);

        HSSFWorkbook workbook = new HSSFWorkbook();
        buildExcelDocument(model);
//        JxlsExcelUtil.exportJxlsExcel(model, excelDataList, null, paramMap);
    }

    private static final String[] fileList = { "basketball","volleyball","hockey","soccer"};

    private static final String FILE_EXT1 = ".xls";
    private static final String FILE_EXT2 = ".xlsx";
    private static final String SHEET_NAME = "sheet";

    protected void buildExcelDocument(Map<String, Object> model) throws Exception {



        Workbook workbook;
        List resultList = (List)model.get("list");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df1 = new SimpleDateFormat("yyyyMMddhhmmss");

        String excelTemplatePath = (String)model.get("EXCEL_TEMPLATE_PATH");
        String excelTemplateName = (String)model.get("excelTemplateName");
        String excelOutputName = (String)model.get("excelOutputName");

        if(!StringUtils.isEmpty(excelTemplatePath) && !StringUtils.isEmpty(excelTemplateName) && !StringUtils.isEmpty(excelOutputName)) {



            System.out.println(1111);
            InputStream inputStream = null;
            String ext = FILE_EXT1;

//            FileInputStream file = new FileInputStream(new File(excelTemplatePath + excelTemplateName + FILE_EXT1));
//            HSSFWorkbook workbook1 = new HSSFWorkbook(file);
//            HSSFSheet sheet1 = workbook1.getSheet("sheet3");
//            HSSFSheetConditionalFormatting cf =sheet1.getSheetConditionalFormatting();
//

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


            if(resultList != null && resultList.size() > 0) { // 데이터가 존재하는 경우 템플릿 출력
//                resultMap.put("resultList", resultList);
//                workbook = (XSSFWorkbook) transformer.transformXLS(inputStream, resultMap);
                workbook =  transformer.transformMultipleSheetsList(inputStream, rowList, sheetNameList, "resultList", resultMap, 0);
            } else { // 데이터가 없는경우 빈템플릿 출력
                resultMap.put("resultList", resultList);
                workbook =  transformer.transformXLS(inputStream, resultMap);
            }

            String fileName = null;

            String fileName1 = fileList[0] +"_"+ df1.format(cal.getTime()) + ".xls";
            FileOutputStream fos = new FileOutputStream("D:test/" + fileName1);
            workbook.write(fos);
        }

    }



    public static void main(String[] args) {
        JxlsMakeExcel jxlsMakeExcel = new JxlsMakeExcel();
        List excelDataList = new ArrayList<>();
        try {
            jxlsMakeExcel.statXlsDown("bas");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }

}
