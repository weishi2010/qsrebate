package com.rebate.common.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
public class ProfilePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(ProfilePropertyPlaceholderConfigurer.class.getName());
    private static final String DEFAULT_PROFILE_CONFIG_PATH = "profile.properties";

    // 主控文件路径
    private String profileConfigPath = DEFAULT_PROFILE_CONFIG_PATH;
    // 托底配置,配置文件里没有配的话会使用这个
    private String fallbackProfile;


    private Properties profile;

    private void loadConfig(){
        // 已经直接配置profile，不读取配置文件了
        if (profile !=null){
            return ;
        }
        try {
            profile = PropertiesLoaderUtils.loadAllProperties(profileConfigPath);
            if (profile.size()==0){
                throw new RuntimeException(profileConfigPath+" is empty");
            }
        } catch (Exception e) {
            throw new RuntimeException("加载["+profileConfigPath+"]失败",e);
        }
    }

    @Override
    public void setLocations(Resource[] locations) {
        loadConfig();

        PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper(
                this.placeholderPrefix,
                this.placeholderSuffix,
                this.valueSeparator,
                this.ignoreUnresolvablePlaceholders);


        if(locations !=null ){
            List<Resource> resources = new ArrayList<Resource>();

            for(int i=0; i< locations.length; i++){
                Resource r = locations[i];
                if(r==null){
                    continue;
                }
                if(r instanceof ClassPathResource){
                    String path = ((ClassPathResource)r).getPath();

                    String basePath= placeholderHelper.replacePlaceholders(path, profile);
                    ClassPathResource baseRes = checkClassPathResource(basePath);
                    if(baseRes!=null) {
                        LOG.info("加载基础属性文件: {} <- {}", basePath, path);
                        resources.add(baseRes);
                    }else{
                        LOG.error("基础属性文件不存在,忽略: {} <- {}", basePath, path);
                        throw new RuntimeException("The base property file is not exists!path:"+basePath);
                    }

                }else{
                    //newRes[i]=r;
                    LOG.debug("加载属性文件: {}", r.getFilename());
                    resources.add(r);
                }
            }
            Resource[] clacLocations = resources.toArray(new Resource[resources.size()]);
            super.setLocations( clacLocations );
        }
    }

    private ClassPathResource checkClassPathResource(String path){
        ClassPathResource classPathResource = new ClassPathResource(path);
        if(classPathResource.exists() && classPathResource.isReadable()){
            return classPathResource;
        }
        return null;
    }


    @Override
    protected Properties mergeProperties() throws IOException {
        Properties p= super.mergeProperties();

        for (Map.Entry<Object, Object> entry : profile.entrySet()) {
            p.setProperty((String)entry.getKey(), (String) entry.getValue());
        }

        return p;
    }

    public String getFallbackProfile() {
        return fallbackProfile;
    }

    public void setFallbackProfile(String fallbackProfile) {
        this.fallbackProfile = fallbackProfile;
    }



    public String getProfileConfigPath() {
        return profileConfigPath;
    }

    public void setProfileConfigPath(String profileConfigPath) {
        this.profileConfigPath = profileConfigPath;
    }

}
