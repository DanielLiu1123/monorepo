package monorepo.services.todo.service;

import lombok.RequiredArgsConstructor;
import monorepo.services.todo.mapper.TodoMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;
}
