package entity;

import network.TempTable;
import utils.mappingtool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Kalven
 * @version 10.0
 * Created by Kalven on 2021/11/24
 */
public class TreeNode {
    public TreeNode(){
        
    }

    public static mappingtool.layer dfs(TreeNode root) {
        mappingtool.layer m = new mappingtool.layer(root.content.tableName + root.site);
        for(int i = 0; i < root.children.size(); i++) {
            m.addNext(dfs(root.children.get(i)));
        }
        return m;
    }
    public boolean isUsed = true;
    public TempTable content = new TempTable();
    public String fragmentId = "";
    public String site = "";
    public List<TreeNode> children = new ArrayList<>();
    public int childNum = 0;
    public int predictNum = 0;
    public int nodeSize = 1;
    public int nodePos = 0;
    //是否需要key,false表示实际上没有key
    public boolean needKey = true;
}
