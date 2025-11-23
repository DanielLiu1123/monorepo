package monorepo.services.todo.entity.typehandler;

import monorepo.lib.mybatis.typehandler.BaseProtobufEnumTypeHandler;
import monorepo.proto.todo.v1.TodoModel;

public class TodoStateTypeHandler extends BaseProtobufEnumTypeHandler<TodoModel.State, Integer> {}
