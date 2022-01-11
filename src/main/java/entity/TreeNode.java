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
    // public static void Print(TreeNode root) {
    //     //计算每一层元素个数
    //     Queue<TreeNode> queue1 = new LinkedList<>();
    //     Queue<TreeNode> queue2 = new LinkedList<>();
    //     queue1.offer(root);
    //     while(!queue1.isEmpty()) {
    //         int pos = 0;
    //         while(!queue1.isEmpty()) {
    //             TreeNode node = queue1.remove();
    //             pos = node.nodePos + node.nodeSize / 2 - pos;
    //             for(int i = 0; i < pos; i++) {
    //                 System.out.print("                            ");
    //             }
    //             System.out.print(node.content.tableName + node.site);
    //             for(int i = 0; i < node.children.size(); i++) {
    //                 queue2.offer(node.children.get(i));
    //             }
    //         }
    //         System.out.println();
    //         queue1.addAll(queue2);
    //         queue2.clear();
    //     }
    // }
    
    // public static int calculateNode(TreeNode root) {
    //     if(root.children.size() == 0) {
    //         return 1;
    //     }
    //     int sum = 0;
    //     for(int i = 0; i < root.children.size(); i++) {
    //         root.children.get(i).nodePos = sum;
    //         root.nodeSize += calculateNode(root.children.get(i)); 
    //         sum = root.nodeSize;
    //     }
    //     return root.nodeSize;
    // }
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
