package james.altoclef.taskcreator;

import james.altoclef.taskcreator.graphics.AltoFrame;
import james.altoclef.taskcreator.interfaces.ICustomTask;
import james.altoclef.taskcreator.interfaces.Key;
import james.altoclef.taskcreator.utils.JSONManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Driver {
    public static void main(String[] arg) throws IOException, ParseException {
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
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        AltoFrame mainFrame = new AltoFrame("CustomTasks.json");
    }
}