import java.util.LinkedList;
import java.util.List;

/**
 * 缺少对递归的优化！！
 */
public class Fun implements Operation, Cloneable {
    List<String> params;
    List<TreeNode> innerDef = new LinkedList<>();
    TreeNode body;
    Environment env;

    public Fun(TreeNode def, Environment env) throws InterpreteError {
        this.params = getDefParams(def);
        int size = def.sonSize();
        if (def.sonSize() < 3) {
            throw new InterpreteError();
        }
        for (int i = 2; i < size - 1; i++) {
            innerDef.add(def.getSon(i));
        }
        this.body = def.getSon(size - 1);
        this.env = env;
    }

    public Fun(List<String> params, TreeNode def, Environment env) throws InterpreteError {
        this.params = params;
        int size = def.sonSize();
        if (def.sonSize() < 3) {
            throw new InterpreteError();
        }
        for (int i = 2; i < size - 1; i++) {
            innerDef.add(def.getSon(i));
        }
        this.body = def.getSon(size - 1);
        this.env = env;
    }

    // lambda表达式可以执行，不过返回的是一个函数
    public static Fun createFunByLambda(TreeNode lambda, Environment env) throws InterpreteError {
        if (lambda.sonSize() != 3) {
            throw new InterpreteError();
        }
        List<String> params = new LinkedList<>();
        List<TreeNode> sons = lambda.getSons();
        if (sons.get(1).isValue()) {
            String param = sons.get(1).getVal();
            if(!" ".equals(param)){
                params.add(param);
            }
        } else {
            for (TreeNode param : sons.get(1).getSons()) {
                params.add(param.getVal());
            }
        }
        return new Fun(params, lambda, new Environment(env));
    }

    //内部的函数必须运行时定义！！！
    //否则将是类函数，而非对象函数
    public void innerDefine() throws Exception {
        for (TreeNode def : innerDef) {
            def.execute(env);
        }
    }
    public int getParamsSize(){
        return params.size();
    }
    public static String getDefName(TreeNode def) {
        return def.getSon(1).getOperator();
    }

    public static List<String> getDefParams(TreeNode def) throws InterpreteError {
        List<String> params = new LinkedList<>();
        List<TreeNode> sons = def.getSon(1).getSons();
        int size = sons.size();
        for (int i = 1; i < size; i++) {
            TreeNode param = sons.get(i);
            if (!param.isKey()) {
                throw new InterpreteError();
            }
            params.add(param.getVal());
        }
        return params;
    }

    @Override
    public Object action(List<Object> argus) throws Exception {
        // 必须要在函数作用时再绑定内部定义的函数，否则内部定义的函数将成为‘类函数’（父环境为函数模板的环境），
        // 无法访问函数对象的实参
        if (params.size() != argus.size()) {
            throw new InterpreteError();
        }
        for (int i = 0; i < params.size(); i++) {
            env.put(params.get(i), argus.get(i));
        }
        // 内部define的生成要迟于实参的绑定
        innerDefine();
        return body.execute(env);
    }

    /**
     * 必须要有克隆，否则无法实现函数对象的隔离
     * （每个相同函数的对象都使用同一模板克隆，然后在后续传入不同实参实现本地环境的差异）
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Fun clone = (Fun) super.clone();
        clone.env = new Environment(clone.env.localDict,clone.env.fatherEnv);
        return clone;
    }

    @Override
    public String toString() {
        return "Fun{" +
                "params=" + params +
                ", body=" + body +
                ", env=" + env +
                '}';
    }
}
