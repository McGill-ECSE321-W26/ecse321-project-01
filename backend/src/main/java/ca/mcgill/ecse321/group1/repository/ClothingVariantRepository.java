package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.ClothingVariant;
import org.springframework.data.repository.CrudRepository;

public interface ClothingVariantRepository extends CrudRepository<ClothingVariant, String> {

  ClothingVariant findClothingVariantByClothingVariantID(String id);
}
