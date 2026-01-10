package monorepo.services.todo.mapper;

import static monorepo.services.todo.mapper.TodoSubtaskDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import jakarta.annotation.Generated;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import monorepo.lib.mybatis.datasources.dynamic.DynamicDataSource;
import monorepo.services.todo.entity.TodoSubtask;
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
public interface TodoSubtaskMapper extends CommonSelectMapper, CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper, DynamicDataSource<TodoSubtaskMapper> {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    BasicColumn[] selectList = BasicColumn.columnList(id, todoId, title, createdAt, updatedAt, deletedAt);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="row.id")
    int insert(InsertStatementProvider<TodoSubtask> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<TodoSubtask> records);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TodoSubtaskResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="todo_id", property="todoId", jdbcType=JdbcType.BIGINT),
        @Result(column="title", property="title", jdbcType=JdbcType.VARCHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="deleted_at", property="deletedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<TodoSubtask> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TodoSubtaskResult")
    Optional<TodoSubtask> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, todoSubtask, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, todoSubtask, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default int insertMultiple(Collection<TodoSubtask> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, todoSubtask, c ->
            c.map(todoId).toProperty("todoId")
            .map(title).toProperty("title")
            .map(createdAt).toProperty("createdAt")
            .map(updatedAt).toProperty("updatedAt")
            .map(deletedAt).toProperty("deletedAt")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default int insertSelective(TodoSubtask row) {
        return MyBatis3Utils.insert(this::insert, row, todoSubtask, c ->
            c.map(todoId).toPropertyWhenPresent("todoId", row::getTodoId)
            .map(title).toPropertyWhenPresent("title", row::getTitle)
            .map(createdAt).toPropertyWhenPresent("createdAt", row::getCreatedAt)
            .map(updatedAt).toPropertyWhenPresent("updatedAt", row::getUpdatedAt)
            .map(deletedAt).toPropertyWhenPresent("deletedAt", row::getDeletedAt)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default Optional<TodoSubtask> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, todoSubtask, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default List<TodoSubtask> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, todoSubtask, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default List<TodoSubtask> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, todoSubtask, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default Optional<TodoSubtask> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, todoSubtask, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    static UpdateDSL<UpdateModel> updateAllColumns(TodoSubtask row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(todoId).equalTo(row::getTodoId)
                .set(title).equalTo(row::getTitle)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .set(deletedAt).equalTo(row::getDeletedAt);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(TodoSubtask row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(todoId).equalToWhenPresent(row::getTodoId)
                .set(title).equalToWhenPresent(row::getTitle)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt)
                .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
                .set(deletedAt).equalToWhenPresent(row::getDeletedAt);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    default int updateByPrimaryKeySelective(TodoSubtask row) {
        return update(c ->
            c.set(todoId).equalToWhenPresent(row::getTodoId)
            .set(title).equalToWhenPresent(row::getTitle)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
            .set(deletedAt).equalToWhenPresent(row::getDeletedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}