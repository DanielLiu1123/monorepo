package monorepo.services.todo.entity.typehandler;

import monorepo.lib.mybatis.typehandler.BaseProtobufEnumTypeHandler;
import monorepo.proto.todo.v1.Todo;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/23
 */
public class TodoPriorityTypeHandler extends BaseProtobufEnumTypeHandler<Todo.Priority, String> {}
