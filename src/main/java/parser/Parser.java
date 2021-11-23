package parser;

import entity.AttributeConstant;
import entity.Condition;
import entity.FragmentConstant;
import entity.RelationConstant;
import entity.SiteConstant;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import network.EtcdClient;
import network.SqlClient;
import network.TempTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kalven
 * @version 10.0 Created by Kalven on 2021/11/5
 */

public class Parser {
    private EtcdClient etcdClient;

    public Parser() {
        etcdClient = new EtcdClient("127.0.0.1:2379");
    }

    public enum InputState {
        SELECT, INSERT, DEFINESITE, ERROR, CREATETABLE, FRAGMENT,ALLOCATE
    };

    public void parseInput(String input) throws Exception {
        switch (getInputType(input)) {
        case SELECT:
            parseSelectSql(input);
            break;
        case DEFINESITE:
            parseDefineSite(input);
            break;
        case CREATETABLE: createTable(input); break;
        case FRAGMENT: createFragment(input); break;
        case ALLOCATE: allocateFragment(input);break;
        case INSERT: parseInsert(input); break;
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
        if(input.contains("fragment")) {
            return InputState.FRAGMENT;
        }
        if(input.contains("allocate")) {
            return InputState.ALLOCATE;
        }
        if(input.contains("select")) {
            return InputState.SELECT;
        }
        //TO-DO 解析sql
        if(input.contains("insert")) {
            return InputState.INSERT;
        }
        return InputState.ERROR;
    }

    //插入所有节点查询时优化
    private void parseInsert(String sql) throws Exception {
        // Insert insertStatement = (Insert)CCJSqlParserUtil.parse(sql);
        // insertStatement.getColumns();
        // TablesNamesFinder tablesNamesFinder =new TablesNamesFinder();
        SiteConstant siteConstant = new SiteConstant("");
        String sites = etcdClient.get(siteConstant.getSites());
        String[] siteList = sites.split(",");
        for(String site : siteList) {
            site = site.trim();
            SqlClient sqlClient = new SqlClient(site);
            sqlClient.executeNonQuery(sql);
        }
        // List<String> tables = tablesNamesFinder.getTableList(statement);
        // tables[0]
    }

    private void allocateFragment(String input) throws Exception {
        String[] strs = input.split("to");
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
        if(strs.length < 2) {
            throw new Exception();
        }   
        String tableName = strs[0].split(" ")[1].trim();
        String[] horizons = strs[1].split(",");
        String fragments = "";
        for(int i = 0; i < horizons.length; i++) {
            String fragmentId = tableName + "." + i;
            fragments += fragmentId + ",";
            String[] conditions = horizons[i].split("and");
            String fragmentCondition = "";
            for(int j = 0; j <  conditions.length; j++) {
                conditions[j] = conditions[j].trim();
                Condition condition = parseCondition(conditions[j], tableName, false);
                fragmentCondition += condition.toJson() + ",";
            }
            FragmentConstant fragmentConstant = new FragmentConstant(fragmentId);
            etcdClient.put(fragmentConstant.getConditions(), fragmentCondition);
            etcdClient.put(fragmentConstant.getIsHorizontal(), "1");
        }
        FragmentConstant fragmentConstant = new FragmentConstant("");
        etcdClient.put(fragmentConstant.getFragments(), fragments);
    }

    //将str解析成condition,haveTable表示str中存在表名了
    private Condition parseCondition(String str, String tableName, boolean haveTable) {
        String[] strs = str.split("<>|<=|>=|>|<|=");
        Pattern p = Pattern.compile("[A-Za-z_]*");
        Matcher m = p.matcher(strs[0].trim());
        Condition condition = new Condition();
        String additionInfo = "";
        if(haveTable == false) {
            additionInfo = tableName + ".";
        }
        if(m.matches()) {
            condition.leftValue.attrName = additionInfo + strs[0].trim();
            condition.leftValue.isAttribute = true;
        } else {
            condition.leftValue.isAttribute = false;
            condition.leftValue.value = Double.parseDouble(strs[0].trim());
        }
        m = p.matcher(strs[1].trim());
        if(m.matches()) {
            condition.rightValue.attrName = additionInfo + strs[1].trim();
            condition.rightValue.isAttribute = true;
        } else {
            condition.rightValue.isAttribute = false;
            condition.rightValue.value = Double.parseDouble(strs[1].trim());
        }
        p = Pattern.compile("<>|<=|>=|>|<|=");
        m = p.matcher(str);
        m.find();
        String op = m.group();
        if(op.equals("<>")) {
            condition.op = Condition.opType.NOT_EQUAL;
        } else if(op.equals(">")) {
            condition.op = Condition.opType.GREATER_THAN;
        } else  if(op.equals("=")) {
            condition.op = Condition.opType.EQUAL_TO;
        } else  if(op.equals("<")) {
            condition.op = Condition.opType.MIN_THAN;
        } else  if(op.equals(">=")) {
            condition.op = Condition.opType.GREATER_EQUAL_TO;
        } else  if(op.equals("<=")) {
            condition.op = Condition.opType.MIN_EQUAL_TO;
        }
        return condition;
    }

    private void parseVertical(String[] strs) throws Exception {
        if(strs.length < 2) {
            throw new Exception();
        }
        String tableName = strs[0].split(" ")[1].trim();
        int lb = 0;
        ArrayList<String> verticals = new ArrayList<String>();
        int count = 0;
        for(int i = 0; i < strs[1].length(); i++) {
            if(strs[1].charAt(i) == ')') {
                verticals.add(strs[1].substring(lb + 1, i));
            }
            if(strs[1].charAt(i) == '(') {
                lb = i;
            }
        }
        String fragments = "";
        for(int i = 0; i < verticals.size(); i++) {
            String fragmentId = tableName + "." + i;
            fragments += fragmentId + ",";
            String[] attributes = verticals.get(i).split(",");
            String fragmentAttributes = "";
            for(int j = 0; j <  attributes.length; j++) {
                attributes[j] = attributes[j].trim();
                fragmentAttributes += attributes[j] + ",";
            }
            FragmentConstant fragmentConstant = new FragmentConstant(fragmentId);
            etcdClient.put(fragmentConstant.getAttributes(), fragmentAttributes);
            etcdClient.put(fragmentConstant.getIsVertical(), "1");
        }
        FragmentConstant fragmentConstant = new FragmentConstant("");
        etcdClient.put(fragmentConstant.getFragments(), fragments);
    }

    private void createFragment(String input) throws Exception {
        //parse Horizon
        if(input.contains("horizontally into")) {
            String[] strs = input.split("horizontally into");
            parseHorizon(strs);
        }
        //parse Vertical
        if(input.contains("vertically into")) {
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
        str += relationName;
        etcdClient.put(relation.getRelations(), str);
        
        ArrayList<String> attributeList = new ArrayList<String>();
        //etcd存attr
        String attributsName = "";
        for(ColumnDefinition columnDefinition: statement.getColumnDefinitions()) {
            SiteConstant siteConstant = new SiteConstant("");
            String sites = etcdClient.get(siteConstant.getSites());
            String[] siteList = sites.split(",");

            attributeList.add(columnDefinition.getColumnName());//attrname存到数据表中
            AttributeConstant attributeConstant = new AttributeConstant(statement.getTable().getName(), columnDefinition.getColumnName());
            for(String site : siteList) {
                etcdClient.put(attributeConstant.getIsExitSite(site), "1");
            }
            etcdClient.put(attributeConstant.getIsHorizon(), "0");
            etcdClient.put(attributeConstant.getIsVertical(), "0");
            etcdClient.put(attributeConstant.getAttributeType(), columnDefinition.getColDataType().toString());
        }

        for(String attribute : attributeList) {
            attributsName += "," + attribute;
        }
        etcdClient.put(relation.getAttributes(), attributsName);
        //2.往数据库里存table
        SiteConstant siteConstant = new SiteConstant("");
        String sites = etcdClient.get(siteConstant.getSites());
        String[] siteList = sites.split(",");
        for(String site : siteList) {
            site = site.trim();

            SqlClient sqlClient = new SqlClient(site);
            sqlClient.executeNonQuery(input);
        }
    }

    private void parseDefineSite(String cmdString) throws Exception{
        //加入etcd中
        String[] siteInfo = cmdString.split(" ");
        String siteName = siteInfo[2].trim();
        String siteAddr = siteInfo[3].trim();
        SiteConstant site = new SiteConstant(siteName);
        String str = etcdClient.get("sites");
        str += "," + siteName;
        etcdClient.put("sites", str);
        etcdClient.put(site.getSiteIp(), siteAddr);//加入etcd中
    }

    //现在做的都是多表查询而且表名.属性，TO-DO单表的查询
    private void parseSelectSql(String sql) throws Exception{
       Select selectStatement = (Select)CCJSqlParserUtil.parse(sql);
       TablesNamesFinder tablesNamesFinder =new TablesNamesFinder();
       //单表情况下
       String tableName = tablesNamesFinder.getTableList(selectStatement).get(0);
       PlainSelect plainSelect = (PlainSelect)selectStatement.getSelectBody();
       List<AttributeConstant> selectAttributes = new ArrayList<>();
       List<AttributeConstant> whereAttributes = new ArrayList<>();
       List<Condition> conditions = new ArrayList<>();
       for(int i = 0; i < plainSelect.getSelectItems().size(); i++) {
           //选中的属性
           SelectExpressionItem item = (SelectExpressionItem) plainSelect.getSelectItems().get(i);
           Column column = (Column)item.getExpression();
           String attributeName = column.getColumnName();
           String tmpTableName = tableName;
           if(column.getTable() != null) {
               tmpTableName = column.getTable().getName();
           }
           AttributeConstant attributeConstant = new AttributeConstant(tmpTableName, attributeName);
           selectAttributes.add(attributeConstant);
       }

       String where = plainSelect.getWhere().toString();
       String[] conditionStrings = where.split("and");
       for(int i = 0; i < conditionStrings.length; i++) {
           boolean haveTable = false;
           if(conditionStrings[0].contains(".")) {
               haveTable = true;
           }
           Condition condition = parseCondition(conditionStrings[i], tableName, haveTable);
           conditions.add(condition);
           if(condition.leftValue.isAttribute == true) {
               String[] attrs = condition.leftValue.attrName.split(".");
               AttributeConstant constant = new AttributeConstant(attrs[0], attrs[1]);
               whereAttributes.add(constant);
           }
           if(condition.rightValue.isAttribute == true) {
               String[] attrs = condition.rightValue.attrName.split(".");
               AttributeConstant constant = new AttributeConstant(attrs[0], attrs[1]);
               whereAttributes.add(constant);
           }
       }
       Set<AttributeConstant> finalSet = new HashSet<>();
       finalSet.addAll(whereAttributes);
       finalSet.addAll(selectAttributes);
       //对于所有的表名和属性在etcd里查询
        String[] fragmentsId = etcdClient.get("fragments").split(",");
        for(String fragmentId: fragmentsId) {
            FragmentConstant fragmentConstant = new FragmentConstant(fragmentId);
            //水平分片,默认拥有所有的属性,TO-DO 混合分片
            if(etcdClient.get(fragmentConstant.getIsHorizontal()).equals("1")) {
                String[] strs = etcdClient.get(fragmentConstant.getConditions()).split(",");
                TempTable tempTable = new TempTable();
                tempTable.isLeaf = true;
                for(int j = 0; j < selectAttributes.size(); j++) {
                    //分片存了表名
                    if(fragmentId.contains(selectAttributes.get(j).getRelationName())) {
//                        tempTable.tableName
                    }
                }
                //
                for(int i = 0; i < strs.length; i++) {
                    Condition conditions1 = Condition.fromJson(strs[i]);
                }
            } else {
                String[] attributes = etcdClient.get(fragmentConstant.getAttributes()).split(",");

            }
        }
    }

//    private void
}