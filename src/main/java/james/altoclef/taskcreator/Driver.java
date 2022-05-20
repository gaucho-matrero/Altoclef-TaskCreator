package james.altoclef.taskcreator;

import james.altoclef.taskcreator.utils.JSONManager;
import james.altoclef.taskcreator.utils.Key;
import james.altoclef.taskcreator.utils.emptyClass;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;

public class Driver {
    public static void main(String[] arg){
        System.out.println("Hello World");
        JSONManager manager = new JSONManager("CustomTask");
        try{
            JSONArray customTasks = new JSONArray();
            manager.add("prefix","custom");
            customTasks.put(writeCustomTask("gearup","gets 64 diamonds"));
            manager.add("custom-tasks",customTasks);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        manager.write();
    }

    public static JSONObject writeCustomTask(String name,String desc) throws JSONException {
        //TODO port this method to the gui.
        JSONObject customTask = new JSONObject(); //includes name, desription, and task array
        JSONArray taskArray = new JSONArray(); // task array to add to customTask
        writeableTask gearup = new writeableTask();
        customTask.put("name",name);
        customTask.put("description",desc);
        addSubTask(gearup,"command",new Object[]{"diamond",64});
        taskArray.put(gearup.writeObject());
        addSubTask(gearup,"goto",new Object[]{0,0,0,"overworld"});
        taskArray.put(gearup.writeObject());
        customTask.put("tasks",taskArray);
        return customTask;
    }

    private static void addSubTask(writeableTask gearup, String command, Object[] objects) {
        gearup.add(new Key(){

            @Override
            public String getKey() {
                return "command";
            }

            @Override
            public Object getValue() {
                return command;
            }
        });
        gearup.add(new Key() {
            @Override
            public String getKey() {
                return "parameters";
            }

            @Override
            public Object getValue() {
                return objects;
            }
        });
    }
}
