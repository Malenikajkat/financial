package org.malenikajkat.service.category;

import org.malenikajkat.model.Category;
import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;

import java.util.Set;

public interface CategoryService {
    void addCategory(User user, String categoryName) throws ServiceException, ValidationException;
    void removeCategory(User user, String categoryName) throws ServiceException, ValidationException;
    boolean hasCategory(User user, String categoryName) throws ServiceException, ValidationException;
    Set<Category> getAllCategories(User user) throws ServiceException;
    void renameCategory(User user, String oldName, String newName) throws ServiceException, ValidationException;
}