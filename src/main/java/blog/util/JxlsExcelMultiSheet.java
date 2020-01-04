package blog.util;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class JxlsExcelMultiSheet extends AbstractExcelView {


    private static final Logger logger = LoggerFactory.getLogger(JxlsExcelMultiSheet.class);
    private static final String FILE_EXT1 = ".xls";
    private static final String FILE_EXT2 = ".xlsx";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      HSSFWorkbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        List resultList = (List) model.get("list");

        String sheetName = (String) model.get("sheetName");

        logger.debug("resultList ========> " + resultList);
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


            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("headerInfos", headerInfos); // 템플릿 헤더에 사용할 정보

            List<String> sheetNameList = new ArrayList<>();

            String[] sheetNameArray = sheetName.split(",");
            for (int i = 0; i < sheetNameArray.length; i++) {
                sheetNameList.add(sheetNameArray[i]);
            }

            XLSTransformer transformer = new XLSTransformer();

            if(resultList != null && resultList.size() > 0) { // 데이터가 존재하는 경우 템플릿 출력
                workbook = (HSSFWorkbook) transformer.transformMultipleSheetsList(inputStream, resultList, sheetNameList, "resultList", resultMap, 0);
            } else { // 데이터가 없는경우 빈템플릿 출력
                resultMap.put("resultList", resultList);
                resultMap.put("resultList.list", resultList);
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
            workbook.write(out);
            out.flush();
        }

    }

	/*public List jsonToArray(JSONArray json) {
		List list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		for (int i = 0; i < json.size(); i++) {
			JSONObject obj = (JSONObject) json.get(i);

			@SuppressWarnings("rawtypes")
			Iterator iter = obj.keys();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				map.put(key, value)
			}
		}

		return list;
	}*/
}