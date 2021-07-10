
import java.util.*;

public class TreeUtils {
    private static Deque<TreeNode> stack;
    private static StringTokenizer in;
    private static CodeReader input;

    public static Object execute(TreeNode root, Environment env) throws Exception {
        if (null == root) {
            return root;
        } else if (root.sonSize() == 0) {
            return env.search(root.getVal());
        } else if (root.sonSize() == 1) {
            return env.search(root.getSons().get(0).getVal());
        } else {
            Operation oper = (Operation) env.search(root.getSons().get(0).getVal());
            List<TreeNode> sons = root.getSons();
            List<Object> argus = new LinkedList<>();
            for (int i = 0; i < root.sonSize() - 1; i++) {
                argus.add(execute(sons.get(i + 1), new Environment(env)));
            }
            return oper.action(argus);
        }
    }

    public static TreeNode generateNode(String s) {
        return new TreeNode(s);
    }


    public static TreeNode getParentNode() {
        return new TreeNode(" ");
    }


    public static TreeNode generateTree(String lispCode) {

        input = new CodeReader(lispCode);
        return generateTree(input);
    }

    public static TreeNode generateTree(CodeReader input) {
        stack = new LinkedList<>();
        TreeNode root = generateNode(input.next());
        stack.push(root);
        while (input.hasNext()) {
            String val = input.next();
            if (")".equals(val)) {
                root = getParentNode();
                TreeNode tmp = stack.pop();
                while (!"(".equals(tmp.getVal())) {
                    tmp.grabFather(root);
                    tmp = stack.pop();
                }
                stack.push(root);
            } else {
                stack.push(generateNode(val));
            }
        }
        return root;
    }

    public static List<TreeNode> generateTrees(String lispCode) {
        List<TreeNode> trees = new LinkedList<>();

        input = new CodeReader(lispCode);
        while (input.hasNext()) {
            trees.add(generateTree(input));
        }
        return trees;
    }

    public static TreeNode generateTreeByRecurse(String lispCode) {
        return recurseSol(new CodeReader(lispCode));
    }

    private static TreeNode recurseSol(CodeReader in) {
        String val = in.next();
        if (!"()".contains(val)) {
            return generateNode(val);
        } else if (val.equals("(")) {
            TreeNode parent = TreeUtils.getParentNode();
            TreeNode son = recurseSol(in);
            while (null!=son){
                parent.addSon(son);
                son = recurseSol(in);
            }
            return parent;
        }else {
            return null;
        }

    }

    public static void main(String[] args) {
        TreeNode root = generateTreeByRecurse("(define (recurseF n)\n" +
                "    (if (< n 3) \n" +
                "        n \n" +
                "        (+ (recurseF (- n 1)) (* 2 (recurseF (- n 2))) (* 3 (recurseF (- n 3))))))");
        System.out.println(root);
    }
}
