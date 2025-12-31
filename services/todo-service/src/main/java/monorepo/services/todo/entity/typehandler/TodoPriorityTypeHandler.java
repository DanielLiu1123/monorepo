package monorepo.services.todo.entity.typehandler;

import monorepo.lib.mybatis.typehandler.BaseProtobufEnumNameTypeHandler;
import monorepo.proto.todo.v1.Todo;

/**
 * Type handler for {@link Todo.Priority}.
 *
 * @author Freeman
 * @since 2025/11/23
 */
public class TodoPriorityTypeHandler extends BaseProtobufEnumNameTypeHandler<Todo.Priority> {}
