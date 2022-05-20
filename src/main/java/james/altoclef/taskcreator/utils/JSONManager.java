package james.altoclef.taskcreator.utils;



import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONManager {
    private final String filename;
    JSONObject file;

    /**
     * Creates a new JSON manager who will be able to write a JSON file with "filename"/
     * @param filename the name of the file w/o the JSON extension.
     */
    public JSONManager(String filename){
        file = new JSONObject();
        this.filename = filename+".json";
    }

    public void write(){
        try {
            FileWriter filew = new FileWriter(filename);
            file.write(filew);
            filew.close();
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void add(String key, Object value) throws JSONException {
        file.put(key,value);
    }

    //public interface
    public String getFileName(){
        return filename;
    }

}
