package BKE.Helper;

import java.util.*;

public class ServerDataDecoder {

    public static List<String> DecodeArray(String[] segments){
        // reconstruct string
        String data = String.join(" ", segments);
        return DecodeArray(data);
    }

    public static List<String> DecodeArray(String data){
        ArrayList<String> list = new ArrayList<>();

        int pos1 = data.indexOf("[");
        int pos2 = data.indexOf("]");
        if (pos1 > -1 && pos2 > -1){
            String stripped = data.substring(pos1 + 1, pos2);
            String[] split = stripped.split(",");
            for (int i = 0; i < split.length; i++) {
                String item = split[i];
                list.add(item.replace("\"", "").trim());
            }
        }
        return list;
    }

    public static Map<String, String> DecodeMap(String[] segments){
        // reconstruct string
        String data = String.join(" ", segments);
        return DecodeMap(data);
    }

    public static Map<String, String> DecodeMap(String data){
        int pos1 = data.indexOf("{");
        int pos2 = data.indexOf("}");
        if (pos1 > -1 && pos2 > -1){
            Map<String, String> map = new HashMap<String, String>();
            String stripped = data.substring(pos1 + 1, pos2);
            String[] split = stripped.split(",");
            for (String kvpString: split) {
                String[] kvp = kvpString.trim().split(":");
                map.put(kvp[0].trim(), kvp[1].replace("\"", "").trim());
            }
            return map;
        }
        return new HashMap<>();
    }
}
