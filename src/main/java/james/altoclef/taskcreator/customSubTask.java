package james.altoclef.taskcreator;

import james.altoclef.taskcreator.interfaces.ICustomTask;
import james.altoclef.taskcreator.interfaces.Key;
import org.json.JSONObject;

public class customSubTask implements ICustomTask {
    private final String type;
    private final Object[] parameters;

    public customSubTask(String type, Object[] parameters){
        this.type=type;
        this.parameters=parameters;
    }

    public String getType(){
        return type;
    }

    public Object[] getParameters(){
        return parameters;
    }

    @Override
    public JSONObject build() {
        writeableTask ret = new writeableTask();
        ret.add(new Key() {
            @Override
            public String getKey() {
                return "command";
            }

            @Override
            public Object getValue() {
                return type;
            }
        });
        ret.add(new Key() {
            @Override
            public String getKey() {
                return "parameters";
            }

            @Override
            public Object getValue() {
                return parameters;
            }
        });
        return ret.writeObject();
    }
}
