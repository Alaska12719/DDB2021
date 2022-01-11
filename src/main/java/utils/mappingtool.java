package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mappingtool {
    public static class layer {
        private String content = "default content";
        private String content_length = "                                                                          ";
        private int blank_num = 0;
        private List<layer> next = new ArrayList<>();

        public layer(String content) {
            this.content = content;
            this.blank_num = this.content.length();
            setBlank();
        }

        private void setBlank() {
            if (this.blank_num < this.content_length.length()) {
                this.content_length = this.content_length.substring(0,this.blank_num);
            } else {
                char[] chars = new char[this.blank_num];  Arrays.fill(chars,' ');
                this.content_length = new String(chars);
            }

        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void addNext(layer layer){
            layer.blank_num = layer.content.length() + this.content_length.length();
            layer.setBlank();
            next.add(layer);
        }

        public layer getLayerByNum(int num){
            return this.next.get(num-1);
        }

        public void removeLayer(layer layer){
            next.remove(layer);
        }

        @Override
        public String toString() {
            String str = ""+this.content+"\n";
            if (this.next==null || this.next.size() < 1){
                return str;
            }
            for(int i=0;i<this.next.size();i++){
                str = str + this.content_length + "|___"+this.next.get(i).toString();
            }
            return str;
        }
    }
}