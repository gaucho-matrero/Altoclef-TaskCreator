package james.altoclef.taskcreator.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

class JSONManagerTest {

    @AfterAll
    static void tearDown(){
        File file = new File("test-write.json");
        file.delete();
    }

    @Test
    void write() throws JSONException {
        JSONManager jm = new JSONManager("test-write");
        try {
            jm.add("test","write");
            jm.write(); //if no exception occurs, this test was successful
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    void add() throws JSONException {
        JSONManager jm = new JSONManager("test-add");
        JSONObject obj = new JSONObject();
        obj.put("test","value");
        jm.add("test","value");
        assertEquals(obj.toString(),jm.toString());
        assertThrows(JSONException.class,()->{
            jm.add(null,null);
        });
    }

    @Test
    void getFileName() {
        JSONManager jm = new JSONManager("test-get-file-name");
        assertEquals("test-get-file-name.json",jm.getFileName());
    }

}