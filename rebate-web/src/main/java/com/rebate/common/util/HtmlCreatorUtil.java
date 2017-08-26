package com.rebate.common.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;

public class HtmlCreatorUtil {

    private static final Logger LOG = Logger.getLogger(HtmlCreatorUtil.class);

    public static void createHtml(String templatePath,String htmlPath,String templateName,String html, Map<String,Object> paramMap){
        try {

            //创建一个合适的Configration对象
            Configuration configuration = new Configuration();
            configuration.setDirectoryForTemplateLoading(new File(templatePath));
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            configuration.setDefaultEncoding("UTF-8");   //这个一定要设置，不然在生成的页面中 会乱码
            //获取或创建一个模版。
            Template template = configuration.getTemplate(templateName);

            Writer writer = new OutputStreamWriter(new FileOutputStream(htmlPath+html), "UTF-8");
            template.process(paramMap, writer);

            LOG.error("Html create success!");
        } catch (IOException e) {
            LOG.error("createHtml error!",e);
        } catch (TemplateException e) {
            LOG.error("createHtml error!",e);
        }
    }

}
