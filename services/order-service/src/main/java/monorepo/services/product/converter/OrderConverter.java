package monorepo.services.product.converter;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.ReportingPolicy.ERROR;

import monorepo.proto.order.v1.OrderModel;
import monorepo.services.product.dto.OrderDTO;
import monorepo.services.product.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/6
 */
@Mapper(nullValueCheckStrategy = ALWAYS, unmappedTargetPolicy = ERROR)
public interface OrderConverter {
    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);

    OrderModel entityToModel(Order entity);

    OrderModel dtoToModel(OrderDTO dto);

    Order modelToEntity(OrderModel model);

    Order dtoToEntity(OrderDTO dto);

    OrderDTO entityToDto(Order entity);

    OrderDTO modelToDto(OrderModel model);
}
