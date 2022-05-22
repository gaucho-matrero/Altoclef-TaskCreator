package james.altoclef.taskcreator.utils;



import java.io.*;

import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONManager {
    private final String filename;
    JSONObject file;

    /**
     * Creates a new JSON manager who will be able to write a JSON file with "filename"/
     * @param filename the name of the file w/o the JSON extension.
     */
    public JSONManager(String filename) throws IOException, ParseException {
        this.filename = filename;
        FileReader fr = new FileReader(filename);
        JSONParser pr = new JSONParser();
        file = new JSONObject(pr.parse(fr).toString());
    }
    public JSONManager(){
        this.filename = "CustomTasks.json";
        file = new JSONObject();
    }

    public String[] getTaskNames() throws JSONException{
        try {
            JSONArray tasks = file.getJSONArray("custom-tasks");
            String[] res = new String[tasks.length()];
            for (int i = 0; i < res.length; i++) {
                JSONObject obj = tasks.getJSONObject(i);
                res[i] = obj.getString("name");
            }
            return res;
        }catch (JSONException e){
            throw e;
        }
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

    @Override
    public String toString(){
        return file.toString();
    }

    public JSONObject getFile() {
        return this.file;
    }
}
