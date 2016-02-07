/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aadi
 */
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class jsonOperations {
    static String JSONEncode(String [] arrValue, String[] arrIdx){
        JSONObject obj = new JSONObject();
        int length = arrValue.length;
        for (int i = 0; i < length; i++) {
            obj.put(arrIdx[i], arrValue[i]);
        }
        return obj.toJSONString();
    }
    static String[] JSONDecode(String inp) throws ParseException{
        JSONParser parser = new JSONParser();
        
        Object obj = parser.parse(inp);
        JSONArray array = (JSONArray)obj;
        int size = array.size();
        String[] out = new String[size];
        for (int i = 0; i < size; i++) {
            out[i] = "" + array.get(i);
        }
        return out;
    }
    public static void main(String[] args) {
        System.out.println("Testing encoding");
        String[] arrValue = {"Aditya","05-04-1995"};
        String[] arrIndex = {"name", "dob"};
        String out = JSONEncode(arrValue,arrIndex);
        System.out.println(out);
        System.out.println("Testing Decoding");
        out = "[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
        System.out.println("Input String" + out);
        try {
            System.out.println("Output");
            String[] outA = JSONDecode(out);
            for(String x : outA){
                System.out.println(x);
            }
        } catch (ParseException ex) {
            Logger.getLogger(jsonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
