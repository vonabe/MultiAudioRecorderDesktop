package Network;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author wenkael™
 */
public class ParseTJson{
    
    private final Interface Interface;
    
    public static enum Type {
        ARRAY,OBJECT,VALUE,PARSER,AWARE,STRING
    }

    public ParseTJson() {this.Interface = null;}
    
    public ParseTJson(Interface in, Object a0) {
        this.Interface = in;
        if(a0 instanceof JSONArray)      Interface.jsonArray((JSONArray) a0);
        else if(a0 instanceof JSONObject)Interface.jsonObject((JSONObject) a0);
        else if(a0 instanceof JSONValue) Interface.jsonValue((JSONValue) a0);
        else if(a0 instanceof JSONParser)Interface.jsonParser((JSONParser) a0);
        else if(a0 instanceof JSONAware) Interface.jsonAware((JSONAware) a0);
        else Interface.jsonNull(a0);
    }
    
    public static Type isType(Object obj){
        if(obj instanceof JSONArray) return Type.ARRAY;
        if(obj instanceof JSONObject)return Type.OBJECT;
        if(obj instanceof JSONAware) return Type.AWARE;
        if(obj instanceof JSONParser)return Type.PARSER;
        if(obj instanceof JSONValue) return Type.VALUE;
        return Type.STRING;
    }
    
    public static JSONArray toArray(Object obj){
        return (obj instanceof JSONArray) ? (JSONArray)obj : null;
    }
    
    public static JSONObject toObject(Object obj){
        return (obj instanceof JSONObject)?(JSONObject)obj:null;
    }    
    
    public static JSONValue toValue(Object obj){
        return (obj instanceof JSONValue)?(JSONValue)obj:null;
    }
    
    public static JSONParser toParser(Object obj){
        return (obj instanceof JSONParser)?(JSONParser)obj:null;
    }
    
    public static JSONAware toAware(Object obj){
        return (obj instanceof JSONAware)?(JSONAware)obj:null;
    }
    
    public static Object toType(Object o, Type t){
        if(t==Type.ARRAY){return toArray(o);}
        if(t==Type.AWARE){return toAware(o);}
        if(t==Type.OBJECT){return toObject(o);}
        if(t==Type.PARSER){return toParser(o);}
        if(t==Type.VALUE){return toValue(o);}
        if(t==Type.STRING){return o.toString();}
        
        return null;
    }
    
    
    interface Interface{
    
        public void jsonArray(JSONArray array);

        public void jsonObject(JSONObject object);

        public void jsonValue(JSONValue value);

        public void jsonParser(JSONParser parser);

        public void jsonAware(JSONAware aware);

        public void jsonNull(Object obj);
    
    }
    
    
}
