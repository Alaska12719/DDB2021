package network;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TempTable {
    public boolean isLeaf;
    public boolean isUnion;
    public String project;
    public String condition;
    public String joinAttribute;
    public String tableName;
    public List<String> children;
    public List<String> ips;

    public String toJson() {
        return JSON.toJSONString(this);
    }
    
    public static TempTable fromJson(String json) {
        return JSONObject.parseObject(json, TempTable.class);
    }
}