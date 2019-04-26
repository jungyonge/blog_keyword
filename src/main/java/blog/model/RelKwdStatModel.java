package blog.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RelKwdStatModel {

        public List<Keyword> keywordList;

        @Data
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
            private int totalPost;
            private int naverCnt;
            private int tstoryCnt;
            private int elseCnt;
        }
    }

