package com.rebate.common.data.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class JsonDataReader {
    private Map<String, String> map = new HashMap();

    public JsonDataReader(String path) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] e = resolver.getResources(path);
            Resource[] arr$ = e;
            int len$ = e.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Resource resource = arr$[i$];
                this.map.put(resource.getFilename(), IOUtils.toString(resource.getInputStream(), "UTF-8"));
            }
        } catch (IOException var8) {
            var8.printStackTrace();
        }

    }

    public String get(String fileName) {
        return (String)this.map.get(fileName);
    }

    public List<Map<String, Object>> getList(String fileName) {
        String json = this.get(fileName);
        ArrayList list = new ArrayList();
        JSONArray jsonArray = JSON.parseArray(json);

        for(int i = 0; i < jsonArray.size(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            list.add(jsonObject);
        }

        return list;
    }
}
