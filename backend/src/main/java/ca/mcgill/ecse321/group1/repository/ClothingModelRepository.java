package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import org.springframework.data.repository.CrudRepository;

public interface ClothingModelRepository extends CrudRepository<ClothingModel, String> {

  ClothingModel findByClothingModelID(String clothingModelID);

  int deleteByClothingModelID(String clothingModelID);
}
