package james.altoclef.taskcreator.utils;

/**
 * This is just a wrapper for a String as JSON Manager needs to have access to it.
 */
public class CompressedString {
    private final String contents;
    public CompressedString(String s){
        this.contents = s;
    }
    public String toString(){
        return contents;
    }
}
