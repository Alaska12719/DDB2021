package parser;

import entity.AttributeConstant;
import entity.Condition;
import entity.FragmentConstant;
import entity.RelationConstant;
import entity.SiteConstant;
import entity.TreeNode;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import network.EtcdClient;
import network.SqlClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        etcdClient = new EtcdClient("http://127.0.0.1:2379");
    }

    public void closeEtcd() {
        etcdClient.close();
    }

    public enum InputState {
        SELECT, INSERT, DEFINESITE, ERROR, CREATETABLE, FRAGMENT, ALLOCATE
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
                executeSql(input);
                break;
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
        return InputState.ERROR;
    }

    //插入所有节点查询时优化
    private void executeSql(String sql) throws Exception {
        SiteConstant siteConstant = new SiteConstant("");
        String sites = etcdClient.get(siteConstant.getSites());
        String[] siteList = sites.split(",");
        for (String site : siteList) {
            site = site.trim();
            SqlClient sqlClient = new SqlClient(site);
            sqlClient.executeNonQuery(sql);
        }
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
                fragmentCondition += condition.toJson() + ",";
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
        Pattern p = Pattern.compile("[A-Za-z_]*");
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
    private void parseSelectSql(String sql) throws Exception {
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

        String where = plainSelect.getWhere().toString();
        String[] conditionStrings = where.split("AND");
        for (int i = 0; i < conditionStrings.length; i++) {
            boolean haveTable = false;
            if (conditionStrings[0].contains(".")) {
                haveTable = true;
            }
            Condition condition = parseCondition(conditionStrings[i], tableName, haveTable);
            conditions.add(condition);
            if (condition.leftValue.isAttribute == true) {
                String[] attrs = condition.leftValue.attrName.split(".");
                AttributeConstant constant = new AttributeConstant(attrs[0], attrs[1]);
                whereAttributes.add(constant);
            }
            if (condition.rightValue.isAttribute == true) {
                String[] attrs = condition.rightValue.attrName.split(".");
                AttributeConstant constant = new AttributeConstant(attrs[0], attrs[1]);
                whereAttributes.add(constant);
            }
        }
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
            tableName = fragmentId.split(".")[0];
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
                String[] strs = tempTable.content.condition.split(",");//分片中所有的condition
                List<Condition> fragmentConditions = new ArrayList<>();
                for (int i = 0; i < strs.length; i++) {
                    Condition conditions1 = Condition.fromJson(strs[i]);
                    fragmentConditions.add(conditions1);
                }
                for (Condition condition : conditions) {
                    boolean isUsed = false;
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
                            tempTable.content.condition += "," + condition.origin;
                        }
                    } else {
                        break;
                    }
                }
                if (tempTable.isUsed) {
                    if (tempTable.content.project.indexOf(0) == ',') {
                        tempTable.content.project = tempTable.content.project.substring(1);
                    }
                    if (tempTable.content.condition.indexOf(0) == ',') {
                        tempTable.content.condition = tempTable.content.condition.substring(1);
                    }
                    tempTable.content.condition = tempTable.content.condition.replaceAll(",", " and ");
                    tableName = fragmentId.split("-")[0].trim();
                    if (horizonMaps.get(tableName).isEmpty() || horizonMaps.get(tableName).size() == 0) {
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

                if (verticalMaps.get(tableName).isEmpty() || verticalMaps.get(tableName).size() == 0) {
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


        tableNode.get(0).content.toJson();

        //以上是test
//        //把垂直分片节点join在一起
//        for (Map.Entry<String, List<TreeNode>> entry : verticalMaps.entrySet()) {
//            tableName = entry.getKey();
//            List<TreeNode> treeNodes = entry.getValue();
//            if (treeNodes.size() == 1) {
//                if (!treeNodes.get(0).needKey) {
//                    treeNodes.get(0).content.project = "";
//                    String[] strs = treeNodes.get(0).content.project.split(",");
//                    for (int i = 0; i < strs.length; i++) {
//                        treeNodes.get(0).content.project += "," + strs[i];
//                    }
//                    tableNode.put(entry.getKey(), treeNodes.get(0));
//                } else {
//                    tableNode.put(entry.getKey(), treeNodes.get(0));
//                }
//            } else {
//                tableNode.put(entry.getKey(), JoinLeaf(treeNodes));
//            }
//        }
//        //把所有的表按照join条件组合在一起
//        for (Condition condition : conditions) {
//            if (condition.isJoin) {
//                TreeNode leftNode = tableNode.get(condition.leftValue.attrName.split(".")[0]);
//                TreeNode rightNode = tableNode.get(condition.rightValue.attrName.split(".")[0]);
//                Join(leftNode, rightNode, condition.leftValue.attrName.split(".")[1], condition.rightValue.attrName.split(".")[1]);
//            }
//        }

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

    private TreeNode Union(List<TreeNode> treeNodes) {
        TreeNode parent = new TreeNode();
        parent.content.isUnion = true;
        parent.content.isLeaf = false;
        parent.content.project = treeNodes.get(0).content.project;
        parent.content.condition = "";
        for (TreeNode node : treeNodes) {
            parent.content.children.add(node.fragmentId);
            parent.content.ips.add(node.site);
            parent.children.add(node);
            parent.childNum++;
            parent.site = node.site;
            parent.fragmentId += node.fragmentId;
        }
        parent.content.project = getProjectsByTreeNode(parent);
        return parent;
    }

    private TreeNode JoinLeaf(List<TreeNode> treeNodes) {
        TreeNode root = new TreeNode();
        root.site = treeNodes.get(0).site;
        root.children.add(treeNodes.get(0));
        root.children.add(treeNodes.get(1));
        root.content.project = "";
        root.content.project += getProjectsByTreeNode(treeNodes.get(0)) + "," + getProjectsByTreeNode(treeNodes.get(1));
        root.content.isLeaf = false;
        root.content.isUnion = false;
        root.content.children.add(treeNodes.get(0).fragmentId);
        root.content.children.add(treeNodes.get(1).fragmentId);
        root.content.ips.add(treeNodes.get(0).site);
        root.content.ips.add(treeNodes.get(1).site);
        root.childNum += 2;
        for (int i = 2; i < treeNodes.size(); i++) {
            TreeNode node = new TreeNode();
            node.children.add(root);
            node.children.add(treeNodes.get(i));
            node.childNum += 2;
            node.content.project = "";
            node.content.project += root.content.project + "," + getProjectsByTreeNode(treeNodes.get(i));
            node.content.isLeaf = false;
            node.content.isUnion = false;
            node.content.children.add(root.fragmentId);
            node.content.children.add(treeNodes.get(i).fragmentId);
            node.content.ips.add(root.site);
            node.content.ips.add(treeNodes.get(i).site);
            node.site = root.site;
            root = node;
        }
        return root;
    }

    private String getProjectsByTreeNode(TreeNode node) {
        String str = "";
        String[] strs = node.content.project.split(",");
        for (int i = 0; i < strs.length; i++) {
            str += "," + node.fragmentId + "." + strs[i].split(".")[1];
        }
        if (str.indexOf(0) == ',') {
            str = str.substring(1);
        }
        return str;
    }

    private TreeNode Join(TreeNode leftNode, TreeNode rightNode, String leftAttr, String rightAttr) {
        TreeNode root = new TreeNode();
        root.site = leftNode.site;
        root.children.add(leftNode);
        root.children.add(rightNode);
        root.content.project = "";
        root.content.project += getProjectsByTreeNode(leftNode) + "," + getProjectsByTreeNode(rightNode);
        root.content.isLeaf = false;
        root.content.isUnion = false;
        root.content.children.add(leftNode.fragmentId);
        root.content.children.add(rightNode.fragmentId);
        root.content.ips.add(leftNode.site);
        root.content.ips.add(rightNode.site);
        root.content.joinAttribute = leftNode.fragmentId + leftAttr + "=" + rightNode.fragmentId + rightAttr;
        root.childNum += 2;
        return root;
    }
//    private void
}