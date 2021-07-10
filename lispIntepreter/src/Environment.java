import java.util.HashMap;
import java.util.Map;

/**
 * 环境存储的变量分两类 值(数字、字符串、函数) 与 函数模板
 *
 * 存储值由 (define key val)实现，查询时直接返回值
 * 存储函数模板由 (define (f param...) (val) ) 实现，查询时返回模板的克隆
 */
public class Environment {
    Map<String, Object> localDict = new HashMap<>();
    Environment fatherEnv;
    int layer = 0;
    public Environment() {
    }

    public Environment(Environment fatherEnv) {
        this.fatherEnv = fatherEnv;
        this.layer = fatherEnv.layer+1;
    }

    public Environment(Map<String, Object> map,Environment fatherEnv) {
        localDict.putAll(map);
        this.fatherEnv = fatherEnv;
        this.layer = fatherEnv.layer+1;
    }

    public Object search(String key) throws InterpreteError {
        if (localDict.containsKey(key)) {
            return localDict.get(key);
        } else {
            if (null == fatherEnv) {
                throw new InterpreteError();
            } else {
                return fatherEnv.search(key);
            }
        }
    }

    public void put(String key, Object val) throws Exception {
        while (val instanceof TreeNode && !((TreeNode) val).isSetq()){
            val = ((TreeNode)val).execute(this);
        }
        localDict.put(key, val);
    }

    public void set(String key, Object val) throws Exception {
        Environment env = this;
        while (!env.localDict.containsKey(key)){
            env = env.fatherEnv;
            if(env == null){
                break;
            }
        }
        if(null == env){
            throw  new InterpreteError();
        }
        env.localDict.put(key, val);
    }

    @Override
    public String toString() {
        return "env-layer: "+layer;
    }
}
