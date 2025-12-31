package monorepo.services.product.converter;

import monorepo.lib.msp.MapStructConfig;
import monorepo.proto.order.v1.OrderModel;
import monorepo.services.product.dto.OrderDTO;
import monorepo.services.product.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Order converter.
 *
 * @author Freeman
 * @since 2025/11/6
 */
@Mapper(config = MapStructConfig.class)
public interface OrderConverter {
    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);

    OrderModel entityToModel(Order entity);

    OrderModel dtoToModel(OrderDTO dto);

    Order modelToEntity(OrderModel model);

    Order dtoToEntity(OrderDTO dto);

    OrderDTO entityToDto(Order entity);

    OrderDTO modelToDto(OrderModel model);
}
