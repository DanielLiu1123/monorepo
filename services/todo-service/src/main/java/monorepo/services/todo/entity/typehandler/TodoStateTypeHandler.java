package monorepo.services.todo.entity.typehandler;

import monorepo.lib.mybatis.typehandler.BaseProtobufEnumTypeHandler;
import monorepo.proto.todo.v1.Todo;

public class TodoStateTypeHandler extends BaseProtobufEnumTypeHandler<Todo.State, Integer> {}
