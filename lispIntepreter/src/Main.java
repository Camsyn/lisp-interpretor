
import java.io.*;
import java.util.*;


/**
 * 目前不支持脚本化运行，仅仅支持命令行
 *
 * @author 86171
 */
public class Main {
    Environment highestEnv = new Environment();
    List<TreeNode> roots;

    Deque<Integer> syntaxStack = new LinkedList<>();

    public Main() {
        try {
            highestEnv.put("*", DefaultFun.Multiple);
            highestEnv.put("/", DefaultFun.DIVIDE);
            highestEnv.put("-", DefaultFun.SUBTRACTION);
            highestEnv.put("+", DefaultFun.PLUS);
            highestEnv.put("**", DefaultFun.POW);
            highestEnv.put("pow", DefaultFun.POW);
            highestEnv.put("|", DefaultFun.OR);
            highestEnv.put("&", DefaultFun.AND);
            highestEnv.put("~", DefaultFun.NOT);
            highestEnv.put("^", DefaultFun.XOR);
            highestEnv.put("||", DefaultFun.LOGIC_OR);
            highestEnv.put("or", DefaultFun.LOGIC_OR);
            highestEnv.put("&&", DefaultFun.LOGIC_AND);
            highestEnv.put("and", DefaultFun.LOGIC_AND);
            highestEnv.put("!", DefaultFun.LOGIC_NOT);
            highestEnv.put("not", DefaultFun.LOGIC_NOT);
            highestEnv.put("eq", DefaultFun.EQUAL);
            highestEnv.put("eq?", DefaultFun.EQUAL);
            highestEnv.put("==", DefaultFun.EQUAL);
            highestEnv.put("!=", DefaultFun.N_EQUAL);
            highestEnv.put("neq", DefaultFun.N_EQUAL);
            highestEnv.put("=", DefaultFun.EQUAL);
            highestEnv.put(">=", DefaultFun.GET);
            highestEnv.put(">", DefaultFun.GT);
            highestEnv.put("<=", DefaultFun.LET);
            highestEnv.put("<", DefaultFun.LT);
            highestEnv.put("<<", DefaultFun.LSHIFT);
            highestEnv.put(">>", DefaultFun.RSHIFT);
            highestEnv.put(">>>", DefaultFun.ABS_RSHIFT);


            highestEnv.put("exit", DefaultFun.EXIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean syntaxCheck(String lispCode) throws Exception {
        int len = lispCode.length();
        boolean isString = false;
        for (int i = 0; i < len; i++) {
            if (lispCode.charAt(i) == '"') {
                isString = !isString;
            } else if (!isString) {
                if (lispCode.charAt(i) == '(') {
                    syntaxStack.push(1);
                } else if (lispCode.charAt(i) == ')') {
                    syntaxStack.pop();
                }
            }
        }
        return syntaxStack.isEmpty();
    }

/*    public void define(TreeNode def) throws Exception {
        if (def.sonSize() < 3) {
            throw new InterpreteError();
        }
        if (def.getSon(1).isKey()) {
            if (def.sonSize() != 3) {
                throw new InterpreteError();
            }
            highestEnv.put(def.getSon(1).getVal(), def.getSon(2));
        } else {
            Fun fun = new Fun(def, new Environment(highestEnv));
            highestEnv.put(Fun.getDefName(def), fun);
        }
    }*/

    public void runAsShell() {
        run(System.in);
    }

    public void run(InputStream input) {
        run(input, System.out);
    }

    public void run(InputStream input, OutputStream output) {
        StringBuilder sb = new StringBuilder();
        List<Object> res = null;
        String codeFragment = "";
        try (Scanner in = new Scanner(input);
             PrintStream out = new PrintStream(output)) {
            end:
            while (in.hasNextLine()) {
                try {
                    do {
                        codeFragment = in.nextLine();
                        sb.append(codeFragment).append(" ");
                    } while (!syntaxCheck(codeFragment) && in.hasNextLine());
                    res = execute(sb.toString());
                    for (Object tmp : res) {
                        if (DefaultFun.EXIT.equals(tmp)) {
                            break end;
                        } else {
                            out.println(tmp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    syntaxStack = new LinkedList<>();
                    sb.delete(0, sb.length());
                }
            }
        }
    }

    public void runAsScript(String filePath) throws FileNotFoundException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            run(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runAsScript(String inputFile, String outputFile) throws FileNotFoundException {
        if (inputFile.equals(".")) {
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                run(System.in, fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileInputStream fis = new FileInputStream(inputFile);
                 FileOutputStream fos = new FileOutputStream(outputFile)) {
                run(fis, fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Object> execute(String lispCode) throws Exception {
        roots = TreeUtils.generateTrees(lispCode);
        List<Object> res = new LinkedList<>();
        for (TreeNode root : roots) {
            Object r = root.execute(highestEnv);
            if (null != r) {
                res.add(r);
            }
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        if (args.length == 0) {
            main.runAsShell();
        } else if (args.length == 1) {
            main.runAsScript(args[0]);
        } else {
            main.runAsScript(args[0], args[1]);
        }

       /* Scanner in = new Scanner(new FileInputStream(args[0]));
        System.out.println(in.nextLine());*/
    }
}
