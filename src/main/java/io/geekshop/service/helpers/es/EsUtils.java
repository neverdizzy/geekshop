package io.geekshop.service.helpers.es;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * @author bo.chen
 * @date 2021/11/19
 **/
public class EsUtils {

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 下划线转驼峰
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 原字符是否包含 指定字符(不区分大小写)
     * @param srcStr 原字符
     * @param tags 目标数组
     * @return
     */
    public static boolean isCludeCharByOr(String srcStr,String... tags){
        if(tags != null && tags.length>0){
            for(String str:tags){
                if(isEmpty(str))continue;
                if(srcStr.toLowerCase().contains(str))return true;
            }
        }
        return false;
    }

    /**
     * 对象生成properties 属性map
     * 下面的else if 判断还有很多，懒得写，根据自己的需求完善吧
     * @param t
     * @return
     */
    public static Map<String,Object> getProperties(Object t){
        Map<String,Object> propertiesMap = new HashMap<>();
        if(t != null){
            Class<? extends Object> tClass = t.getClass();
            //得到所有属性
            Field[] fields = tClass.getDeclaredFields();
            if(fields!=null&& fields.length>0){
                for(Field field:fields){
                    String fieldClassStr = field.getGenericType().toString();
                    Map<String,Object> fieldAttrMap = new HashMap<>();
                    if("class java.lang.Long".equals(fieldClassStr)){
                        fieldAttrMap.put("type","long");
                    }else if("class java.lang.Double".equals(fieldClassStr)){
                        fieldAttrMap.put("type","double");
                    }else if("class java.time.LocalDateTime".equals(fieldClassStr) || "class java.util.Date".equals(fieldClassStr) || "class java.time.LocalDate".equals(fieldClassStr)){
                        fieldAttrMap.put("type","date");
                        fieldAttrMap.put("format","yyyy-MM-dd HH:mm:ss || yyyy-MM-dd'T'HH:mm:ss.SSS || yyyy-MM-dd || epoch_millis");
                    }else if("class java.lang.Boolean".equals(fieldClassStr)){
                        fieldAttrMap.put("type","boolean");
                    }else if("class java.lang.String".equals(fieldClassStr) && EsUtils.isCludeCharByOr(field.getName(),"id,type,state,status".split(","))){
                        fieldAttrMap.put("type","keyword");//keyword：存储数据时候，不会分词建立索引，支持模糊、支持精确匹配，支持聚合、排序操作
                    }else{
                        fieldAttrMap.put("type","text");//分词建立索引
                        fieldAttrMap.put("analyzer", "ik_max_word");
                        fieldAttrMap.put("search_analyzer", "ik_smart");
                    }
                    propertiesMap.put(EsUtils.humpToLine(field.getName()),fieldAttrMap);
                }
            }

        }
        return propertiesMap;
    }

    /**
     * 判空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        return str == null || "".equals(str);
    }

}
