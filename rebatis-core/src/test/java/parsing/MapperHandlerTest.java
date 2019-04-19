package parsing;

import org.gosky.mapping.MapperHandler;
import org.gosky.mapping.MethodMapper;
import org.junit.Test;

import java.util.List;

public class MapperHandlerTest {


    @Test
    public void test() {
        new MapperHandler().parsingInterface("org.gosky.mapping");
        List<MethodMapper> methodMapperList = MapperHandler.methodMapperList;
        MethodMapper methodMapper = methodMapperList.get(0);
        String sql = methodMapper.getSql();
        System.out.println(sql);
        System.out.println(methodMapperList);
    }
}
