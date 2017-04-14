package com.iip.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Demo on 4/14/2017.
 */
@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private static final String DEFAULT_REPLACEMENT = "***";

    private TriesNode rootNode = new TriesNode();

    private class TriesNode {
        private boolean end = false;

        private Map<Character, TriesNode> subNode = new HashMap<>();

        public void addSubNode(char key, TriesNode node) {
            subNode.put(key, node);
        }

        public TriesNode getSubNode(char key) {
            return subNode.get(key);
        }

        public void setEnd(boolean end) {
            this.end = end;
        }

        public boolean isEnd() {
            return end;
        }
    }


    private void addWord(String lineTxt) {
        TriesNode tmpNode = rootNode;
        for(int i = 0; i < lineTxt.length(); ++i) {
            char c = lineTxt.charAt(i);
            if(isSymbol(c)) {
                continue;
            }
            TriesNode node = tmpNode.getSubNode(c);

            if(node == null) {
                node = new TriesNode();
                tmpNode.addSubNode(c, node);

            }
            tmpNode = node;

            if(i == lineTxt.length() - 1) {
                tmpNode.setEnd(true);
            }
        }
    }

    private boolean isSymbol(char c) {
        int num = (int)c;
        return !CharUtils.isAsciiAlphanumeric(c) && (num < 0x2E80 || num > 0x9FFF);
    }




    public String filter(String text) {
        if(StringUtils.isBlank(text)) {
            return text;
        }
        int begin = 0;
        int cur = 0;
        TriesNode tmpNode = rootNode;
        StringBuilder sb = new StringBuilder();
        while(cur < text.length()) {
            char c = text.charAt(cur);
            if(isSymbol(c)) {
                if(tmpNode == rootNode) {
                    ++begin;
                    sb.append(c);
                }
                ++cur;
                continue;
            }

            tmpNode = tmpNode.getSubNode(c);

            if(tmpNode == null) {
                sb.append(text.charAt(begin));
                ++begin;
                cur = begin;
                tmpNode = rootNode;
            }else if(tmpNode.isEnd()){
                sb.append(DEFAULT_REPLACEMENT);
                ++cur;
                begin = cur;
                tmpNode = rootNode;
            }else {
                ++cur;
            }
        }
        sb.append(text.substring(begin));
        return HtmlUtils.htmlEscape(sb.toString());

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TriesNode();

        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = br.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);

            }
            read.close();
        }catch (Exception e) {
            logger.error("read files failed." + e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        SensitiveService s = new SensitiveService();
//        s.addWord("色情");
//        s.addWord("好色");
//        System.out.println(s.filter("这是色情。"));
//    }
}
