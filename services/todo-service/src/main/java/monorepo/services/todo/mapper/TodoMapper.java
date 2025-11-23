package monorepo.services.todo.mapper;

import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import jakarta.annotation.Generated;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import monorepo.services.todo.entity.Todo;
import monorepo.services.todo.entity.typehandler.TodoPriorityTypeHandler;
import monorepo.services.todo.entity.typehandler.TodoStateTypeHandler;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonSelectMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface TodoMapper extends CommonSelectMapper, CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    BasicColumn[] selectList = BasicColumn.columnList(id, userId, title, description, state, priority, assignee, dueDate, createdAt, updatedAt, deletedAt);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="row.id")
    int insert(InsertStatementProvider<Todo> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<Todo> records);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TodoResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.BIGINT),
        @Result(column="title", property="title", jdbcType=JdbcType.VARCHAR),
        @Result(column="description", property="description", jdbcType=JdbcType.VARCHAR),
        @Result(column="state", property="state", typeHandler=TodoStateTypeHandler.class, jdbcType=JdbcType.SMALLINT),
        @Result(column="priority", property="priority", typeHandler=TodoPriorityTypeHandler.class, jdbcType=JdbcType.VARCHAR),
        @Result(column="assignee", property="assignee", jdbcType=JdbcType.BIGINT),
        @Result(column="due_date", property="dueDate", jdbcType=JdbcType.DATE),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="deleted_at", property="deletedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Todo> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TodoResult")
    Optional<Todo> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, todo, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, todo, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default int insertMultiple(Collection<Todo> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, todo, c ->
            c.map(userId).toProperty("userId")
            .map(title).toProperty("title")
            .map(description).toProperty("description")
            .map(state).toProperty("state")
            .map(priority).toProperty("priority")
            .map(assignee).toProperty("assignee")
            .map(dueDate).toProperty("dueDate")
            .map(createdAt).toProperty("createdAt")
            .map(updatedAt).toProperty("updatedAt")
            .map(deletedAt).toProperty("deletedAt")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default int insertSelective(Todo row) {
        return MyBatis3Utils.insert(this::insert, row, todo, c ->
            c.map(userId).toPropertyWhenPresent("userId", row::getUserId)
            .map(title).toPropertyWhenPresent("title", row::getTitle)
            .map(description).toPropertyWhenPresent("description", row::getDescription)
            .map(state).toPropertyWhenPresent("state", row::getState)
            .map(priority).toPropertyWhenPresent("priority", row::getPriority)
            .map(assignee).toPropertyWhenPresent("assignee", row::getAssignee)
            .map(dueDate).toPropertyWhenPresent("dueDate", row::getDueDate)
            .map(createdAt).toPropertyWhenPresent("createdAt", row::getCreatedAt)
            .map(updatedAt).toPropertyWhenPresent("updatedAt", row::getUpdatedAt)
            .map(deletedAt).toPropertyWhenPresent("deletedAt", row::getDeletedAt)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default Optional<Todo> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, todo, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default List<Todo> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, todo, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default List<Todo> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, todo, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default Optional<Todo> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, todo, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    static UpdateDSL<UpdateModel> updateAllColumns(Todo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(userId).equalTo(row::getUserId)
                .set(title).equalTo(row::getTitle)
                .set(description).equalTo(row::getDescription)
                .set(state).equalTo(row::getState)
                .set(priority).equalTo(row::getPriority)
                .set(assignee).equalTo(row::getAssignee)
                .set(dueDate).equalTo(row::getDueDate)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .set(deletedAt).equalTo(row::getDeletedAt);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Todo row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(userId).equalToWhenPresent(row::getUserId)
                .set(title).equalToWhenPresent(row::getTitle)
                .set(description).equalToWhenPresent(row::getDescription)
                .set(state).equalToWhenPresent(row::getState)
                .set(priority).equalToWhenPresent(row::getPriority)
                .set(assignee).equalToWhenPresent(row::getAssignee)
                .set(dueDate).equalToWhenPresent(row::getDueDate)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt)
                .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
                .set(deletedAt).equalToWhenPresent(row::getDeletedAt);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    default int updateByPrimaryKeySelective(Todo row) {
        return update(c ->
            c.set(userId).equalToWhenPresent(row::getUserId)
            .set(title).equalToWhenPresent(row::getTitle)
            .set(description).equalToWhenPresent(row::getDescription)
            .set(state).equalToWhenPresent(row::getState)
            .set(priority).equalToWhenPresent(row::getPriority)
            .set(assignee).equalToWhenPresent(row::getAssignee)
            .set(dueDate).equalToWhenPresent(row::getDueDate)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
            .set(deletedAt).equalToWhenPresent(row::getDeletedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}