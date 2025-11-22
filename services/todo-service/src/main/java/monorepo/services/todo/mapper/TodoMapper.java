package monorepo.services.todo.mapper;

import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.assignee;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.createdAt;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.description;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.dueDate;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.id;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.priority;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.status;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.title;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.todo;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.updatedAt;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.userId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import monorepo.services.todo.entity.TodoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonInsertMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

/**
 * MyBatis mapper for Todo operations
 *
 * @author Claude
 * @since 2025/11/22
 */
@Mapper
public interface TodoMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<TodoEntity>,
        CommonUpdateMapper {

    BasicColumn[] selectList = BasicColumn.columnList(id, userId, title, description, status, priority, assignee,
            dueDate, createdAt, updatedAt);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(
            id = "TodoResult",
            value = {
                @Result(column = "id", property = "id", id = true),
                @Result(column = "user_id", property = "userId"),
                @Result(column = "title", property = "title"),
                @Result(column = "description", property = "description"),
                @Result(column = "status", property = "status"),
                @Result(column = "priority", property = "priority"),
                @Result(column = "assignee", property = "assignee"),
                @Result(column = "due_date", property = "dueDate"),
                @Result(column = "created_at", property = "createdAt"),
                @Result(column = "updated_at", property = "updatedAt")
            })
    List<TodoEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("TodoResult")
    Optional<TodoEntity> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, todo, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, todo, completer);
    }

    default int deleteByPrimaryKey(String id_) {
        return delete(c -> c.where(id, isEqualTo(id_)));
    }

    default int insert(TodoEntity row) {
        return MyBatis3Utils.insert(this::insert, row, todo, c -> c.map(id)
                .toProperty("id")
                .map(userId)
                .toProperty("userId")
                .map(title)
                .toProperty("title")
                .map(description)
                .toProperty("description")
                .map(status)
                .toProperty("status")
                .map(priority)
                .toProperty("priority")
                .map(assignee)
                .toProperty("assignee")
                .map(dueDate)
                .toProperty("dueDate")
                .map(createdAt)
                .toProperty("createdAt")
                .map(updatedAt)
                .toProperty("updatedAt"));
    }

    default int insertSelective(TodoEntity row) {
        return MyBatis3Utils.insert(this::insert, row, todo, c -> c.map(id)
                .toPropertyWhenPresent("id", row::getId)
                .map(userId)
                .toPropertyWhenPresent("userId", row::getUserId)
                .map(title)
                .toPropertyWhenPresent("title", row::getTitle)
                .map(description)
                .toPropertyWhenPresent("description", row::getDescription)
                .map(status)
                .toPropertyWhenPresent("status", row::getStatus)
                .map(priority)
                .toPropertyWhenPresent("priority", row::getPriority)
                .map(assignee)
                .toPropertyWhenPresent("assignee", row::getAssignee)
                .map(dueDate)
                .toPropertyWhenPresent("dueDate", row::getDueDate)
                .map(createdAt)
                .toPropertyWhenPresent("createdAt", row::getCreatedAt)
                .map(updatedAt)
                .toPropertyWhenPresent("updatedAt", row::getUpdatedAt));
    }

    default Optional<TodoEntity> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, todo, completer);
    }

    default List<TodoEntity> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, todo, completer);
    }

    default List<TodoEntity> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, todo, completer);
    }

    default Optional<TodoEntity> selectByPrimaryKey(String id_) {
        return selectOne(c -> c.where(id, isEqualTo(id_)));
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, todo, completer);
    }

    static UpdateDSL<UpdateModel> updateAllColumns(TodoEntity row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id)
                .equalTo(row::getId)
                .set(userId)
                .equalTo(row::getUserId)
                .set(title)
                .equalTo(row::getTitle)
                .set(description)
                .equalTo(row::getDescription)
                .set(status)
                .equalTo(row::getStatus)
                .set(priority)
                .equalTo(row::getPriority)
                .set(assignee)
                .equalTo(row::getAssignee)
                .set(dueDate)
                .equalTo(row::getDueDate)
                .set(createdAt)
                .equalTo(row::getCreatedAt)
                .set(updatedAt)
                .equalTo(row::getUpdatedAt);
    }

    static UpdateDSL<UpdateModel> updateSelectiveColumns(TodoEntity row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id)
                .equalToWhenPresent(row::getId)
                .set(userId)
                .equalToWhenPresent(row::getUserId)
                .set(title)
                .equalToWhenPresent(row::getTitle)
                .set(description)
                .equalToWhenPresent(row::getDescription)
                .set(status)
                .equalToWhenPresent(row::getStatus)
                .set(priority)
                .equalToWhenPresent(row::getPriority)
                .set(assignee)
                .equalToWhenPresent(row::getAssignee)
                .set(dueDate)
                .equalToWhenPresent(row::getDueDate)
                .set(createdAt)
                .equalToWhenPresent(row::getCreatedAt)
                .set(updatedAt)
                .equalToWhenPresent(row::getUpdatedAt);
    }

    default int updateByPrimaryKey(TodoEntity row) {
        return update(c -> c.set(userId)
                .equalTo(row::getUserId)
                .set(title)
                .equalTo(row::getTitle)
                .set(description)
                .equalTo(row::getDescription)
                .set(status)
                .equalTo(row::getStatus)
                .set(priority)
                .equalTo(row::getPriority)
                .set(assignee)
                .equalTo(row::getAssignee)
                .set(dueDate)
                .equalTo(row::getDueDate)
                .set(updatedAt)
                .equalTo(row::getUpdatedAt)
                .where(id, isEqualTo(row::getId)));
    }

    default int updateByPrimaryKeySelective(TodoEntity row) {
        return update(c -> c.set(userId)
                .equalToWhenPresent(row::getUserId)
                .set(title)
                .equalToWhenPresent(row::getTitle)
                .set(description)
                .equalToWhenPresent(row::getDescription)
                .set(status)
                .equalToWhenPresent(row::getStatus)
                .set(priority)
                .equalToWhenPresent(row::getPriority)
                .set(assignee)
                .equalToWhenPresent(row::getAssignee)
                .set(dueDate)
                .equalToWhenPresent(row::getDueDate)
                .set(updatedAt)
                .equalToWhenPresent(row::getUpdatedAt)
                .where(id, isEqualTo(row::getId)));
    }

    default List<TodoEntity> selectByIds(Collection<String> ids) {
        return select(c -> c.where(id, isIn(ids)));
    }
}
