package org.malenikajkat.service.category;

import org.malenikajkat.model.Category;
import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;
import org.malenikajkat.util.ValidatorService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoryServiceImpl implements CategoryService {

    private final ValidatorService validatorService;

    public CategoryServiceImpl(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    @Override
    public void addCategory(User user, String categoryName) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(categoryName, "название категории");

        if (hasCategory(user, categoryName)) {
            throw new ServiceException("Категория '" + categoryName + "' уже существует");
        }

        Category category = new Category(categoryName);
        user.getWallet().addCategory(category.getName());
    }

    @Override
    public void removeCategory(User user, String categoryName) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(categoryName, "название категории");

        if (!hasCategory(user, categoryName)) {
            throw new ServiceException("Категория '" + categoryName + "' не найдена");
        }

        user.getWallet().removeCategory(categoryName);
    }

    @Override
    public boolean hasCategory(User user, String categoryName) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(categoryName, "название категории");

        return user.getWallet().hasCategory(categoryName);
    }

    @Override
    public Set<Category> getAllCategories(User user) throws ServiceException {
        validateUser(user);

        Set<String> categoryNames = user.getWallet().getAllCategories();
        return categoryNames.stream()
                .map(Category::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public void renameCategory(User user, String oldName, String newName) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(oldName, "старое название категории");
        validatorService.validateCategory(newName, "новое название категории");

        if (!hasCategory(user, oldName)) {
            throw new ServiceException("Категория '" + oldName + "' не найдена");
        }
        if (hasCategory(user, newName)) {
            throw new ServiceException("Категория '" + newName + "' уже существует");
        }

        user.getWallet().renameCategory(oldName, newName);
    }

    private void validateUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }
    }
}