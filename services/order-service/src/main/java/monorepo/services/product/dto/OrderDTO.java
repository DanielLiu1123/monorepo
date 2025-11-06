package monorepo.services.product.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/6
 */
public record OrderDTO(Long id, List<Long> itemIds, List<String> itemNames, Map<String, String> attributes,
                       Instant createdAt
) {}
