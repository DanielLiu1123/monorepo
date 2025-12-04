package monorepo.services.todo.entity.typehandler;

import monorepo.lib.mybatis.typehandler.BaseProtobufEnumNumberTypeHandler;
import monorepo.proto.todo.v1.Todo;

public class TodoStateTypeHandler extends BaseProtobufEnumNumberTypeHandler<Todo.State> {}
