package blog.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;


public class JxlsExcelUtil {

    private static final String XLS = ".xls";
    private static final String XLS_CONTENT_TYPE = "excel";

    private static final String XLSX = ".xlsx";
    private static final String XLSX_CONTENT_TYPE = "officedocument";

    public static <T> View exportJxlsExcel(ModelMap model, List<T> list, T tModel) {
        return exportJxlsExcel(model, list, tModel, null);
    }

    public static <T> View exportJxlsExcel(ModelMap model, List<T> list, T tModel, Map<String, Object> headerInfos) {
        model.addAttribute("list", list);
        model.addAttribute("params", model);
        model.addAttribute("vo", tModel);
        model.addAttribute("headerInfos", headerInfos);

        return new JxlsExcelView();
    }

    public static <T> View exportJxlsExcelMultiSheet(ModelMap model, List<T> list, String sheetName, T tModel, Map<String, Object> headerInfos) {
        model.addAttribute("list", list);
        model.addAttribute("sheetName", sheetName);
        model.addAttribute("params", model);
        model.addAttribute("vo", tModel);
        model.addAttribute("headerInfos", headerInfos);

        return new JxlsExcelMultiSheet();
    }

    public static String getExcelType(MultipartFile file) {
        String contentType = null;
        String result = null;

        if(file != null && !file.isEmpty()) {
            contentType = file.getContentType();

            System.out.println("################################"+contentType+"###################################");
            if(contentType.indexOf(XLS_CONTENT_TYPE) > -1) {
                result = XLS; // xls 타입인 경우
            } else if(contentType.indexOf(XLSX_CONTENT_TYPE) > -1) {
                result = XLSX; // xlsx 타입이 아닌경우
            }
        }

        return result;
    }

    public static List<List<String>> readExcel(MultipartFile file) {
        List<List<String>> resultList = null;

        if(XLS.equals(getExcelType(file))) { // xls 타입인 경우
            resultList = readXls(file);
        } else if(XLSX.equals(getExcelType(file))) { // xlsx 타입인 경우
            resultList = readXlsx(file);
        }

        return resultList;
    }

    public static List<List<String>> readXls(MultipartFile file) {
        List<List<String>> resultList = null;
        List<String> tempRow = null;
        InputStream inputStream = null;
        HSSFWorkbook workbook = null;
        HSSFSheet activeSheet = null;
        HSSFRow row = null;
        HSSFCell cell = null;

        int rows = 0;
        int cells = 0;

        if(!file.isEmpty()) {
            try {
                inputStream = file.getInputStream();

                if(inputStream != null) {
                    workbook = new HSSFWorkbook(inputStream);
                    activeSheet = workbook.getSheetAt(0);
                    rows = activeSheet.getPhysicalNumberOfRows();
                    resultList = new ArrayList<List<String>>();

                    for(int i = 0; i < rows; i++) {
                        row = activeSheet.getRow(i);
                        tempRow = new ArrayList<String>();

                        if(row != null) {
                            cells = row.getPhysicalNumberOfCells();

                            for(int j = 0; j < cells; j++) {
                                cell = row.getCell(j);
                                tempRow.add(cell.toString());
                            }
                        }

                        resultList.add(tempRow);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        return resultList;
    }

    public static List<List<String>> readXlsx(MultipartFile file) {
        List<List<String>> resultList = null;
        List<String> tempRow = null;
        InputStream inputStream = null;
        XSSFWorkbook workbook = null;
        XSSFSheet activeSheet = null;
        XSSFRow row = null;
        XSSFCell cell = null;

        int rows = 0;
        int cells = 0;

        if(!file.isEmpty()) {
            try {
                inputStream = file.getInputStream();

                if(inputStream != null) {
                    workbook = new XSSFWorkbook(inputStream);
                    activeSheet = workbook.getSheetAt(0);
                    rows = activeSheet.getPhysicalNumberOfRows();
                    resultList = new ArrayList<List<String>>();

                    for(int i = 0; i < rows; i++) {
                        row = activeSheet.getRow(i);
                        tempRow = new ArrayList<String>();

                        if(row != null) {
                            cells = row.getPhysicalNumberOfCells();

                            for(int j = 0; j < cells; j++) {
                                cell = row.getCell(j);
                                tempRow.add(cell.toString());
                            }
                        }

                        resultList.add(tempRow);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        return resultList;
    }

    public static List<String> getFilterList(MultipartFile file, String filterHeader) {
        return getFilterList(readExcel(file), filterHeader);
    }

    public static int getFilterIndex(List<List<String>> list, String filterHeader) {
        int headerIndex = 0;

        if(!list.isEmpty() && list.size() > 0) {
            for(int i = 0; i < list.get(0).size(); i++) {
                if(filterHeader.equals(list.get(0).get(i))) {
                    headerIndex = i;
                    break;
                }
            }
        }

        return headerIndex;
    }

    public static List<String> getFilterList(List<List<String>> list, String filterHeader) {
        List<String> filterList = null;
        filterList = new ArrayList<String>();
        int headerIndex = getFilterIndex(list, filterHeader);

        for(int i = 1; i < list.size(); i++) {
            filterList.add(list.get(i).get(headerIndex));
        }

        return filterList;
    }
}