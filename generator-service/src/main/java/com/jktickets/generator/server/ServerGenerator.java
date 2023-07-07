package com.jktickets.generator.server;


import com.jktickets.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerGenerator {

//    static String serverPath = "generator-service\\src\\main\\java\\com\\jktickets\\generator\\test\\";
    static String serverPath = "[module]-service/src/main/java/com/jktickets/";
    static String pomPath = "generator-service/pom.xml";

    static {
        new File(serverPath).mkdirs();
    }


    //    生成代码 主路径
    public static void main(String[] args) throws Exception {


        // 获取mybatis-generator
//        来自于POM文件
        String generatorPath = getGeneratorPath();
        // 比如generator-config-member.xml，得到module = member
        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module: " + module);
        serverPath = serverPath.replace("[module]", module);
        // new File(servicePath).mkdirs();
        System.out.println("servicePath: " + serverPath);



        // 读取table节点
//        查找POM中的TBALE节点中的TableName和domainObjectName 也就是拿到到 模板变量Domain domain
        Document document = new SAXReader().read("generator-service/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());



        // 示例：表名 jiawa_test
        // Domain = JiawaTest
        String Domain = domainObjectName.getText();
        // Domain 第一个字符变小写然后拼接  =>  domain = jiawaTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = jiawa-test 表名XX_XX 转成XX-XX
        String do_main = tableName.getText().replaceAll("_", "-");


        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("module", module);
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);

        System.out.println("组装参数：" + param);


        gen(Domain, param, "service", "service");
        genImpl(Domain, param, "service", "serviceImpl");


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

//    设置POM中的生成路径
    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
//        读取pom路径
        Document document = saxReader.read(pomPath);
//        读取   pom中的 configurationFile节点
        Node node = document.selectSingleNode("//pom:configurationFile");
        return node.getText();
    }


    private static void gen(String Domain, Map<String, Object> param, String packageName, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = serverPath + packageName + "/";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    private static void genImpl(String Domain, Map<String, Object> param, String packageName, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = serverPath +packageName + "/impl/" ;
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

}
