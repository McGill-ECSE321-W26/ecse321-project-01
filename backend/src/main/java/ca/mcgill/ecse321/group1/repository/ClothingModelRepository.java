package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface ClothingModelRepository extends ListCrudRepository<ClothingModel, String> {

  ClothingModel findByClothingModelIDAndArchivedFalse(String clothingModelID);

  ClothingModel findByNameAndArchivedFalse(String name);

  List<ClothingModel> findByArchivedFalse();
}
