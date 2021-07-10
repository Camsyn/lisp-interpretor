import java.util.List;

public interface Operation {
    Object action(List<Object> params) throws Exception;
}
