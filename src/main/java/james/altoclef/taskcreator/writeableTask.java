package james.altoclef.taskcreator;

import james.altoclef.taskcreator.interfaces.IWritableTask;
import james.altoclef.taskcreator.interfaces.Key;
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
            obj.put(k.getKey(),k.getValue());
        }
        purge();
        return obj;
    }

    private void purge(){
        writeableItems.clear();
    }
}
