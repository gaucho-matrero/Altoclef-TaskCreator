package james.altoclef.taskcreator;

import james.altoclef.taskcreator.graphics.AltoFrame;
import james.altoclef.taskcreator.interfaces.ICustomTask;
import james.altoclef.taskcreator.interfaces.Key;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;

public class Driver {
    public static void main(String[] arg){
        //This is the way to do stuff
/*        JSONManager manager = new JSONManager("CustomTask");
        JSONArray customTasks = new JSONArray();
        try{
            manager.add("prefix","custom");
            customTasks.put(newCustomTask("gearup","gets 64 diamonds",new ICustomTask[]{
                    new customSubTask("get",new Object[]{"diamond",64})
                    }));
            customTasks.put(newCustomTask("test2","gets a diamond pickaxe, them goes to 0 0 0 nether",new ICustomTask[]{
                    new customSubTask("get",new Object[]{"diamond_pickaxe",1}),
                    new customSubTask("goto",new Object[]{0,0,0,"nether"})
            }));
            manager.add("custom-tasks",customTasks);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        manager.write();*/

        //actual driver code
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //Windows Look and feel
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        AltoFrame mainFrame = new AltoFrame();
    }

    public static JSONObject newCustomTask(String name, String desc, ICustomTask[] ctasks) throws JSONException {
        writeableTask custom_task = new writeableTask();
        custom_task.add(new Key() {
            @Override
            public String getKey() {
                return "name";
            }

            @Override
            public Object getValue() {
                return name;
            }
        });
        custom_task.add(new Key() {
            @Override
            public String getKey() {
                return "description";
            }

            @Override
            public Object getValue() {
                return desc;
            }
        });
        JSONArray subTasks = new JSONArray();
        for (ICustomTask st:ctasks) {
            subTasks.put(st.build());
        }
        custom_task.add(new Key() {
            @Override
            public String getKey() {
                return "tasks";
            }

            @Override
            public Object getValue() {
                return subTasks;
            }
        });
        return custom_task.writeObject();
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
