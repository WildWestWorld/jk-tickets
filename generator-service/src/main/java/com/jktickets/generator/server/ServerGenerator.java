package com.jktickets.generator.server;


import com.jktickets.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerGenerator {

    static String serverPath = "generator-service\\src\\main\\java\\com\\jktickets\\generator\\test\\";
    static String pomPath = "generator-service/pom.xml";

    static {
        new File(serverPath).mkdirs();
    }


    //    生成代码 主路径
    public static void main(String[] args) throws Exception {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
//        读取pom路径
        Document document = saxReader.read(pomPath);
//        读取   pom中的 configurationFile节点
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());


////        生成类
////        设置模板路径
//        FreemarkerUtil.initConfig("test.ftl");
////        设置模板中的参数
//        HashMap<String, Object> param = new HashMap<>();
////        domain:在ftl模板的参数  Test:输入到domain的参数
//        param.put("domain","Test");
////        设置生成路径
//        FreemarkerUtil.generator(serverPath+"Test.java",param);
    }


}
