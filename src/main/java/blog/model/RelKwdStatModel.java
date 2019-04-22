package blog.model;

import lombok.Data;

import java.util.List;

@Data
public class RelKwdStatModel {

        public List<Keyword> keywordList = null;

        public class Keyword {
            private String relKeyword;
            private String monthlyPcQcCnt;
            private String monthlyMobileQcCnt;
            private String monthlyAvePcClkCnt;
            private String monthlyAveMobileClkCnt;
            private String monthlyAvePcCtr;
            private String monthlyAveMobileCtr;
            private String plAvgDepth;
            private String compIdx;
        }
    }

