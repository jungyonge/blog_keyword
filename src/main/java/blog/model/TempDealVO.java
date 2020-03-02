package blog.model;

import lombok.Data;

import java.util.Date;

@Data
public class TempDealVO {
    String idx;
    String sdid;
    int dealrank;
    String dealName;
    String cate;
    String dcPrice;
    String nmPrice;
    String dcRatio;
    String productUrl;
    String imgUrl;
    String postid;

}
