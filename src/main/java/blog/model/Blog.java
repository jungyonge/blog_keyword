package blog.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class Blog {

    public String lastBuildData;
    public String total;
    public List<Item> items;

        public class Item{
            public String title;
            public String link;
            public String description;
        }

}
