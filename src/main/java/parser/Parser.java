package parser;

import entity.AttributeConstant;
import entity.Condition;
import entity.FragmentConstant;
import entity.RelationConstant;
import entity.SiteConstant;
import entity.TreeNode;
import utils.*;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import network.EtcdClient;
import network.SqlClient;
import network.SqlServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.relation.Relation;
import javax.swing.text.AbstractDocument.Content;

import javafx.util.*;

import network.Constants;
/**
 * @author Kalven
 * @version 10.0 Created by Kalven on 2021/11/5
 */

public class Parser {
    private EtcdClient etcdClient;

    public Parser() {
        etcdClient = new EtcdClient(Constants.ETCD_ENDPOINTS);
    }



    public void closeEtcd() {
        etcdClient.close();
    }
    
    public void deleteAllEtcd() throws Exception {
        etcdClient.removeByPrefix("");
    }
    public enum InputState {
        SELECT, INSERT, DEFINESITE, ERROR, CREATETABLE, FRAGMENT, ALLOCATE, LOAD, DELETE,LOAD_DATA
    }

    ;

    public void parseInput(String input) throws Exception {
        switch (getInputType(input)) {
            case SELECT:
                parseSelectSql(input);
                break;
            case DEFINESITE:
                parseDefineSite(input);
                break;
            case CREATETABLE:
                createTable(input);
                break;
            case FRAGMENT:
                createFragment(input);
                break;
            case ALLOCATE:
                allocateFragment(input);
                break;
            case INSERT:
                parse_insert(input);
                break;
            case DELETE:
                parse_delete(input);
                break;
            case LOAD_DATA:
                loadTSV(input.substring(9).trim());
                break;
            // case LOAD:
                default: executeSql(input);
        }
    }

    // TO-DO 解析输入类型
    private InputState getInputType(String input) {
        if (input.contains("define site")) {
            return InputState.DEFINESITE;
        }
        if (input.contains("create table")) {
            return InputState.CREATETABLE;
        }
        if (input.contains("fragment")) {
            return InputState.FRAGMENT;
        }
        if (input.contains("allocate")) {
            return InputState.ALLOCATE;
        }
        if (input.contains("select")) {
            return InputState.SELECT;
        }
        //TO-DO 解析sql
        if (input.contains("insert")) {
            return InputState.INSERT;
        }
        if(input.contains("delete")) {
            return InputState.DELETE;
        }
        if(input.contains("LOAD_DATA")) {
            return InputState.LOAD_DATA;
        }
        // if(input.contains("load")) {
        //     return InputState.LOAD;
        // }
        return InputState.ERROR;
    }

    private void parse_delete(String input) throws Exception {
        String fragments = "";
        FragmentConstant Constant = new FragmentConstant("");
        fragments += etcdClient.get(Constant.getFragments());
        List<String> fragmentList = new ArrayList<>();
        Delete deleteStatement = (Delete) CCJSqlParserUtil.parse(input);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        //单表情况下
        String tableName = tablesNamesFinder.getTableList(deleteStatement).get(0);
        String[] fragmentSplits = fragments.split(",");
        for(int i = 0; i < fragmentSplits.length; i++) {
            if(fragmentSplits[i].contains(tableName)) {
                fragmentList.add(fragmentSplits[i]);
            }
        }
        FragmentConstant fragmentConstant = new FragmentConstant(fragmentList.get(0));
        String isHorizon = etcdClient.get(fragmentConstant.getIsHorizontal());
        //水平分片
        if(isHorizon.contains("1")) {
            executeSql(input);
        } else {
            String where = "";
            if(deleteStatement.getWhere() == null) {
                executeSql(input);
            } else {
                where = deleteStatement.getWhere().toString();
                String select = "select id from " + tableName + " where " + where;
                List<String> ids = parseSelectSql(select);
                if(ids.size() == 1) {
                    return;
                }
                String sql = "delete from " + tableName + " where ";
                for(int i = 1; i < ids.size() - 1; i++) {
                    sql += "id=" + ids.get(i).substring(1,ids.get(i).length() - 1)+ " or ";
                }
                sql += "id=" + ids.get(ids.size() - 1).substring(1,ids.get(ids.size() - 1).length() - 1);
                executeSql(sql);
            }
        }
    }

    private void parse_insert(String input) throws Exception {
        String fragments = "";
        FragmentConstant Constant = new FragmentConstant("");
        fragments += etcdClient.get(Constant.getFragments());
        List<String> fragmentList = new ArrayList<>();
        Insert insertStatement = (Insert) CCJSqlParserUtil.parse(input);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        //单表情况下
        String tableName = tablesNamesFinder.getTableList(insertStatement).get(0);
        String[] fragmentSplits = fragments.split(",");
        for(int i = 0; i < fragmentSplits.length; i++) {
            if(fragmentSplits[i].contains(tableName)) {
                fragmentList.add(fragmentSplits[i]);
            }
        }
        FragmentConstant fragmentConstant = new FragmentConstant(fragmentList.get(0));
        RelationConstant relationConstant = new RelationConstant(tableName);
        String isHorizon = etcdClient.get(fragmentConstant.getIsHorizontal());
        String etcdAttribute = etcdClient.get(relationConstant.getAttributes());
        if(etcdAttribute.charAt(0) == ',') {
            etcdAttribute = etcdAttribute.substring(1);
        }
        String[] attributes = etcdAttribute.split(",");
        //水平分片
        if(isHorizon.contains("1")) {
            List<Condition> conditions = new ArrayList<>();
            ExpressionList columns = (ExpressionList)insertStatement.getItemsList();
            for(int i = 0; i < attributes.length; i++) {
                String s = attributes[i] + "=" + columns.getExpressions().get(i).toString();
                Condition condition = parseCondition(s, tableName, false);
                conditions.add(condition);
            }
            for(int i = 0; i < fragmentList.size(); i++) {
                FragmentConstant fConstant = new FragmentConstant(fragmentList.get(i));
                String[] fragmentConditions = etcdClient.get(fConstant.getConditions()).split("%");
                Boolean isConflict = false;
                for(int j = 0; j < fragmentConditions.length; j++) {
                    for(int k = 0; k < conditions.size(); k++) {
                        Condition fragmentCondition = Condition.fromJson(fragmentConditions[j]);
                        if(conditions.get(k).leftValue.attrName.equals(fragmentCondition.leftValue.attrName)) {
                            if(conditions.get(k).isConflict(fragmentCondition)) {
                                isConflict = true;
                                break;
                            }
                        }
                    }
                    if(isConflict) {
                        break;
                    }
                }
                if(!isConflict) {
                    String site = etcdClient.get(fConstant.getSite());
                    SqlClient sqlClient = new SqlClient(site);
                    sqlClient.executeNonQuery(input);
                    sqlClient.close();
                    break;
                }
            }
            
        } else {
            //垂直分片
            Map<String, Integer> map = new HashMap<>();
            for(int i = 0; i < attributes.length; i++) {
                map.put(attributes[i], i);
            }
            ExpressionList columns = (ExpressionList)insertStatement.getItemsList();
            for(int i = 0; i < fragmentList.size(); i++) {
                FragmentConstant fConstant = new FragmentConstant(fragmentList.get(i));
                String[] fragmentAttrs = etcdClient.get(fConstant.getAttributes()).split(",");
                String sql = "insert into " + tableName + " (";
                for(int j = 0; j < fragmentAttrs.length - 1; j++) {
                    sql += fragmentAttrs[j] + ",";
                }
                sql += fragmentAttrs[fragmentAttrs.length - 1] + ") values(";
                for(int j = 0; j < fragmentAttrs.length - 1; j++) {
                    sql += columns.getExpressions().get(map.get(fragmentAttrs[j])).toString() + ",";
                }
                sql += columns.getExpressions().get(map.get(fragmentAttrs[fragmentAttrs.length - 1])).toString() + ")";
                String site = etcdClient.get(fConstant.getSite());
                SqlClient sqlClient = new SqlClient(site);
                sqlClient.executeNonQuery(sql);
                sqlClient.close();
            }
        }
    }

    private void loadTSV(String str) throws Exception {
        String fileName = str.split(" INTO ")[0];
        String tableName = str.split(" INTO ")[1];
        File file = new File(fileName);
        InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader bf = new BufferedReader(inputReader);
        // 按行读取字符串
        String input;
        String[] sites = etcdClient.get("sites").split(",");
        String fragments = "";
        FragmentConstant Constant = new FragmentConstant("");
        fragments += etcdClient.get(Constant.getFragments());
        List<String> fragmentList = new ArrayList<>();
        String[] fragmentSplits = fragments.split(",");
        for(int i = 0; i < fragmentSplits.length; i++) {
            if(fragmentSplits[i].contains(tableName)) {
                fragmentList.add(fragmentSplits[i]);
            }
        }
        Map<String,StringBuffer> siteMap = new HashMap<>();
        FragmentConstant fragmentConstant = new FragmentConstant(fragmentList.get(0));
        RelationConstant relationConstant = new RelationConstant(tableName);
        String isHorizon = etcdClient.get(fragmentConstant.getIsHorizontal());
        String etcdAttribute = etcdClient.get(relationConstant.getAttributes());
        if(etcdAttribute.charAt(0) == ',') {
            etcdAttribute = etcdAttribute.substring(1);
        }
        String[] attributes = etcdAttribute.split(",");
        Map<String, Integer> map = new HashMap<>();
        String[] fragmentAttrs = {};
        if(isHorizon.contains("1")) {
            for(int i = 0; i < sites.length; i++) {
                siteMap.put(sites[i], new StringBuffer("insert into " + tableName + " values"));
            }
        } else {
            for(int i = 0; i < sites.length; i++) {
                siteMap.put(sites[i], new StringBuffer("insert into " + tableName));
            }
            //垂直分片
            for(int i = 0; i < attributes.length; i++) {
                map.put(attributes[i], i);
            }
            for(int i = 0; i < fragmentList.size(); i++) {
                FragmentConstant fConstant = new FragmentConstant(fragmentList.get(i));
                fragmentAttrs = etcdClient.get(fConstant.getAttributes()).split(",");
                String site = etcdClient.get(fConstant.getSite());
                String sql = " (";
                for(int j = 0; j < fragmentAttrs.length - 1; j++) {
                    sql += fragmentAttrs[j] + ",";
                }
                sql += fragmentAttrs[fragmentAttrs.length - 1] + ") values";
                siteMap.get(site).append(sql);
            }
        }
        while ((input = bf.readLine()) != null) {        
            Insert insertStatement = (Insert) CCJSqlParserUtil.parse(input);
            //水平分片
            if(isHorizon.contains("1")) {
                List<Condition> conditions = new ArrayList<>();
                ExpressionList columns = (ExpressionList)insertStatement.getItemsList();
                for(int i = 0; i < attributes.length; i++) {
                    String s = attributes[i] + "=" + columns.getExpressions().get(i).toString();
                    Condition condition = parseCondition(s, tableName, false);
                    conditions.add(condition);
                }
                for(int i = 0; i < fragmentList.size(); i++) {
                    FragmentConstant fConstant = new FragmentConstant(fragmentList.get(i));
                    String[] fragmentConditions = etcdClient.get(fConstant.getConditions()).split("%");
                    Boolean isConflict = false;
                    for(int j = 0; j < fragmentConditions.length; j++) {
                        for(int k = 0; k < conditions.size(); k++) {
                            Condition fragmentCondition = Condition.fromJson(fragmentConditions[j]);
                            if(conditions.get(k).leftValue.attrName.equals(fragmentCondition.leftValue.attrName)) {
                                if(conditions.get(k).isConflict(fragmentCondition)) {
                                    isConflict = true;
                                    break;
                                }
                            }
                        }
                        if(isConflict) {
                            break;
                        }
                    }
                    if(!isConflict) {
                        String value = input.substring(input.indexOf("("));
                        String site = etcdClient.get(fConstant.getSite());
                        siteMap.get(site).append(value + ",");
                        break;
                    }
                }
                
            } else {
                ExpressionList columns = (ExpressionList)insertStatement.getItemsList();
                for(int i = 0; i < fragmentList.size(); i++) {
                    FragmentConstant fConstant = new FragmentConstant(fragmentList.get(i));
                    String sql = "(";
                    for(int j = 0; j < fragmentAttrs.length - 1; j++) {
                        sql += columns.getExpressions().get(map.get(fragmentAttrs[j])).toString() + ",";
                    }
                    sql += columns.getExpressions().get(map.get(fragmentAttrs[fragmentAttrs.length - 1])).toString() + "),";
                    String site = etcdClient.get(fConstant.getSite());
                    siteMap.get(site).append(sql);
                    
                }
            }
        }
        for(Entry<String,StringBuffer> entry: siteMap.entrySet()) {
            String site = entry.getKey();
            SqlClient sqlClient = new SqlClient(site);
            String sql = entry.getValue().toString();
            sql = sql.substring(0,sql.length() - 1);
            if(sql.contains("(")) {
                sqlClient.executeNonQuery(sql);
            }
            sqlClient.close();
        }
        bf.close();
        inputReader.close();
    }
    //插入所有节点查询时优化
    private void executeSql(String sql) throws Exception {
        SiteConstant siteConstant = new SiteConstant("");
        String sites = etcdClient.get(siteConstant.getSites());
        String[] siteList = sites.split(",");
        Map<String, String> map = new HashMap<>();
        Set<String> hashSet = new HashSet<>(); 
        for(int i = 0; i < siteList.length; i++) {
            hashSet.add(siteList[i].split(":")[0]);
            map.put(siteList[i].split(":")[0], siteList[i].trim());
        }
        for (Entry<String,String> entry: map.entrySet()) {
            String site = entry.getValue();
            SqlClient sqlClient = new SqlClient(site);
            sqlClient.executeNonQuery(sql);
            sqlClient.close();
        }
    }

    private void allocateFragment(String input) throws Exception {
        String[] strs = input.split(" to ");
        String fragmentId = strs[0].split(" ")[1].trim();
        String siteName = strs[1].trim();
        FragmentConstant fragmentConstant = new FragmentConstant(fragmentId);
        SiteConstant siteConstant = new SiteConstant(siteName);
        etcdClient.put(fragmentConstant.getSite(), siteName);
        String fragments = etcdClient.get(siteConstant.getFragments());
        fragments += "," + siteName;
        etcdClient.put(siteConstant.getFragments(), fragments);
        //如果是水平分片
        // String isHorizon = etcdClient.get(fragmentConstant.getIsHorizontal());
        // if(isHorizon.charAt(0) == '1') {
        //     String[] conditions = etcdClient.get(fragmentConstant.getConditions()).split(",");
        //     for(int i = 0; i < conditions.length; i++) {
        //         String str = conditions[i];
        //         int j = 0;
        //         while(j < str.length() && str.charAt(j) != '<') {
        //             j++;
        //         }
        //     }
        // } else {
// 
        // }
    }

    private void parseHorizon(String[] strs) throws Exception {
        if (strs.length < 2) {
            throw new Exception();
        }
        String tableName = strs[0].split(" ")[1].trim();
        String[] horizons = strs[1].split(",");
        String fragments = "";
        FragmentConstant Constant = new FragmentConstant("");
        fragments += etcdClient.get(Constant.getFragments());
        for (int i = 0; i < horizons.length; i++) {
            String fragmentId = tableName + "-" + i;
            fragments += fragmentId + ",";
            String[] conditions = horizons[i].split("and");
            String fragmentCondition = "";
            for (int j = 0; j < conditions.length; j++) {
                conditions[j] = conditions[j].trim();
                Condition condition = parseCondition(conditions[j], tableName, false);
                fragmentCondition += condition.toJson() + "%";
            }
            FragmentConstant fragmentConstant = new FragmentConstant(fragmentId);
            etcdClient.put(fragmentConstant.getConditions(), fragmentCondition);
            etcdClient.put(fragmentConstant.getIsHorizontal(), "1");
        }
        etcdClient.put(Constant.getFragments(), fragments);
    }

    //将str解析成condition,haveTable表示str中存在表名了
    private Condition parseCondition(String str, String tableName, boolean haveTable) {
        String[] strs = str.split("<>|<=|>=|>|<|=");
        Pattern p = Pattern.compile("[A-Za-z_.]*");
        Matcher m = p.matcher(strs[0].trim());
        Condition condition = new Condition();
        String additionInfo = "";
        if (haveTable == false) {
            additionInfo = tableName + ".";
        }
        if (m.matches()) {
            condition.leftValue.attrName = additionInfo + strs[0].trim();
            condition.origin += condition.leftValue.attrName;
            condition.leftValue.isAttribute = true;
        } else {
            condition.leftValue.isAttribute = false;
            condition.leftValue.value = strs[0].trim();
            condition.origin += condition.leftValue.value;
        }

        p = Pattern.compile("<>|<=|>=|>|<|=");
        m = p.matcher(str);
        m.find();
        String op = m.group();
        condition.origin += op;
        if (op.equals("<>")) {
            if (strs[1].contains("'") || strs[1].contains("\"")) {
                condition.op = Condition.opType.NOT_EQUAL_TOSTRING;
            } else {
                condition.op = Condition.opType.NOT_EQUAL;
            }
        } else if (op.equals(">")) {
            condition.op = Condition.opType.GREATER_THAN;
        } else if (op.equals("=")) {
            if (strs[1].contains("'") || strs[1].contains("\"")) {
                condition.op = Condition.opType.EQUAL_TO_STRING;
            } else {
                condition.op = Condition.opType.EQUAL_TO;
            }
        } else if (op.equals("<")) {
            condition.op = Condition.opType.MIN_THAN;
        } else if (op.equals(">=")) {
            condition.op = Condition.opType.GREATER_EQUAL_TO;
        } else if (op.equals("<=")) {
            condition.op = Condition.opType.MIN_EQUAL_TO;
        }
        p = Pattern.compile("[A-Za-z_.]*");
        m = p.matcher(strs[1].trim());
        if (m.matches()) {
            condition.rightValue.attrName = additionInfo + strs[1].trim();
            condition.origin += condition.rightValue.attrName;
            condition.rightValue.isAttribute = true;
        } else {
            condition.rightValue.isAttribute = false;
            condition.rightValue.value = strs[1].trim();
            condition.origin += condition.rightValue.value;
        }
        //TO-DO 假设没有a.id>a.name的case
        if (condition.leftValue.isAttribute && condition.rightValue.isAttribute) {
            condition.isJoin = true;
        } else {
            condition.isJoin = false;
        }

        return condition;
    }

    private void parseVertical(String[] strs) throws Exception {
        if (strs.length < 2) {
            throw new Exception();
        }
        String tableName = strs[0].split(" ")[1].trim();
        int lb = 0;
        ArrayList<String> verticals = new ArrayList<String>();
        int count = 0;
        for (int i = 0; i < strs[1].length(); i++) {
            if (strs[1].charAt(i) == ')') {
                verticals.add(strs[1].substring(lb + 1, i));
            }
            if (strs[1].charAt(i) == '(') {
                lb = i;
            }
        }
        FragmentConstant Constant = new FragmentConstant("");
        String fragments = "";
        fragments += etcdClient.get(Constant.getFragments());
        for (int i = 0; i < verticals.size(); i++) {
            String fragmentId = tableName + "-" + i;
            fragments += fragmentId + ",";
            String[] attributes = verticals.get(i).split(",");
            String fragmentAttributes = "";
            for (int j = 0; j < attributes.length; j++) {
                attributes[j] = attributes[j].trim();
                fragmentAttributes += attributes[j] + ",";
            }
            FragmentConstant fragmentConstant = new FragmentConstant(fragmentId);
            etcdClient.put(fragmentConstant.getAttributes(), fragmentAttributes);
            etcdClient.put(fragmentConstant.getIsVertical(), "1");
        }
        FragmentConstant fragmentConstant = new FragmentConstant("");
        etcdClient.put(Constant.getFragments(), fragments);
    }

    private void createFragment(String input) throws Exception {
        //parse Horizon
        if (input.contains("horizontally into")) {
            String[] strs = input.split("horizontally into");
            parseHorizon(strs);
        }
        //parse Vertical
        if (input.contains("vertically into")) {
            String[] strs = input.split("vertically into");
            parseVertical(strs);
        }
    }

    private void createTable(String input) throws Exception {
        // 1.在etcd里存table
        CreateTable statement = (CreateTable) CCJSqlParserUtil.parse(input);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        String relationName = tablesNamesFinder.getTableList(statement).get(0);
        RelationConstant relation = new RelationConstant(relationName);
        String str = etcdClient.get(relation.getRelations());
        if(str == null) {
            str = "";
        }
        str += relationName;
        etcdClient.put(relation.getRelations(), str);

        ArrayList<String> attributeList = new ArrayList<String>();
        //etcd存attr
        String attributsName = "";
        for (ColumnDefinition columnDefinition : statement.getColumnDefinitions()) {
            SiteConstant siteConstant = new SiteConstant("");
            String sites = etcdClient.get(siteConstant.getSites());
            String[] siteList = sites.split(",");
            attributeList.add(columnDefinition.getColumnName());//attrname存到数据表中

            AttributeConstant attributeConstant = new AttributeConstant(statement.getTable().getName(), columnDefinition.getColumnName());
            if (columnDefinition.getColumnSpecs() != null) {
                etcdClient.put(attributeConstant.relationKey(), columnDefinition.getColumnName());
            }
            for (String site : siteList) {
                etcdClient.put(attributeConstant.getIsExitSite(site), "1");
            }
            etcdClient.put(attributeConstant.getIsHorizon(), "0");
            etcdClient.put(attributeConstant.getIsVertical(), "0");
            etcdClient.put(attributeConstant.getAttributeType(), columnDefinition.getColDataType().toString());
        }

        for (String attribute : attributeList) {
            attributsName += "," + attribute;
        }
        etcdClient.put(relation.getAttributes(), attributsName);
        //2.往数据库里存table
        executeSql(input);
    }

    private void parseDefineSite(String cmdString) throws Exception {
        //加入etcd中
        String[] siteInfo = cmdString.split(" ");
        String siteName = siteInfo[2].trim();
        String siteAddr = siteInfo[2].trim();
        SiteConstant site = new SiteConstant(siteName);
        String str = etcdClient.get("sites");
        if (str == null) {
            str = "";
        }
        str += "," + siteName;
        etcdClient.put("sites", str);
        etcdClient.put(site.getSiteIp(), siteAddr);//加入etcd中
    }

    //现在做的都是多表查询而且表名.属性，TO-DO单表的查询
    private List<String> parseSelectSql(String sql) throws Exception {
        Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        //单表情况下
        String tableName = tablesNamesFinder.getTableList(selectStatement).get(0);
        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
        List<AttributeConstant> selectAttributes = new ArrayList<>();
        List<AttributeConstant> whereAttributes = new ArrayList<>();
        List<Condition> conditions = new ArrayList<>();
        if (plainSelect.getSelectItems().get(0) instanceof AllColumns) {
            List<String> tables = tablesNamesFinder.getTableList(selectStatement);
            for (int i = 0; i < tables.size(); i++) {
                RelationConstant relation = new RelationConstant(tables.get(i));
                String[] attrs = etcdClient.get(relation.getAttributes()).split(",");
                for (int j = 0; j < attrs.length; j++) {
                    AttributeConstant constant = new AttributeConstant(tables.get(i), attrs[j]);
                    selectAttributes.add(constant);
                }
            }
        } else {
            for (int i = 0; i < plainSelect.getSelectItems().size(); i++) {
                //选中的属性
                SelectExpressionItem item = (SelectExpressionItem) plainSelect.getSelectItems().get(i);
                Column column = (Column) item.getExpression();
                String attributeName = column.getColumnName();
                String tmpTableName = tableName;
                if (column.getTable() != null) {
                    tmpTableName = column.getTable().getName();
                }
                AttributeConstant attributeConstant = new AttributeConstant(tmpTableName, attributeName);
                selectAttributes.add(attributeConstant);
            }
        }
        String where = "";
        String[] conditionStrings = new String[0];
        if(plainSelect.getWhere() == null) {
            
        } else {
            where = plainSelect.getWhere().toString();
            conditionStrings = where.split("AND");
        }
        for (int i = 0; i < conditionStrings.length; i++) {
            boolean haveTable = false;
            if (conditionStrings[0].contains(".")) {
                haveTable = true;
            }
            Condition condition = parseCondition(conditionStrings[i], tableName, haveTable);
            conditions.add(condition);
            if (condition.leftValue.isAttribute == true) {
                String[] attrs = condition.leftValue.attrName.split("[.]");
                AttributeConstant constant = new AttributeConstant(attrs[0], attrs[1]);
                whereAttributes.add(constant);
            }
            if (condition.rightValue.isAttribute == true) {
                String[] attrs = condition.rightValue.attrName.split("[.]");
                AttributeConstant constant = new AttributeConstant(attrs[0], attrs[1]);
                whereAttributes.add(constant);
            }
        }
        List<Condition> notJoinConditions = new ArrayList<>();
        for(int i = 0; i < conditions.size(); i++) {
            if(!conditions.get(i).isJoin) {
                notJoinConditions.add(conditions.get(i));
            }
        }
        for(Condition condition : conditions) {
            if(condition.isJoin) {
                List<Condition> tmpConditions = new ArrayList<>();
                tmpConditions.addAll(notJoinConditions);
                for(Condition notJoinCondition : tmpConditions) {
                    if(!notJoinCondition.isJoin && notJoinCondition.leftValue.attrName.equals(condition.leftValue.attrName)) {
                        Condition tmpCondition = new Condition(notJoinCondition);
                        tmpCondition.leftValue = condition.rightValue;
                        //TO-DO 修改orign信息
                        // tmpCondition.origin = 
                        notJoinConditions.add(tmpCondition);
                    }
                    if(!notJoinCondition.isJoin && notJoinCondition.leftValue.attrName.equals(condition.rightValue.attrName)) {
                        Condition tmpCondition = new Condition(notJoinCondition);
                        tmpCondition.leftValue = condition.leftValue;
                        //TO-DO 修改orign信息
                        // tmpCondition.origin = 
                        notJoinConditions.add(tmpCondition);
                    }
                }
                
            }
        }
        List<Condition> tmpConditions = new ArrayList<>();
        tmpConditions.addAll(conditions);
        conditions.clear();
        for(int i = 0; i < tmpConditions.size(); i++) {
            if(tmpConditions.get(i).isJoin){
                conditions.add(tmpConditions.get(i));
            }
        }
        conditions.addAll(notJoinConditions);
        Set<AttributeConstant> finalSet = new HashSet<>();
        finalSet.addAll(whereAttributes);
        finalSet.addAll(selectAttributes);
        Map<String, List<TreeNode>> horizonMaps = new HashMap<>();//水平分片
        Map<String, List<TreeNode>> verticalMaps = new HashMap<>();//垂直分片
        //对于所有的表名和属性在etcd里查询
        String[] fragmentsId = etcdClient.get("fragments").split(",");
        for (String fragmentId : fragmentsId) {
            FragmentConstant fragmentConstant = new FragmentConstant(fragmentId);
            TreeNode tempTable = new TreeNode();
            tempTable.fragmentId = fragmentId;
            tempTable.content.isLeaf = true;
            tempTable.content.tableName = fragmentId.split("-")[0];
            tempTable.content.project = "";
            tempTable.content.condition = "";
            tempTable.site = etcdClient.get(fragmentConstant.getSite());
            //TO-DO改ip
            if(tempTable.site.equals("")) {
                tempTable.site = "192.168.31.101" +":31100"; 
            }
            tableName = fragmentId.split("-")[0];
            //水平分片,默认拥有所有的属性,TO-DO 混合分片
            if (etcdClient.get(fragmentConstant.getIsHorizontal()).equals("1")) {
                for (AttributeConstant constant : finalSet) {
                    //看下fragment中是否含有该表，如果有的话就把这个属性加进去
                    if (fragmentId.contains(constant.getRelationName())) {
                        tempTable.content.project += "," + constant.getRelationName() + "." + constant.getAttributeName();
                    }
                }
                if (tempTable.content.project.isEmpty() || tempTable.content.project.equals("")) {
                    continue;
                }
                tempTable.content.condition = etcdClient.get(fragmentConstant.getConditions());
                //遍历所有的condition,如果有冲突，过滤掉，否则把相关的过滤条件都存进去TO-DO默认左边都是attribute，右边都是值
                String[] strs = tempTable.content.condition.split("%");//分片中所有的condition
                List<Condition> fragmentConditions = new ArrayList<>();
                for (int i = 0; i < strs.length; i++) {
                    Condition conditions1 = Condition.fromJson(strs[i]);
                    fragmentConditions.add(conditions1);
                }
                for (Condition condition : conditions) {
                    boolean isUsed = false;
                    if(condition.leftValue.attrName.contains(tableName + ".") && !condition.isJoin) {
                        isUsed = true;
                    } else {
                        continue;
                    }
                    for (Condition fragmentCondition : fragmentConditions) {
                        if (!condition.isJoin && !fragmentCondition.isJoin) {
                            if (condition.leftValue.attrName.equals(fragmentCondition.leftValue.attrName)) {
                                if (condition.isConflict(fragmentCondition)) {
                                    tempTable.isUsed = false;
                                } else {
                                    isUsed = true;
                                }
                            }
                        }
                    }
                    if (tempTable.isUsed) {
                        if (isUsed) {
                            tempTable.content.condition += condition.toJson() + "%";
                        }
                    } else {
                        break;
                    }
                }
                if (tempTable.isUsed) {
                    if (tempTable.content.project.charAt(0) == ',') {
                        tempTable.content.project = tempTable.content.project.substring(1);
                    }
                    if (tempTable.content.condition.charAt(0) == ',') {
                        tempTable.content.condition = tempTable.content.condition.substring(1);
                    }
                    // tempTable.content.condition = tempTable.content.condition.replaceAll(",", " and ");
                    tableName = fragmentId.split("-")[0].trim();
                    if (horizonMaps.get(tableName) == null || horizonMaps.get(tableName).isEmpty()) {
                        List<TreeNode> nodes = new ArrayList<>();
                        nodes.add(tempTable);
                        horizonMaps.put(tableName, nodes);
                    } else {
                        horizonMaps.get(tableName).add(tempTable);
                    }
                }
            } else {
                //找到涉及的所有属性
                String[] attributes = etcdClient.get(fragmentConstant.getAttributes()).split(",");
                List<AttributeConstant> attributeConstants = getConstantsByTabble(finalSet, tableName);
                String key = etcdClient.get("relation." + tableName + ".key");
                //只有主属性
                if (attributeConstants.size() == 1 && attributeConstants.get(0).getAttributeName().equals(key)) {
                    if (verticalMaps.get(tableName) == null || verticalMaps.get(tableName).isEmpty()) {
                        tempTable.content.project += "," + tableName + "." + key;
                        List<TreeNode> treeNodes = new ArrayList<>();
                        treeNodes.add(tempTable);
                        verticalMaps.put(tableName, treeNodes);
                    }
                    continue;
                }

                boolean isKeyUsed = false;
                for (AttributeConstant constant : attributeConstants) {
                    if (constant.getAttributeName().equals(key)) {
                        isKeyUsed = true;
                    }
                }
                //有非主属性
                for (AttributeConstant constant : attributeConstants) {
                    //看下fragment中是否含有该表
                    for (int i = 0; i < attributes.length; i++) {
                        //出现过但不是主属性
                        if (constant.getAttributeName().equals(attributes[i]) && !constant.getAttributeName().equals(key)) {
                            //如果属性是key属性，只需要一个垂直分片即可，如果是非key属性，保留该分片
                            tempTable.content.project += "," + constant.getRelationName() + "." + constant.getAttributeName();
                        }
                    }
                }

                if (tempTable.content.project.equals("") || tempTable.content.project.isEmpty()) {
                    continue;
                }
                if (isKeyUsed) {
                    tempTable.needKey = true;
                    //最后把key值放进去
                    tempTable.content.project += "," + tableName + "." + key;
                } else {
                    tempTable.needKey = false;
                    tempTable.content.project += "," + tableName + "." + key;
                }
                tableName = fragmentId.split("-")[0].trim();

                //如果垂直分片中有condition的列就把condition加进去
                for(Condition condition: conditions) {
                    if(!condition.isJoin && condition.leftValue.attrName.split("[.]")[0].equals(tableName)) {
                        String attrName =  condition.leftValue.attrName.split("[.]")[1];
                        for(int i = 0; i < attributes.length; i++) {
                            if(attributes[i].equals(attrName)) {
                                tempTable.content.condition += condition.toJson() + "%";
                                break;
                            }
                        }
                    }
                }
                if(tempTable.content.condition.length() > 0) {
                    tempTable.content.condition = tempTable.content.condition.substring(0, tempTable.content.condition.length() - 1);
                }
                 if (verticalMaps.get(tableName) == null || verticalMaps.get(tableName).size() == 0) {
                    List<TreeNode> nodes = new ArrayList<>();
                    nodes.add(tempTable);
                    verticalMaps.put(tableName, nodes);
                } else {
                    //如果只有主属性，只添加一次
                    if (attributeConstants.size() == 1 && attributeConstants.get(0).getAttributeName().equals(key)) {

                    } else {
                        verticalMaps.get(tableName).add(tempTable);
                    }
                }
            }
        }
        Map<String, TreeNode> tableNode = new HashMap<>();
        //把水平分片节点拼接在一起
        for (Map.Entry<String, List<TreeNode>> entry : horizonMaps.entrySet()) {
            tableNode.put(entry.getKey(), Union(entry.getValue()));
        }
        //以下是test


        // for (Map.Entry<String, List<TreeNode>> entry : horizonMaps.entrySet()) {
        //     TreeNode node = Union(entry.getValue());
        //     etcdClient.put(node.fragmentId, regularTreeNode(node).content.toJson());

        //     for(int i = 0; i < node.childNum; i++) {
        //         etcdClient.put(node.children.get(i).fragmentId, regularTreeNode(node.children.get(i)).content.toJson());
        //     }
        //     SqlClient client2 = new SqlClient("192.168.31.101:31100");
        //     // System.out.println(client2.requestTable(node.fragmentId));
        //     List<String> l = client2.requestTable(node.fragmentId);
        //     for (String s : l) {
        //         System.out.println(s);
        //     }
        //     client2.close();
        // }
        

        //以上是test

        //根据join条件级联剪枝

       //把垂直分片节点join在一起
       for (Map.Entry<String, List<TreeNode>> entry : verticalMaps.entrySet()) {
             tableNode.put(entry.getKey(), JoinAsUnion(entry.getValue()));
        //    tableName = entry.getKey();
        //    List<TreeNode> treeNodes = entry.getValue();
        //    if (treeNodes.size() == 1) {
        //        if (!treeNodes.get(0).needKey) {
        //            treeNodes.get(0).content.project = "";
        //            String[] strs = treeNodes.get(0).content.project.split(",");
        //            for (int i = 0; i < strs.length; i++) {
        //                treeNodes.get(0).content.project += "," + strs[i];
        //            }
        //            tableNode.put(entry.getKey(), treeNodes.get(0));
        //        } else {
        //            tableNode.put(entry.getKey(), treeNodes.get(0));
        //        }
        //    } else {
        //        tableNode.put(entry.getKey(), JoinLeaf(treeNodes));
        //    }
       }

       List<Pair<TreeNode, TreeNode>> conflictPairs = new ArrayList<>();
       List<Pair<TreeNode, TreeNode>> joinPairs = new ArrayList<>();
       for (Condition condition : conditions) {
            if (condition.isJoin) {
                String leftTable = condition.leftValue.attrName.split("[.]")[0];
                String leftAttr = condition.leftValue.attrName.split("[.]")[1];
                String rightTable = condition.rightValue.attrName.split("[.]")[0];
                String rightAttr = condition.rightValue.attrName.split("[.]")[1];
                TreeNode leftRoot = tableNode.get(leftTable);
                TreeNode rightRoot = tableNode.get(rightTable);
                for(int i = 0; i < leftRoot.childNum; i++) {
                    for(int j = 0; j < rightRoot.childNum; j++) {
                        String[] leftConditiosString = leftRoot.children.get(i).content.condition.split("%");
                        String[] rightConditionsString = rightRoot.children.get(j).content.condition.split("%");
                        boolean isConflict = false;
                        if(leftConditiosString.length == 1 && leftConditiosString[0].length() <3) {}
                        for(int ii = 0; ii < leftConditiosString.length; ii++) {
                            if(isConflict) {
                                break;
                            }
                            Condition leftCondition = Condition.fromJson(leftConditiosString[ii]);
                            if(leftCondition!= null && leftCondition.isJoin) {
                                continue;
                            }
                            if(leftCondition != null && leftCondition.leftValue.attrName.equals(condition.leftValue.attrName)) {
                                leftCondition.leftValue.attrName = condition.rightValue.attrName;
                                for(int jj = 0; jj < rightConditionsString.length; jj++) {
                                    Condition rightCondition = Condition.fromJson(rightConditionsString[jj]);
                                    if(rightCondition != null && rightCondition.isJoin) {
                                        continue;
                                    }
                                    if(rightCondition != null && leftCondition.leftValue.attrName.equals(rightCondition.leftValue.attrName) && leftCondition.isConflict(rightCondition)) {
                                        conflictPairs.add(new Pair<TreeNode,TreeNode>(leftRoot.children.get(i), rightRoot.children.get(j)));
                                        isConflict = true;
                                    }
                                    if(isConflict) {
                                        break;
                                    }
                                }
                            }
                        }                                    
                        if(isConflict) {
                            continue;
                        }
                        for(int ii = 0; ii < rightConditionsString.length; ii++) {
                            if(isConflict) {
                                break;
                            }
                            Condition rightCondition = Condition.fromJson(rightConditionsString[ii]);
                            if(rightCondition != null && rightCondition.isJoin) {
                                continue;
                            }
                            if(rightCondition != null && rightCondition.leftValue.attrName.equals(condition.rightValue.attrName)) {
                                rightCondition.leftValue.attrName = condition.leftValue.attrName;
                                for(int jj = 0; jj < leftConditiosString.length; jj++) {
                                    Condition leftCondition = Condition.fromJson(leftConditiosString[jj]);
                                    if(leftCondition != null && leftCondition.isJoin) {
                                        continue;
                                    }
                                    if(leftCondition != null && leftCondition.leftValue.attrName.equals(rightCondition.leftValue.attrName) && rightCondition.isConflict(leftCondition)) {
                                        conflictPairs.add(new Pair<TreeNode,TreeNode>(leftRoot.children.get(i), rightRoot.children.get(j)));
                                        isConflict = true;
                                    }
                                    if(isConflict) {
                                        break;
                                    }
                                }
                            }
                        }  
                        if(isConflict) {
                            continue;
                        }
                        joinPairs.add(new Pair<TreeNode,TreeNode>(leftRoot.children.get(i), rightRoot.children.get(j)));
                    }
                }
            }
        }
        //选出size和的最大关系
        String maxPos = "";
        for(Map.Entry<String, TreeNode> entry: tableNode.entrySet()) {
            int maxRes = 0;
            int sum = 0;
            for(int i = 0;i < entry.getValue().childNum; i++) {
                sum += getTreeNodeSize(entry.getValue().children.get(i));
            }
            if(sum > maxRes) {
                maxRes = sum;
                maxPos = entry.getKey();
            }
        }

        Graph myGraph = new Graph();
        for(int i = 0; i < joinPairs.size(); i++) {
            myGraph.addPair(joinPairs.get(i));
        }

        //选出执行站点
        TreeNode maxRoot = tableNode.get(maxPos);
        List<String> notSiteList = new ArrayList<>();
        List<String> siteList = new ArrayList<>();
        Map<String,Map<String, Set<TreeNode>>> siteMap = new HashMap<>();
        Set<TreeNode> unUsedNodes = new HashSet<>();
        for(int i = 0; i < maxRoot.childNum; i++) {
            TreeNode executeSite = new TreeNode();
            String mainSite = maxRoot.children.get(i).site;
            int sum = 0;
            List<TreeNode> siteNodes = myGraph.visit(maxRoot.children.get(i));
            Map<String, Set<TreeNode>> curMap = new HashMap<>();
            for(TreeNode node : siteNodes) {
                tableName = node.fragmentId.split("-")[0];
                if(curMap.containsKey(tableName)) {
                    curMap.get(tableName).add(node);
                } else {
                    Set<TreeNode> nodeSet = new HashSet<>();
                    nodeSet.add(node);
                    curMap.put(tableName, nodeSet);
                }
            }
            siteMap.put(mainSite, curMap);
            List<TreeNode> sameSiteNodes = getSameSiteNodes(maxRoot.children.get(i), siteNodes);
            siteList.removeAll(sameSiteNodes);
            for(TreeNode node : siteNodes) {
                sum += getTreeNodeSize(node);
            }
            // for(int j = 0; j < joinPairs.size(); j++) {
            //     if(joinPairs.get(j).getKey().fragmentId.equals(maxRoot.children.get(i).fragmentId)) {
            //         if(!mainSite.equals(joinPairs.get(j).getValue().site)) {
            //             sum += getTreeNodeSize(joinPairs.get(j).getValue());
            //             tableName = joinPairs.get(j).getValue().fragmentId.split("-")[0];
            //             if(siteMap.get(mainSite) == null) {
            //                 Map<String, Set<TreeNode>> map = new HashMap<>();
            //                 Set<TreeNode> nodes = new HashSet<>();
            //                 nodes.add(joinPairs.get(j).getValue());
            //                 map.put(tableName, nodes);
            //                 siteMap.put(mainSite, map);
            //             } else {
            //                 Map<String, Set<TreeNode>> map = siteMap.get(mainSite);
            //                 if(map.get(tableName) == null) {
            //                     Set<TreeNode> nodes = new HashSet<>();
            //                     nodes.add(joinPairs.get(j).getValue());
            //                     map.put(tableName, nodes);
            //                 } else {
            //                     Set<TreeNode> nodes = map.get(tableName);
            //                     nodes.add(joinPairs.get(j).getValue());
            //                     map.put(tableName, nodes);
            //                 }
            //                 siteMap.put(mainSite, map);
            //             }
            //         }
            //         continue;
            //     }
            //     if(joinPairs.get(j).getValue().fragmentId.equals(maxRoot.children.get(i).fragmentId)) {
            //         if(!mainSite.equals(joinPairs.get(j).getKey().site)) {
            //             sum += getTreeNodeSize(joinPairs.get(j).getKey());
            //             tableName = joinPairs.get(j).getKey().fragmentId.split("-")[0];
            //             if(siteMap.get(mainSite) == null) {
            //                 Map<String, Set<TreeNode>> map = new HashMap<>();
            //                 Set<TreeNode> nodes = new HashSet<>();
            //                 nodes.add(joinPairs.get(j).getKey());
            //                 map.put(tableName, nodes);
            //                 siteMap.put(mainSite, map);
            //             } else {
            //                 Map<String, Set<TreeNode>> map = siteMap.get(mainSite);
            //                 if(map.get(tableName) == null) {
            //                     Set<TreeNode> nodes = new HashSet<>();
            //                     nodes.add(joinPairs.get(j).getKey());
            //                     map.put(tableName, nodes);
            //                 } else {
            //                     Set<TreeNode> nodes = map.get(tableName);
            //                     nodes.add(joinPairs.get(j).getKey());
            //                     map.put(tableName, nodes);
            //                 }
            //                 siteMap.put(mainSite, map);
            //             }
            //         }
            //         continue;
            //     }
            //     //没有出现在pair中的节点
            //     unUsedNodes.add(joinPairs.get(j).getKey());
            //     unUsedNodes.add(joinPairs.get(j).getValue());
            // }
            if(sum < getTreeNodeSize(maxRoot.children.get(i))) {
                siteList.add(mainSite);
            } else {
                notSiteList.add(mainSite);
            }
        }
        if(siteList.isEmpty()) {
            siteList.add(maxRoot.children.get(0).site);
        }
        //开始合并site到主节点
        Map<String,Set<TreeNode>> nmap = siteMap.get(siteList.get(0));
        for(int i = 0; i < notSiteList.size(); i++) {
            Map<String,Set<TreeNode>> map = siteMap.get(notSiteList.get(i));
            for(Map.Entry<String,Set<TreeNode>> entry: map.entrySet()) {
                if(nmap.containsKey(entry.getKey())) {
                    Set<TreeNode> nodes = nmap.get(entry.getKey());
                    nodes.addAll(entry.getValue());
                    nmap.put(entry.getKey(), nodes);
                } else {
                    nmap.put(entry.getKey(), entry.getValue());
                }
            }
        }   
        //把垂直分片的叶子节点放到同一个节点上，，先join成一张表

        siteMap.put(siteList.get(0), nmap);
        Map<String,Map<String,TreeNode>> secondMap = new HashMap<String,Map<String,TreeNode>>();
        //开始合并子节点
        for(int i = 0; i < siteList.size(); i++) {
            Map<String, TreeNode> tmpMap = new HashMap<>();
            for(Map.Entry<String,Set<TreeNode>> entry: siteMap.get(siteList.get(i)).entrySet()) {
                List<TreeNode> lNodes = new ArrayList<>();
                lNodes.addAll(entry.getValue());
                if(tableNode.get(entry.getKey()).content.isUnion) {
                    if(lNodes.size() > 1) {
                        TreeNode node = Union(lNodes);
                        node.site = siteList.get(i);
                        tmpMap.put(entry.getKey(), node);
                    } else {
                        tmpMap.put(entry.getKey(), lNodes.get(0));
                    }
                } else {
                    if(lNodes.size() > 1) {
                        TreeNode node = JoinLeaf(lNodes);
                        node.site = siteList.get(i);
                        tmpMap.put(entry.getKey(), node);
                    } else {
                        tmpMap.put(entry.getKey(), lNodes.get(0));
                    }
                }
            }
            secondMap.put(siteList.get(i), tmpMap);
        }

        List<TreeNode> siteFinalList = new ArrayList<>();
        for(int i = 0; i < siteList.size(); i++) {
            TreeNode finalNode = new TreeNode();
            for(Entry<String,Map<String,TreeNode>> entry : secondMap.entrySet()) {
                for(Entry<String, TreeNode> entry2 : entry.getValue().entrySet()) {
                    finalNode = entry2.getValue();
                    break;
                }
                break;
            }
            for (Condition condition : conditions) {
                if (condition.isJoin) {
                    TreeNode leftNode = secondMap.get(siteList.get(i)).get(condition.leftValue.attrName.split("[.]")[0]);
                    TreeNode rightNode =  secondMap.get(siteList.get(i)).get(condition.rightValue.attrName.split("[.]")[0]);
                    TreeNode root = Join(leftNode, rightNode, condition.leftValue.attrName, condition.rightValue.attrName, siteList.get(i));
                    secondMap.get(siteList.get(i)).put(condition.leftValue.attrName.split("[.]")[0], root);
                    secondMap.get(siteList.get(i)).put(condition.rightValue.attrName.split("[.]")[0], root);
                    finalNode = root;
                }
            }
            siteFinalList.add(finalNode);
            // List<TreeNode> tmpList = new ArrayList<>();
            // for(Map.Entry<String, TreeNode> entry : secondMap.get(siteList.get(i)).entrySet()) {
            //     boolean allConflict = true;
            //     for(int j = 0; j < tmpList.size(); j++) {
            //         if(entry.getValue().fragmentId.equals(tmpList.get(j).fragmentId)) {
            //             allConflict = false;
            //         }
            //     }
            //     if(allConflict) {
            //         tmpList.add(entry.getValue());
            //     }
            // }
            // tmpList.
            // secondMap.get(i)
        }
        TreeNode finalRoot = siteFinalList.get(0);
        if(siteFinalList.size() > 1) {
            finalRoot = Union(siteFinalList);
        }
        reviseLeafNode(finalRoot);
        String[] selectAttrs = finalRoot.content.project.split(",");
        List<String> selectFinal = new ArrayList<>();
        for(int i = 0; i < selectAttributes.size(); i++) {
            selectFinal.add(selectAttributes.get(i).getRelationName() + "." + selectAttributes.get(i).getAttributeName());
        }
        finalRoot.content.project = "";
        for(int i = 0; i < selectAttrs.length; i++) {
            for(int j = 0; j < selectFinal.size(); j++) {
                if(selectAttrs[i].contains(selectFinal.get(j))) {
                    finalRoot.content.project += selectAttrs[i] + ",";
                }
            }
        }
        finalRoot.content.project = finalRoot.content.project.substring(0, finalRoot.content.project.length() - 1);
        // TreeNode.calculateNode(finalRoot);
        // TreeNode.Print(finalRoot);
        mappingtool.layer m = TreeNode.dfs(finalRoot);
        System.out.println("" + m.toString());
        storeInEtcd(finalRoot);
        List<String> l = null;
        if(finalRoot.children.size() == 0) {
            SqlClient client2 = new SqlClient(finalRoot.site);
            l = client2.executeQuery(sql);
            client2.close();
        }else {
            SqlClient client2 = new SqlClient(SqlServer.site);
            // System.out.println(client2.requestTable(node.fragmentId));
            l = client2.requestTable(finalRoot.fragmentId);
            client2.close();
        }
            System.out.println("=================");
            System.out.println("length = " + l.size());
            for (int i = 0; i < l.size() && i< 5; i++) {
                System.out.println(l.get(i));
            }
            if (l.size() == 0) {
                System.out.println("empty");
            }
            
        return l;

       //把所有的表按照join条件组合在一起
    //    TreeNode realNode = new TreeNode();
    //    for (Condition condition : conditions) {
    //        if (condition.isJoin) {
    //            TreeNode leftNode = tableNode.get(condition.leftValue.attrName.split("[.]")[0]);
    //            TreeNode rightNode = tableNode.get(condition.rightValue.attrName.split("[.]")[0]);
    //            TreeNode root = Join(leftNode, rightNode, condition.leftValue.attrName.split("[.]")[1], condition.rightValue.attrName.split("[.]")[1]);
    //            tableNode.put(condition.leftValue.attrName.split("[.]")[0], root);
    //            tableNode.put(condition.rightValue.attrName.split("[.]")[0], root);
    //            realNode = root;
    //        }
    //    }
    //    Map<String, Boolean> boolMap = new HashMap<>();
    //    List<TreeNode> nodes = new ArrayList<>();
    //    for(Map.Entry<String, TreeNode> entry: tableNode.entrySet()) {
    //        if(boolMap.containsKey(entry.getValue().fragmentId)){
    //            continue;
    //        }
    //        boolMap.put(entry.getValue().fragmentId, true);
    //        nodes.add(entry.getValue());
    //    }
    //    realNode = Union(nodes);
    //    realNode.fragmentId  = "1";
    }

    //找到所有含有table的属性
    private List<AttributeConstant> getConstantsByTabble(Set<AttributeConstant> attributeConstants, String tableName) {
        List<AttributeConstant> constants = new ArrayList<>();
        for (AttributeConstant constant : attributeConstants) {
            if (constant.getRelationName().equals(tableName)) {
                constants.add(constant);
            }
        }
        return constants;
    }

    private void reviseLeaf(TreeNode root) {
        
    }
    private TreeNode regularTreeNode(TreeNode node) {
        if(node.content.project.charAt(0) == ',') {
            node.content.project = node.content.project.substring(1);
        }
        String[] conditions = new String[0];
        if(node.content.condition.length() > 0) {
            conditions = node.content.condition.split("%");
            node.content.condition = "";
            for(int i = 0; i < conditions.length; i++) {
                Condition condition = Condition.fromJson(conditions[i]);
                node.content.condition += " and " + condition.origin;
            }
            node.content.condition = node.content.condition.substring(5);
        } else {
            node.content.condition = "";
        }
        return node;
    }
    private int getTreeNodeSize(TreeNode node) {
        if(node.content.isLeaf) {
            String sql = "select count(*) from " + node.content.tableName;
            String[] conditionStrings = node.content.condition.split("%");
            if(conditionStrings.length == 1 && conditionStrings[0].length() <3) {

            } else {
                sql += " where ";
                for(int i = 0; i < conditionStrings.length - 1; i++) {
                    Condition condition = Condition.fromJson(conditionStrings[i]);
                    sql += condition.leftValue.attrName + condition.Op() + condition.rightValue.value;
                    sql += " and ";
                }
                Condition condition = Condition.fromJson(conditionStrings[conditionStrings.length - 1]);
                sql += condition.leftValue.attrName + condition.Op() + condition.rightValue.value;
            }
            SqlClient client = new SqlClient(node.site);
            List<String> strList = client.executeQuery(sql);
            node.predictNum = Integer.parseInt(strList.get(1).substring(1, strList.get(1).length() - 1));
            client.close();
            return node.predictNum; 
        } else if(node.content.isUnion) {
            if(node.predictNum == 0) {
                for(int i = 0; i < node.children.size(); i++) {
                    node.predictNum += node.children.get(i).childNum;
                }
            }
            return node.predictNum;
        } else {
            if(node.predictNum == 0) {
                node.predictNum = 1;
                for(int i = 0; i < node.children.size(); i++) {
                    node.predictNum *= node.children.get(i).childNum;
                }
            }
            return node.predictNum; 
        }
    }
    
    private void parseTreeToEtcd(TreeNode root) {
        if(root.content.isLeaf) {
            return;
        }
        if(!root.content.isLeaf) {
            for(int i = 0; i < root.childNum; i++) {
                parseTreeToEtcd(root.children.get(i));
            }
            if(root.content.isUnion) {
                root.content.project = root.children.get(0).content.project;
            }
        }
    }

    private TreeNode Union(List<TreeNode> treeNodes) {
        TreeNode parent = new TreeNode();
        parent.content.isUnion = true;
        parent.content.isLeaf = false;
        parent.content.project += getUnionProjects(treeNodes.get(0));
        parent.content.condition = "";
        parent.content.children = new ArrayList<>();
        parent.content.addresses = new ArrayList<>();
        for (TreeNode node : treeNodes) {
            parent.content.children.add(node.fragmentId);
            parent.content.addresses.add(node.site);
            parent.children.add(node);
            parent.childNum++;
            parent.site = node.site;
            parent.fragmentId += node.fragmentId;
            parent.content.tableName = parent.fragmentId;
        }
        return parent;
    }

    private TreeNode JoinAsUnion(List<TreeNode> treeNodes) {
        TreeNode parent = new TreeNode();
        parent.content.isUnion = false;
        parent.content.isLeaf = false;
        parent.content.project = treeNodes.get(0).content.project;
        parent.content.condition = "";
        parent.content.children = new ArrayList<>();
        parent.content.addresses = new ArrayList<>();
        for (TreeNode node : treeNodes) {
            // node.content.project = getProjectsByTreeNode(node);
            parent.content.children.add(node.fragmentId);
            parent.content.addresses.add(node.site);
            parent.children.add(node);
            parent.childNum++;
            parent.site = node.site;
            parent.fragmentId += node.fragmentId;
        }
        parent.content.project = getProjectsByTreeNode(parent);
        return parent;
    }

    private TreeNode JoinLeaf(List<TreeNode> treeNodes) throws Exception {
        TreeNode root = new TreeNode();
        root.site = treeNodes.get(0).site;
        root.children.add(treeNodes.get(0));
        root.children.add(treeNodes.get(1));
        root.content.project = "";
        root.fragmentId = treeNodes.get(0).fragmentId + treeNodes.get(1).fragmentId;
        root.content.tableName = root.fragmentId;
        String relationName = treeNodes.get(0).fragmentId.split("-")[0];
        AttributeConstant attributeConstant = new AttributeConstant(relationName, "");
        String key = etcdClient.get(attributeConstant.relationKey());
        root.content.joinAttribute = "`" + treeNodes.get(0).fragmentId + "`"  + "." + "`" + relationName  + "." + key + "`" +  "=" + "`" + treeNodes.get(1).fragmentId + "`" + "."  + "`" + relationName  + "." + key + "`";
        //只留一个key
        root.content.project += getProjectsByLeaf(treeNodes.get(0)) + "," + getProjectsByTreeNodeExcludeKey(treeNodes.get(1), key);
        root.content.isLeaf = false;
        root.content.isUnion = false;
        root.content.children.add(treeNodes.get(0).fragmentId);
        root.content.children.add(treeNodes.get(1).fragmentId);
        root.content.addresses.add(treeNodes.get(0).site);
        root.content.addresses.add(treeNodes.get(1).site);
        root.childNum += 2;
        for (int i = 2; i < treeNodes.size(); i++) {
            TreeNode node = new TreeNode();
            node.children.add(root);
            node.children.add(treeNodes.get(i));
            node.childNum += 2;
            node.content.project = "";
            root.content.joinAttribute = "`" + root.fragmentId + "`"  + "." + "`" + relationName  + "." + key + "`" +  "=" + "`" + treeNodes.get(i).fragmentId + "`" + "."  + "`" + relationName  + "." + key + "`";
            node.content.project += getProjectsByTreeNode(root) + "," + getProjectsByTreeNodeExcludeKey(treeNodes.get(i), key);
            node.content.isLeaf = false;
            node.content.isUnion = false;
            node.content.children.add(root.fragmentId);
            node.content.children.add(treeNodes.get(i).fragmentId);
            node.content.addresses.add(root.site);
            node.content.addresses.add(treeNodes.get(i).site);
            node.site = root.site;
            root = node;
        }
        return root;
    }

    private String getProjectsByTreeNode(TreeNode node) {
        String str = "";
        if(node.content.project.charAt(0) == ',') {
            node.content.project = node.content.project.substring(1);
        }
        String[] strs = node.content.project.split(",");
        if(node.content.isLeaf || node.content.isUnion) {
            for(int i = 0; i < strs.length - 1; i++) {
                str += "`" + node.fragmentId + "`" + ".";
                if(node.content.isLeaf) {
                    str += "`" + strs[i] + "`";
                } else {
                    str += strs[i]; 
                }
                str += ",";
            }
            str += "`" + node.fragmentId + "`" + ".";
            if(node.content.isLeaf) {
                str += "`" + strs[strs.length - 1] + "`";
            } else {
                str += strs[strs.length - 1]; 
            }
            return str;
        }
        //不是叶子节点或union节点
        for (int i = 0; i < strs.length; i++) {
            String[] attrs = strs[i].split("[.]");
            if(attrs.length == 2) {
                str += "," + "`" + node.fragmentId + "`" + "." + attrs[1];
            } else {
                str += "," + "`" + node.fragmentId + "`" + ".";
                for(int j = 1; j < attrs.length - 1; j++) {
                     str += attrs[j] + ".";
                }
                str += attrs[attrs.length - 1];
            }
        }
        if (str.charAt(0) == ',') {
            str = str.substring(1);
        }
        return str;
    }
    private String getProjectsByLeaf(TreeNode node) {
        String str = "";
        if(node.content.project.charAt(0) == ',') {
            node.content.project = node.content.project.substring(1);
        }
        String[] strs = node.content.project.split(",");
        for (int i = 0; i < strs.length - 1; i++) {
            str += "`" + node.fragmentId + "`" + "." + "`" + strs[i].trim() + "`" + ",";
        }
        str += "`" + node.fragmentId + "`" + "." + "`" + strs[strs.length - 1].trim() + "`";
        if (str.charAt(0) == ',') {
            str = str.substring(1);
        }
        return str;
    }
    private String getUnionProjects(TreeNode node) {
        String str = "";
        if(node.content.project.charAt(0) == ',') {
            node.content.project = node.content.project.substring(1);
        }
        String[] strs = node.content.project.split(",");
        //如果是叶子节点
        if(node.content.isLeaf) {
            for (int i = 0; i < strs.length; i++) {
                str += "," + "`" + strs[i] + "`";
            }
        } else {
            for (int i = 0; i < strs.length; i++) {
                String[] attrs = strs[i].split("[.]");
                if(attrs.length == 2) {
                    str += "," + attrs[1];
                } else {
                    str += ",";
                    for(int j = 1; j < attrs.length; j++) {
                        str += attrs[j];
                    }
                }
            }
        }
        if (str.charAt(0) == ',') {
            str = str.substring(1);
        }
        return str;
    }
    private String getProjectsByTreeNodeExcludeKey(TreeNode node, String key) {
        String str = "";
        if(node.content.project.charAt(0) == ',') {
            node.content.project = node.content.project.substring(1);
        }
        String[] strs = node.content.project.split(",");
        for (int i = 0; i < strs.length - 1; i++) {
            String attr = strs[i].split("[.]")[1];
            if(!attr.equals(key)) {
                str += "`" + node.fragmentId + "`" + "." + "`" + strs[i].trim() + "`" + ",";
            }
        }
        String attr = strs[strs.length - 1].split("[.]")[1];
        if(!attr.equals(key)) {
            str += "`" + node.fragmentId + "`" + "." + "`" + strs[strs.length - 1].trim() + "`";
        }
        if (str.charAt(0) == ',') {
            str = str.substring(1);
        }
        return str;
    }


    private List<TreeNode> getSameSiteNodes(TreeNode root, List<TreeNode> nodes) {
        List<TreeNode> resNodes = new ArrayList<>();
        for(TreeNode node : nodes) {
            if(node.site.equals(root.site)) {
                resNodes.add(node);
            }
        }
        return resNodes;
    }

    private void reviseLeafNode(TreeNode root) {
        if(root.content.project.charAt(root.content.project.length() - 1) == ',') {
            root.content.project = root.content.project.substring(0, root.content.project.length() - 1);
        }
        if(root.content.isLeaf) {
            String res = "";
            String[] strs = root.content.project.split(",");
            for(int i = 0; i < strs.length - 1; i++) {
                res += strs[i] + " as " + "`" +strs[i] + "`" + ",";
            }
            res += strs[strs.length - 1] + " as " + "`" +strs[strs.length - 1] + "`";
            root.content.project = res;

            String[] conditioStrings = root.content.condition.split("%");
            res = "";
            for(int i = 0; i < conditioStrings.length - 1; i++) {
                if(conditioStrings[i].length() > 0) {
                    Condition condition = Condition.fromJson(conditioStrings[i]);
                    res += condition.leftValue.attrName.split("[.]")[1];
                    res += condition.StringByOpType(condition.op);
                    res += condition.rightValue.value;
                    res +=" and ";
                }
            }
            if(conditioStrings[conditioStrings.length - 1].length() > 0) {
                Condition condition = Condition.fromJson(conditioStrings[conditioStrings.length - 1]);
                res += condition.leftValue.attrName.split("[.]")[1];
                res += condition.StringByOpType(condition.op);
                res += condition.rightValue.value;
            }
            root.content.condition = res;
            return ;
        } else {
            for(int i = 0; i < root.children.size(); i++) {
                reviseLeafNode(root.children.get(i));
            }
        }
    }
    private void storeInEtcd(TreeNode root) throws Exception{
        etcdClient.put(root.fragmentId, root.content.toJson());
        for(int i = 0; i < root.children.size(); i++) {
            storeInEtcd(root.children.get(i));
        }
    }
    private TreeNode Join(TreeNode leftNode, TreeNode rightNode, String leftAttr, String rightAttr, String site) {
        TreeNode root = new TreeNode();
        root.site = site;
        root.children.add(leftNode);
        root.children.add(rightNode);
        root.content.project = "";
        root.fragmentId += leftNode.fragmentId +rightNode.fragmentId;
        root.content.tableName = root.fragmentId;
        root.content.project += getProjectsByTreeNode(leftNode) + "," + getProjectsByTreeNode(rightNode);
        root.content.isLeaf = false;
        root.content.isUnion = false;
        root.content.children.add(leftNode.fragmentId);
        root.content.children.add(rightNode.fragmentId);
        root.content.addresses.add(leftNode.site);
        root.content.addresses.add(rightNode.site);
        root.content.joinAttribute = "`" + leftNode.fragmentId + "`" + "." + "`" + leftAttr + "`" + "=" + "`" + rightNode.fragmentId + "`" + "." + "`" + rightAttr + "`";
        root.childNum += 2;
        return root;
    }
//    private void
}