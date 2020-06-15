package org.gosky.rebatis.sample.mapper;


import io.vertx.core.Future;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.apache.commons.lang3.StringUtils;
import org.gosky.rebatis.sample.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBaseMapper extends org.gosky.common.BaseMapper {

    private Logger logger = LoggerFactory.getLogger(org.gosky.common.BaseMapper.class);

    public TestBaseMapper(org.gosky.Rebatis rebatis) {
        super(rebatis);
    }

    public org.gosky.adapter.Call<User> selectByPrimaryKey(Long id) {
        java.util.Map<String, Object> parameters = new java.util.HashMap<>();
        parameters.put("id", id);
        String template = "select * from user where id = #{id}";
        long start = System.currentTimeMillis();
        Future<User> execute = SqlTemplate
                .forQuery(client, template)
                .mapTo(User.class)
                .execute(parameters)
                .map(users -> {
                    io.vertx.sqlclient.RowIterator<User> iterator = users.iterator();
                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        return null;
                    }
                })
                .onComplete(event -> logger.info("run sql={}, params={}, duration={}, result={}", template, parameters, System.currentTimeMillis() - start, event.result()));

        return new org.gosky.adapter.DefaultCall(execute);
    }

//    @org.apache.ibatis.annotations.Insert("insert into user (id, name, age, sex) values (#{id}, #{name}, #{age}, #{sex} )")
//    Long save(User user);

    public Long insert(User user) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into user (");
        if (user.getName() != null) {
            sql.append("name, ");
        }


//        SqlTemplate.forUpdate(client,)
        return null;
    }
}
