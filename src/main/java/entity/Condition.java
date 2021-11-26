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
        public String value;
    }

    public Condition() {
        leftValue = new Value();
        rightValue = new Value();
    }
    //操作类型
    public enum opType{GREATER_THAN, EQUAL_TO, MIN_THAN, GREATER_EQUAL_TO,MIN_EQUAL_TO,NOT_EQUAL,EQUAL_TO_STRING,NOT_EQUAL_TOSTRING};
    public String origin = "";
    public Value leftValue;
    public Value rightValue;
    public opType op;
    public boolean isJoin;

    public String toJson() {
        return JSON.toJSONString(this);
    }

    //传进来的必须是两个属性名相同的condition，比较两个Condition是否冲突，true表示冲突，前提是condition1和condition2都是,TO-DO默认左边是属性，右边是值
    public boolean isConflict(Condition c1) {
        if(this.op == opType.GREATER_THAN && (c1.op == opType.EQUAL_TO || c1.op == opType.MIN_THAN ||c1.op == opType.MIN_EQUAL_TO )) {
            if(Double.parseDouble(c1.rightValue.value) <= Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.EQUAL_TO && (c1.op == opType.GREATER_EQUAL_TO) ) {
            if(Double.parseDouble(c1.rightValue.value) > Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.EQUAL_TO && (c1.op == opType.GREATER_THAN) ) {
            if(Double.parseDouble(c1.rightValue.value) >= Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.EQUAL_TO && (c1.op == opType.EQUAL_TO) ) {
            if(Double.parseDouble(c1.rightValue.value) != Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.EQUAL_TO && (c1.op == opType.MIN_THAN) ) {
            if(Double.parseDouble(c1.rightValue.value) <= Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.EQUAL_TO && (c1.op == opType.MIN_EQUAL_TO) ) {
            if(Double.parseDouble(c1.rightValue.value) < Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.EQUAL_TO && (c1.op == opType.NOT_EQUAL) ) {
            if(Double.parseDouble(c1.rightValue.value) == Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.MIN_THAN &&(c1.op == opType.GREATER_THAN || c1.op == opType.EQUAL_TO ||c1.op == opType.GREATER_EQUAL_TO)) {
            if(Double.parseDouble(c1.rightValue.value) >= Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.GREATER_EQUAL_TO &&(c1.op == opType.EQUAL_TO || c1.op == opType.MIN_EQUAL_TO)) {
            if(Double.parseDouble(c1.rightValue.value) < Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.GREATER_EQUAL_TO &&(c1.op == opType.MIN_THAN)) {
            if(Double.parseDouble(c1.rightValue.value) <= Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.MIN_EQUAL_TO &&(c1.op == opType.EQUAL_TO ||c1.op == opType.GREATER_EQUAL_TO)) {
            if(Double.parseDouble(c1.rightValue.value) > Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.MIN_EQUAL_TO &&(c1.op == opType.GREATER_THAN)) {
            if(Double.parseDouble(c1.rightValue.value) >= Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op ==opType.NOT_EQUAL &&(c1.op == opType.EQUAL_TO)) {
            if(Double.parseDouble(c1.rightValue.value) == Double.parseDouble(this.rightValue.value)) {
                return true;
            }
        }
        if(this.op == opType.EQUAL_TO_STRING && c1.op == opType.EQUAL_TO_STRING) {
            if(!c1.rightValue.equals(this.rightValue)) {
                return true;
            }
        }
        if(this.op == opType.EQUAL_TO_STRING && c1.op == opType.NOT_EQUAL_TOSTRING) {
            if(c1.rightValue.equals(this.rightValue)) {
                return true;
            }
        }
        if(this.op == opType.NOT_EQUAL_TOSTRING && c1.op == opType.EQUAL_TO_STRING) {
            if(c1.rightValue.equals(this.rightValue)) {
                return true;
            }
        }
        return false;
    }
    public static Condition fromJson(String json) {
        return JSONObject.parseObject(json, Condition.class);
    }
}
