package com.jiuyi.qujiuyi.common.interceptor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.log4j.Logger;

import com.jiuyi.qujiuyi.dto.BaseDto;
import com.jiuyi.qujiuyi.dto.Page;

@Intercepts({ @Signature(method = "prepare", type = StatementHandler.class, args = { Connection.class }) })
public class PageInterceptor implements Interceptor {

    private final static Logger logger = Logger.getLogger(PageInterceptor.class);

    private String databaseType;// 数据库类型，不同的数据库有不同的分页方法

    /**
     * 拦截后要执行的方法
     */
    public Object intercept(Invocation invocation) throws Throwable {
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();

        StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");

        BoundSql boundSql = delegate.getBoundSql();

        // 第一种根据ID进入分页
        // String id = mappedStatement.getId();
        // System.out.println("id------" + id);

        // 第二种根据参数类型进入分页
        Object obj = boundSql.getParameterObject();

		// 打印SQL语句
        logger.debug(boundSql.getSql().replaceAll("\\s+|\t|\r|\n", " "));

        String sql = null;

        if (obj instanceof BaseDto) {
            BaseDto dto = (BaseDto) obj;

            Page page = dto.getPage();

            if (page != null && boundSql.getSql().toLowerCase().indexOf("select") != -1) {
                // 拦截到的prepare方法参数是一个Connection对象
                Connection connection = (Connection) invocation.getArgs()[0];

                // 获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
                sql = boundSql.getSql();

				// 通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
				MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate,
						"mappedStatement");

                // 给当前的page参数对象设置总记录数
				this.setTotalRecord(dto, mappedStatement, connection);

                // 获取分页Sql语句
				sql = this.getPageSql(page, sql);

                // 利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
				ReflectUtil.setFieldValue(boundSql, "sql", sql);
            }
        }

        return invocation.proceed();
    }

    /**
     * 拦截器对应的封装原始对象的方法
     */
    public Object plugin(Object target) {
        // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    /**
     * 设置注册拦截器时设定的属性
     */
    public void setProperties(Properties properties) {
        this.databaseType = properties.getProperty("databaseType");
    }

    /**
     * 根据page对象获取对应的分页查询Sql语句，这里只做了两种数据库类型，Mysql和Oracle 其它的数据库都没有进行分页
     * 
     * @param page
     *            分页对象
     * @param sql
     *            原sql语句
     * @return
     */
    private String getPageSql(Page page, String sql) {
        StringBuffer sqlBuffer = new StringBuffer(sql);
        if ("mysql".equalsIgnoreCase(databaseType)) {
            return getMysqlPageSql(page, sqlBuffer);
        } else if ("oracle".equalsIgnoreCase(databaseType)) {
            return getOraclePageSql(page, sqlBuffer);
        }
        return sqlBuffer.toString();
    }

    /**
     * 获取Mysql数据库的分页查询语句
     * 
     * @param page
     *            分页对象
     * @param sqlBuffer
     *            包含原sql语句的StringBuffer对象
     * @return Mysql数据库分页语句
     */
    private String getMysqlPageSql(Page page, StringBuffer sqlBuffer) {
        // 计算第一条记录的位置，Mysql中记录的位置是从0开始的。
        int offset = (page.getCurrentPage() - 1) * page.getPageSize();
        sqlBuffer.append(" limit ").append(offset).append(",").append(page.getPageSize());
        return sqlBuffer.toString();
    }

    /**
     * 获取Oracle数据库的分页查询语句
     * 
     * @param page
     *            分页对象
     * @param sqlBuffer
     *            包含原sql语句的StringBuffer对象
     * @return Oracle数据库的分页查询语句
     */
    private String getOraclePageSql(Page page, StringBuffer sqlBuffer) {
        int offset = (page.getCurrentPage() - 1) * page.getPageSize() + 1;

        sqlBuffer.insert(0, "select u.*, rownum r from (").append(") u where rownum < ")
                .append(offset + page.getPageSize());

        sqlBuffer.insert(0, "select * from (").append(") where r >= ").append(offset);

        return sqlBuffer.toString();
    }

    /**
     * 给当前的参数对象page设置总记录数
     * 
     * @param page
     *            Mapper映射语句对应的参数对象
     * @param mappedStatement
     *            Mapper映射语句
     * @param connection
     *            当前的数据库连接
     */
	private void setTotalRecord(BaseDto dto, MappedStatement mappedStatement, Connection connection) {

        // 获取对应的BoundSql，这个BoundSql其实跟我们利用StatementHandler获取到的BoundSql是同一个对象。
        // delegate里面的boundSql也是通过mappedStatement.getBoundSql(paramObj)方法获取到的。
		BoundSql boundSql = mappedStatement.getBoundSql(dto);

        // 获取到我们自己写在Mapper映射语句中对应的Sql语句
        String sql = boundSql.getSql();

        // 通过查询Sql语句获取到对应的计算总记录数的sql语句
        String countSql = this.getCountSql(sql);

        // 通过BoundSql获取对应的参数映射
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        // 利用Configuration、查询记录数的Sql语句countSql、参数映射关系parameterMappings和参数对象page建立查询记录数对应的BoundSql对象。
		BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, dto);

        // 通过mappedStatement、参数对象page和BoundSql对象countBoundSql建立一个用于设定参数的ParameterHandler对象
		ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, dto, countBoundSql);

        // 通过connection建立一个countSql对应的PreparedStatement对象。
        PreparedStatement pstmt = null;

        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(countSql);

            // 通过parameterHandler给PreparedStatement对象设置参数
            parameterHandler.setParameters(pstmt);

            // 之后就是执行获取总记录数的Sql语句和获取结果了。
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int totalRecord = rs.getInt(1);

                // 给当前的参数page对象设置总记录数
				dto.getPage().setTotalRecord(totalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据原Sql语句获取对应的查询总记录数的Sql语句
     * 
     * @param sql
     * @return
     */
    private String getCountSql(String sql) {
        return "select count(*) from (" + sql + ") tableAlias";
    }

    /**
     * 利用反射进行操作的一个工具类
     * 
     */
    private static class ReflectUtil {
        /**
         * 利用反射获取指定对象的指定属性
         * 
         * @param obj
         *            目标对象
         * @param fieldName
         *            目标属性
         * @return 目标属性的值
         */
        public static Object getFieldValue(Object obj, String fieldName) {
            Object result = null;
            Field field = ReflectUtil.getField(obj, fieldName);
            if (field != null) {
                field.setAccessible(true);
                try {
                    result = field.get(obj);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        /**
         * 利用反射获取指定对象里面的指定属性
         * 
         * @param obj
         *            目标对象
         * @param fieldName
         *            目标属性
         * @return 目标字段
         */
        private static Field getField(Object obj, String fieldName) {
            Field field = null;
            for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException e) {
                    // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
                }
            }
            return field;
        }

        /**
         * 利用反射设置指定对象的指定属性为指定的值
         * 
         * @param obj
         *            目标对象
         * @param fieldName
         *            目标属性
         * @param fieldValue
         *            目标值
         */
        public static void setFieldValue(Object obj, String fieldName, String fieldValue) {
            Field field = ReflectUtil.getField(obj, fieldName);
            if (field != null) {
                try {
                    field.setAccessible(true);
                    field.set(obj, fieldValue);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}