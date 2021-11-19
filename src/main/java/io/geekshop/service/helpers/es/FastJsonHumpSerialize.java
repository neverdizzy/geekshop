package io.geekshop.service.helpers.es;

/**
 * @author bo.chen
 * @date 2021/11/19
 **/

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;

/**
 * 驼峰序列化配置
 *  https://github.com/alibaba/fastjson/wiki/PropertyNamingStrategy_cn
 *  String text = JSON.toJSONString(model, serializeConfig);
 *  Model model2 = JSON.parseObject(text, Model.class, parserConfig);
 */
public class FastJsonHumpSerialize {

    private static SerializeConfig serializeConfig;
    private static ParserConfig parserConfig;

    public static SerializeConfig getSerializeConfig() {
        if(serializeConfig==null){
            synchronized (FastJsonHumpSerialize.class){
                if(serializeConfig==null){
                    serializeConfig=new SerializeConfig();
                    serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                }
            }
        }
        return serializeConfig;
    }

    public static ParserConfig getParserConfig() {
        if(parserConfig==null){
            synchronized (FastJsonHumpSerialize.class){
                if(parserConfig==null){
                    parserConfig=new ParserConfig();
                    parserConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                }
            }
        }
        return parserConfig;
    }
}
