package entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import network.TempTable;

/**
 * @author Kalven
 * @version 10.0
 * Created by Kalven on 2021/11/23
 */
public class Condition {
    public class Value {
        public Value() {

        }

        public Boolean isAttribute;
        public String attrName;
        public double value;
    }

    public Condition() {
        leftValue = new Value();
        rightValue = new Value();
    }
    //操作类型
    public enum opType{GREATER_THAN, EQUAL_TO, MIN_THAN, GREATER_EQUAL_TO,MIN_EQUAL_TO,NOT_EQUAL};
    public Value leftValue;
    public Value rightValue;
    public opType op;

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static Condition fromJson(String json) {
        return JSONObject.parseObject(json, Condition.class);
    }
}
