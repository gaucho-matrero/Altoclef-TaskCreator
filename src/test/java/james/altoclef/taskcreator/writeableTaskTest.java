package james.altoclef.taskcreator;

import james.altoclef.taskcreator.interfaces.IWritableTask;
import james.altoclef.taskcreator.interfaces.Key;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class writeableTaskTest {

    @Test
    void add() {
        writeableTask t = new writeableTask();
        t.add(new Key() {
            @Override
            public String getKey() {
                return "test-add";
            }

            @Override
            public Object getValue() {
                return "do nothing";
            }
        });
        writeableTask t2 = new writeableTask(new ArrayList<Key>()); //ensure both constructors work
    } //passes if no exceptions are thrown

    @Test
    void writeObject() {
        class stub_writeableObject implements IWritableTask {
            @Override
            public JSONObject writeObject() { //stub of the interface
                JSONObject temp = new JSONObject();
                try{temp.put("test-normal","value");}
                catch (JSONException e){
                    e.printStackTrace();
                }
                return temp;
            }
            public JSONObject writeObject2() { //stub of the interface
                JSONObject temp = new JSONObject();
                try{temp.put("test-array",new JSONArray(new String[]{"test1","test2"}));}
                catch (JSONException e){
                    e.printStackTrace();
                }
                return temp;
            }
        }
        stub_writeableObject stub = new stub_writeableObject();
        writeableTask t = new writeableTask();
        t.add(new Key() {
            @Override
            public String getKey() {
                return "test-normal";
            }

            @Override
            public Object getValue() {
                return "value";
            }
        });
        assertEquals(stub.writeObject().toString(),t.writeObject().toString());
        t.add(new Key() {
            @Override
            public String getKey() {
                return "test-array";
            }

            @Override
            public Object getValue() {
                return new Object[]{"test1","test2"};
            }
        });
        assertEquals(stub.writeObject2().toString(),t.writeObject().toString());
        t.add(new Key() {
            @Override
            public String getKey() {
                return null;
            }

            @Override
            public Object getValue() {
                return null;
            }
        });

        assertNull(t.writeObject());
    } //passes if it can write without exceptions and if it can nest arrays
}