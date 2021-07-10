import java.util.List;

public class FunTemplate extends Fun{
    public FunTemplate(TreeNode def, Environment env) throws InterpreteError {
        super(def, env);
    }

    public FunTemplate(List<String> params, TreeNode def, Environment env) throws InterpreteError {
        super(params, def, env);
    }
    public Fun getFunObject() throws CloneNotSupportedException {
        return (Fun) super.clone();
    }
}
