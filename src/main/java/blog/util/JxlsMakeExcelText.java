package blog.util;

import blog.mybatis.MyBatisConnectionFactory;
import blog.mybatis.SetalarmDAO;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleItem;
import org.springframework.ui.ModelMap;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JxlsMakeExcelText {

    private SetalarmDAO setalarmDAO = new SetalarmDAO(MyBatisConnectionFactory.getSqlSessionFactory());

    public void statXlsDown(String type) throws Exception {
        ModelMap model = new ModelMap();
        String excelTemplatePath;

        List<String> sheetNames = new ArrayList<String>();//시트 이름 리스트
        List<HashMap<String, Object>> sheetMaps = new ArrayList<HashMap<String, Object>>();//시트리스트
        HashMap<String, Object> sheetMap = new HashMap<String, Object>();//각시트단위

        HashMap<String, Object> data = new HashMap<String, Object>();
        List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();


        sheetNames.add(type);
        sheetMap = new HashMap<String, Object>();
        sheetMap.put("special",setalarmDAO.selectBasketSpecialSummary());
        sheetMap.put("handi",setalarmDAO.selectBasketQuarterHandiOverSummary());
        sheetMap.put("specialground",setalarmDAO.selectBasketSpecialGroundSummary());
        sheetMap.put("handiground",setalarmDAO.selectBasketQuarterHandiOverGroundSummary());
        sheetMap.put("specialcombo",setalarmDAO.selectBasketSpecialComboSummary());
        sheetMap.put("handicombo",setalarmDAO.selectBasketQuarterHandiComboSummary());
        sheetMap.put("full",setalarmDAO.selectBasketHandiOverSummary());
        sheetMap.put("fullground",setalarmDAO.selectBasketHandiOverGroundSummary());
        sheetMap.put("summary",setalarmDAO.selectAllSummary());

        List list = setalarmDAO.selectAllSummary();

        sheetMap.put("count",setalarmDAO.selectBasketSpecialSummary().size());
        sheetMaps.add(sheetMap);





        excelTemplatePath ="C:/Users/qjsro/IdeaProjects/blog_keyword/src/main/resources/excelTemplate/";
        // 세션값 얻기
        List excelDataList = new ArrayList<>();


        Map<String, Object> paramMap = new HashMap<String, Object>();

        model.put("EXCEL_TEMPLATE_PATH", excelTemplatePath);

        model.put("excelTemplateName", type);
        model.put("excelOutputName", type);
        model.addAttribute("sheetMaps",sheetMaps);
        model.addAttribute("sheetNames",sheetNames);


        HSSFWorkbook workbook = new HSSFWorkbook();
        buildExcelDocument(model);
    }


    private static final String FILE_EXT1 = ".xls";
    private static final String FILE_EXT2 = ".xlsx";
    private static final String SHEET_NAME = "sheet";

    protected void buildExcelDocument(Map<String, Object> model) throws Exception {



        Workbook workbook;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df1 = new SimpleDateFormat("yyyyMMdd");

        String excelTemplatePath = (String)model.get("EXCEL_TEMPLATE_PATH");
        String excelTemplateName = (String)model.get("excelTemplateName");
        String excelOutputName = (String)model.get("excelOutputName");
        List<HashMap<String, Object>> sheetMaps = (List<HashMap<String, Object>>) model.get("sheetMaps");
        List<String> sheetNames =  (List<String>) model.get("sheetNames");


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


            XLSTransformer transformer = new XLSTransformer();
            Map<String, Object> resultMap = new HashMap<String, Object>();


//            if(resultList != null && resultList.size() > 0) { // 데이터가 존재하는 경우 템플릿 출력
                workbook  = transformer.transformMultipleSheetsList(inputStream, sheetMaps, sheetNames, "sheetMap", new HashMap(), 0);
//            } else { // 데이터가 없는경우 빈템플릿 출력
//                resultMap.put("resultList", resultList);
//                workbook =  transformer.transformXLS(inputStream, new HashMap());
//            }

            String fileName = null;

            String fileName1 = excelOutputName +"_"+ df1.format(cal.getTime()) + ".xls";
            FileOutputStream fos = new FileOutputStream("D:test/" + fileName1);
            workbook.write(fos);
        }

    }



    public static void main(String[] args) {
        JxlsMakeExcelText jxlsMakeExcel = new JxlsMakeExcelText();
        List excelDataList = new ArrayList<>();
        try {
            jxlsMakeExcel.statXlsDown("basketball_summary");

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
