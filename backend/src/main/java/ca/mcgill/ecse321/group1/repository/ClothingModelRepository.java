package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import org.springframework.data.repository.ListCrudRepository;

public interface ClothingModelRepository extends ListCrudRepository<ClothingModel, String> {

  ClothingModel findByClothingModelID(String clothingModelID);

  ClothingModel findByName(String name);

  int deleteByClothingModelID(String clothingModelID);
}
