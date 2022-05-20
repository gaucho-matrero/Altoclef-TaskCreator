package james.altoclef.taskcreator;

import james.altoclef.taskcreator.utils.IWritableTask;
import james.altoclef.taskcreator.utils.Key;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class writeableTask implements IWritableTask {
    private final List<Key> writeableItems;

    public writeableTask(){
        writeableItems = new ArrayList<>();
    }

    public void add(Key k){
        writeableItems.add(k);
    }

    public writeableTask(List<Key> writeableItems){
        this.writeableItems = writeableItems;
    }
    @Override
    public JSONObject writeObject() {
        JSONObject obj = new JSONObject();
        for(Key k : writeableItems){
            try{
                obj.put(k.getKey(),k.getValue());
            }catch (JSONException ignored){
                return null;
            }
        }
        purge();
        return obj;
    }

    private void purge(){
        writeableItems.clear();
    }
}
