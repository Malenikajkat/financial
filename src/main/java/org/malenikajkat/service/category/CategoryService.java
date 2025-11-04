package org.malenikajkat.service.category;

import org.malenikajkat.model.Category;
import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;

import java.util.Set;

public interface CategoryService {

    void addCategory(User user, String categoryName) throws ServiceException;

    void removeCategory(User user, String categoryName) throws ServiceException;

    boolean hasCategory(User user, String categoryName) throws ServiceException;

    Set<Category> getAllCategories(User user) throws ServiceException;

    void renameCategory(User user, String oldName, String newName) throws ServiceException;
}
