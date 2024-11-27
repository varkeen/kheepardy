package org.khee.kheepardygl;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CategoryControl {

  public static CategoryControl INSTANCE;

  private ArrayList<Category> categories;
  private HashMap<String, Category> categoriesByName;

  private Category activeCategory;

  private boolean categoriesLoaded;
  private boolean busy;

  public static CategoryControl getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CategoryControl();
    }
    return INSTANCE;
  }

  public CategoryControl() {
    this.categoriesByName = new HashMap<>();
    this.categories = new ArrayList<>();
    this.categoriesLoaded = false;
    this.busy = false;
  }

  public void loadCategories() {

    if (categoriesLoaded) {
      return;
    }

    File categoriesDirectory = new File("categories");
    for (File categoryDirectory : categoriesDirectory.listFiles()) {
      ObjectMapper om =
          new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

      try {
        Category category =
            om.readValue(
                new File(categoryDirectory.getAbsolutePath() + "/data.yaml"), Category.class);

        category.setPath(categoryDirectory.getAbsolutePath());

        if (category.getName().length() > 0) {
          this.categories.add(category);
          this.categoriesByName.put(category.getName(), category);
        }
      } catch (StreamReadException eStream) {
        eStream.printStackTrace();
      } catch (IOException eIO) {
        System.err.println("Could not open " + categoryDirectory.getAbsolutePath() + "/data.yaml");
      }
    }

    this.categoriesLoaded = true;
  }

  public Category getActiveCategory() {
    return this.activeCategory;
  }

  public void showAnswer( Category category, int index ) {

    this.activeCategory = category;
    category.showAnswer(index);
  }

  public void deactivateAll() {
    for (Category category: this.categories) {
      category.deactivate();
    }
  }

  public ArrayList<Category> getCategories() {
    return categories;
  }

  public void setCategories(ArrayList<Category> categories) {
    this.categories = categories;
  }

  public HashMap<String, Category> getCategoriesByName() {
    return categoriesByName;
  }

  public void setCategoriesByName(HashMap<String, Category> categoriesByName) {
    this.categoriesByName = categoriesByName;
  }

  public boolean isBusy() {
    return busy;
  }

  public void setBusy(boolean busy) {
    this.busy = busy;
  }
}
