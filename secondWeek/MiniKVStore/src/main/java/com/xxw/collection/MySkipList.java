package com.xxw.collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySkipList {

    private static class Node{
        String key;
        String value;
        Node[] forward;

        Node(String key, String value, Integer level){
            this.key = key;
            this.value = value;
            this.forward = new Node[level + 1];
        }
    }

    private final int MAX_LEVEL = 16;
    private final Node head = new Node(null, null, MAX_LEVEL);
    private int currentLevel = 0;

    private int randLevel(){
        int level = 0;
        while (Math.random() < 0.5 && level < MAX_LEVEL){
            level++;
        }
        return level;
    }

    public void put(String key, String value){
        Node[] update = new Node[MAX_LEVEL + 1];
        Node curr = head;

        //从最高层开始找到每一层比key小的一个node存入update[i]
        for (int i = currentLevel; i >= 0; i--){
            while (curr.forward[i] != null && curr.forward[i].key.compareTo(key) < 0){
                curr = curr.forward[i];
            }

            update[i] = curr;
        }

        //next是第0层比key大的第一个node
        Node next = curr.forward[0];

        //next存放的key与新增的一样，覆盖
        if(next != null && next.key.equals(key)){
            next.value = value;
        }else{
            int level = randLevel();

            //随机到的level > currentLevel，将新层的第一个(update[i] 原 == null)置为head
            if(level > currentLevel){
                for (int i = currentLevel + 1; i <= level; i++){
                    update[i] = head;
                }
                currentLevel = level;
            }

            //新建一个Node, 将其放置与每层update和update.forward中间
            Node newNode = new Node(key, value, level);
            for (int i = 0; i <= level; i++){
                newNode.forward[i] = update[i].forward[i];
                update[i].forward[i] = newNode;
            }
        }
    }

    public void remove(String key) {
        if (key == null) return;

        Node[] update = new Node[MAX_LEVEL + 1];
        Node curr = head;

        //update记录每一层的前驱
        for (int i = currentLevel; i >= 0; i--) {
            while (curr.forward[i] != null &&
                    curr.forward[i].key.compareTo(key) < 0) {
                curr = curr.forward[i];
            }
            update[i] = curr;
        }

        //确认target是否存在
        Node target = curr.forward[0];
        if (target == null || !target.key.equals(key)) {
            return;
        }

        //断开所有层的指针
        int targetLevel = target.forward.length - 1;
        for (int i = 0; i <= targetLevel; i++) {
            //update[i] == target，跳过 target
            if (update[i].forward[i] == target) {
                update[i].forward[i] = target.forward[i];
            }
        }

    }

    public List<String> keysInorder(){
        List<String> result = new ArrayList<>();
        Node curr = head.forward[0];

        while (curr != null){
            result.add(curr.key);
            curr = curr.forward[0];
        }
        return result;
    }

    public Map<String, String> rangeQuery(String min, String max){
        Map<String, String> result = new HashMap<>();
        Node curr = head.forward[0];

        while (curr != null){
            //curr在min与max区间
            if(curr.key.compareTo(min) >= 0 && curr.key.compareTo(max) <= 0){
                result.put(curr.key, curr.value);
            }

            //curr不在区间
            if(curr.key.compareTo(max) > 0) break;

            curr = curr.forward[0];
        }

        return result;
    }
}
