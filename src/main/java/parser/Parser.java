package parser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.AttributeConstant;
import entity.RelationConstant;
import entity.SiteConstant;
import entity.RelationConstant;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import net.sf.jsqlparser.util.SelectUtils;
import net.sf.jsqlparser.util.TablesNamesFinder;
import network.EtcdClient;
import network.SqlClient;

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
        SELECT, INSERT, DEFINESITE, ERROR, CREATETABLE, FRAGMENT
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
        return InputState.ERROR;
    }

    private void createFragment(String input) throws Exception {
        //parse
        String[] strs = input.split("horizontally into");
        
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
            attributeList.add(columnDefinition.getColumnName());//attrname存到数据表中
            AttributeConstant attributeConstant = new AttributeConstant(columnDefinition.getColumnName());
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
            SqlClient sqlClient = new SqlClient(site);
            sqlClient.executeNonQuery(input);
        }
    }

    private void parseDefineSite(String cmdString) throws Exception{
        //加入etcd中
        String[] siteInfo = cmdString.split(" ");
        String siteName = siteInfo[2];
        String siteAddr = siteInfo[3];
        SiteConstant site = new SiteConstant(siteName);
        String str = etcdClient.get("sites");
        str += "," + siteName;
        etcdClient.put("sites", str);
        etcdClient.put(site.getSiteIp(), siteAddr);//加入etcd中
    }

    private void parseSelectSql(String sql) throws Exception{
       Statement statement = CCJSqlParserUtil.parse(sql);
       TablesNamesFinder tablesNamesFinder =new TablesNamesFinder();
       tablesNamesFinder.getTableList(statement);
    }
}