package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import org.springframework.data.repository.ListCrudRepository;

public interface ClothingModelRepository extends ListCrudRepository<ClothingModel, String> {

  ClothingModel findByClothingModelID(String clothingModelID);

  int deleteByClothingModelID(String clothingModelID);
}
