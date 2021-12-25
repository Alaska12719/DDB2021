package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;//代码中使用了Scanner类
import java.util.Set;

import entity.TreeNode;
import javafx.util.*;

public class Graph{
    //定义一下的属性变量
    Map<String, Node> graphMap = new HashMap<>();
    int count = 0;
    class Node {
        public Node() {

        }
        public Node(TreeNode node) {
            content = node;
        }
        public TreeNode content = null;
        public Set<Node> children = new HashSet();
    }
    public Graph() {

    }
    public List<TreeNode> allNode = new ArrayList<>(); 
    
    public void addPair(Pair<TreeNode,TreeNode> pair) {
        if(!graphMap.containsKey(pair.getKey().fragmentId)) {

            Node node = new Node(pair.getKey());
            graphMap.put(pair.getKey().fragmentId, node);
        }
        if(!graphMap.containsKey(pair.getValue().fragmentId)) {
            Node node = new Node(pair.getValue());
            graphMap.put(pair.getValue().fragmentId, node);
        }

        Node leftNode = graphMap.get(pair.getKey().fragmentId);
        Node rightNode = graphMap.get(pair.getValue().fragmentId);

        leftNode.children.add(rightNode);
        rightNode.children.add(leftNode);
        graphMap.put(pair.getKey().fragmentId, leftNode);
        graphMap.put(pair.getValue().fragmentId, rightNode);
    }

    public List<TreeNode> visit(TreeNode rootContent) {
        Node root = graphMap.get(rootContent.fragmentId);
        Set<Node> nodes = new HashSet<>();
        Set<Node> nodeList = new HashSet<>();
        nodes.add(root);
        nodeList.add(root);
        Set<String> tableSet = new HashSet<>();
        tableSet.add(root.content.fragmentId.split("-")[0]);
        bfs(root, nodes, nodeList, tableSet);
        List<TreeNode> res = new ArrayList<>();
        for(Node n : nodeList) {
            res.add(n.content);
        }
        return res;
    }
    
    public void bfs(Node root, Set<Node> curNodes, Set<Node> nodeList, Set<String> tableSet) {
        Set<Node> nodes = new HashSet<>();
        Set<String> curSet = new HashSet<>();
        if(curNodes.isEmpty()) {
            return ;
        }
        curSet.addAll(tableSet);
        for(Node curNode : curNodes){
            for(Node chileNode : curNode.children) {
                if(!tableSet.contains(chileNode.content.fragmentId.split("-")[0])) {
                    nodes.add(chileNode);
                    curSet.add(chileNode.content.fragmentId.split("-")[0]);
                    nodeList.add(chileNode);
                }
            }
        }
        bfs(root, nodes, nodeList, curSet);
    }
    public boolean isSameTable(Node node1, Node node2) {
        return node1.content.fragmentId.split("-")[0].equals(node2.content.fragmentId.split("-")[0]);
    }
    public boolean isSameSite(Node node1, Node node2) {
        return node1.content.site.equals(node2.content.site);
    }
}