package james.altoclef.taskcreator.utils;



import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.*;
import java.util.*;

import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONManager {
    private final String filename;
    JSONObject file; //TODO Never allow a task with the same name to be made

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
    public JSONManager(CompressedString contents) throws ParseException {
        this.filename = "";
        JSONParser pr = new JSONParser();
        file = new JSONObject(pr.parse(contents.toString()).toString());
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

    public void setFile(JSONObject file) {
        this.file=file;
    }



    public String compressToString() throws UnsupportedEncodingException {
        String jsonString = file.toString();
        byte[] jsonbytes = jsonString.getBytes(StandardCharsets.UTF_8);
        Deflater compressor = new Deflater();
        compressor.setInput(jsonbytes);
        byte[] compressed = new byte[jsonbytes.length]; //should ALWAYS be big enough to contain compressed data
        compressor.finish();
        int resultLength = compressor.deflate(compressed);
        String result = "UNABLE TO CONVERT";
        result = new String(Base64.getEncoder().encode(compressed));
        return result;
    }

    public JSONObject decompressToJson(String decom) throws UnsupportedEncodingException, DataFormatException {
        byte[] jsonbytes = Base64.getDecoder().decode(decom);
        Inflater decompressor = new Inflater();
        decompressor.setInput(jsonbytes);
        byte[] decompressed = new byte[2048]; //should ALWAYS be big enough to contain compressed data
        int resultLength = decompressor.inflate(decompressed);
        decompressor.end();
        JSONObject unableToConvert = new JSONObject();
        try {
            String result = new String(decompressed, 0, resultLength, StandardCharsets.UTF_8);
            this.file = new JSONObject(result);
        }catch (Exception ignored){
            this.file = unableToConvert;
        }
        return file;
    }
}
