package monorepo.services.product.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/6
 */
@Data
public class Order {
    private Long id;
    private List<Long> itemIds;
    private String[] itemNames;
    private Map<String, String> attributes;
}
