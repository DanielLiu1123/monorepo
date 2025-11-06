package monorepo.services.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/6
 */
@Getter
@Builder
public class OrderDTO {
    private Long id;
    @Singular
    private List<Long> itemIds;
    @Singular
    private Map<String, String> attributes;
}
