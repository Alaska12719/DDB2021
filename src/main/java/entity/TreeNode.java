package entity;

import network.TempTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalven
 * @version 10.0
 * Created by Kalven on 2021/11/24
 */
public class TreeNode {
    public TreeNode(){

    }
    public boolean isUsed = true;
    public TempTable content = new TempTable();
    public String fragmentId = "";
    public String site;
    public List<TreeNode> children = new ArrayList<>();
    public int childNum = 0;
    //是否需要key,false表示实际上没有key
    public boolean needKey;
}
