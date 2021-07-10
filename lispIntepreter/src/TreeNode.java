import org.w3c.dom.Node;

import javax.crypto.spec.PSource;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class TreeNode {
    private String val;
    private Deque<TreeNode> sons = new LinkedList<>();


    public TreeNode(String val) {
        this.val = val;
    }

    public TreeNode() {
    }

    public TreeNode(String val, List<TreeNode> sons) {
        this.val = val;
        this.sons.addAll(sons);
    }

    public boolean judgeType(String type) {
        if (isValue()) {
            return false;
        }
        return type.equals(getSon(0).val);
    }

    public boolean isDefine() {
        return judgeType("define");
    }

    public boolean isLambda() {
        return judgeType("lambda");
    }

    public boolean isIf() {
        return judgeType("if");
    }

    public boolean isCond() {
        return judgeType("cond");
    }

    public boolean isLet() {
        return judgeType("let");
    }

    public boolean isSetq() {
        return judgeType("set!");
    }

    public Object executeIf(Environment env) throws Exception {
        if (this.sonSize() != 4) {
            throw new InterpreteError();
        }
        List<TreeNode> sons = this.getSons();
        if (sons.get(1).execute(env).equals(1)) {
            return sons.get(2).execute(env);
        } else {
            return sons.get(3).execute(env);
        }
    }

    public Object executeDef(Environment env) throws Exception {
        if (this.sonSize() < 3) {
            throw new InterpreteError();
        }
        if (this.getSon(1).isKey()) {
            if (this.sonSize() != 3) {
                throw new InterpreteError();
            }
            Object res = this.getSon(2).execute(env);
            while (res instanceof TreeNode){
                res = ((TreeNode) res).execute(env);
            }
            env.put(this.getSon(1).getVal(), res);
            return res;
        } else {
            String funName = Fun.getDefName(this);
            Fun funTemplate =  new FunTemplate(Fun.getDefParams(this), this, new Environment(env));
            env.put(funName,funTemplate);
            return TreeUtils.generateNode(funName).execute(env);
        }
    }

    public Object executeCond(Environment env) throws Exception {
        if (this.sonSize() == 1) {
            throw new InterpreteError();
        }
        env.put("else", 1);
        List<TreeNode> sons = this.getSons();
        for (int i = 1; i < sons.size(); i++) {
            TreeNode son = sons.get(i);
            if (son.sonSize() > 2) {
                throw new InterpreteError();
            }
            if (son.sonSize() == 2) {
                if (son.getSon(0).execute(env).equals(1)) {
                    return son.getSon(1).execute(env);
                }
            } else {
                return son.execute(env);
            }
        }
        return null;
    }

    //关于(set! )的语法和返回值
    public Object executeSetq(Environment env) throws Exception {
        if (this.sonSize() != 3) {
            throw new InterpreteError();
        }
        if (!this.getSon(1).isKey()) {
            throw new InterpreteError();
        }
        Object val = this.getSon(2).execute(env);
        String key = this.getSon(1).getVal();
        env.set(key, val);
        return val;
    }

    private Environment getSonEnvForLet(Environment env) throws Exception {
        List<TreeNode> nodes = getSons();
        Environment sonEnv = new Environment(env);
        for (TreeNode var : nodes) {
            TreeNode key = var.getSon(0);
            if (!key.isKey() || var.sonSize() != 2) {
                throw new InterpreteError();
            }
            sonEnv.put(key.getVal(), var.getSon(1).execute(sonEnv));
        }
        return sonEnv;
    }

    public Object executeLet(Environment env) throws Exception {
        if (this.sonSize() != 3) {
            throw new InterpreteError();
        }
        TreeNode res = this.getSon(2);
        Environment sonEnv = this.getSon(1).getSonEnvForLet(env);
        return res.execute(sonEnv);
    }

    public int sonSize() {
        return sons.size();
    }

    public String getOperator() {
        return ((LinkedList<TreeNode>) sons).get(0).getVal();
    }

    public String getVal() {
        return val;
    }

    public TreeNode getSon(int index) {
        return ((LinkedList<TreeNode>) sons).get(index);
    }

    public void setVal(String val) {
        this.val = val;
    }

    public List<TreeNode> getSons() {
        return (List<TreeNode>) sons;
    }

    public void setSons(List<TreeNode> sons) {
        this.sons.addAll(sons);
    }

    public void addSon(TreeNode son) {
        this.sons.add(son);
    }

    public void setFather(TreeNode father) {
        father.addSon(this);
    }

    public void grabFather(TreeNode father) {
        father.sons.addFirst(this);
    }

    public Object execute(Environment env) throws Exception {
        if (isNum()) {
            return TransformUtils.toNum(val);
        } else if(isString()){
            return TransformUtils.getString(val);
        } else if (isValue()){
            String val = this.getVal();
            if(" ".equals(val)){
                return null;
            }
            Object res = env.search(val);
            if (res instanceof TreeNode) {
                res = ((TreeNode) res).execute(env);
            }
            if (res instanceof FunTemplate) {
                //不应该直接传送函数对象，而应该传送其副本；（Class与Object）
                res = ((FunTemplate) res).getFunObject();
            }
            return res;
        }else {
            int sonSize = this.sonSize();
            if (sonSize == 1) {
                Object res=this.getSon(0).execute(env);
                if (res instanceof Fun) {
                    //不应该直接传送函数对象，而应该传送其副本；（Class与Object）
                    if(((Fun)res).getParamsSize()==0){
                        return ((Fun) res).action(List.of());
                    }
                }
                return res;
            } else if (this.isLambda()) {
                return Fun.createFunByLambda(this, env);
            } else if (this.isIf()) {
                return this.executeIf(env);
            } else if (this.isCond()) {
                return this.executeCond(env);
            } else if (this.isSetq()) {
                return this.executeSetq(env);
            } else if (this.isLet()) {
                return this.executeLet(env);
            } else if (this.isDefine()){
                return this.executeDef(env);
            }else {
                boolean hasFun = false;
                Object object;
                Operation oper =null;
                int arguIndex=0;
                List<TreeNode> children = this.getSons();
                List<Object> valueList = new LinkedList<>();
                for (int i = 0; i < sonSize; i++) {
                    object = children.get(i).execute(env);
                    valueList.add(object);
                    if(object instanceof Operation){
                        hasFun = true;
                        oper = (Operation) object;
                        arguIndex = i+1;
                    }
                }
                if(hasFun){
                    if (arguIndex==valueList.size()&& arguIndex!=0) {
                        return oper;
                    }
                    return oper.action(valueList.subList(arguIndex,valueList.size()));
                }else {
                    return valueList.get(valueList.size()-1);
                }
            }
        }
    }

    public boolean isValue() {
        return sonSize() == 0;
    }

    public boolean isKey() {
        return isValue() && !TransformUtils.isNum(val);
    }

    public boolean isNum(){
        return TransformUtils.isNum(val);
    }

    public boolean isString(){
        return TransformUtils.isString(val);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (" ".equals(val)) {
            sb.append("( ");
            sons.forEach(sb::append);
            sb.append(") ");
        } else {
            sb.append(val).append(" ");
        }
        return sb.toString();
    }


}
