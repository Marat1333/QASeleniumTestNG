package com.leroy.core.fieldfactory;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.Form;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.web_elements.general.ElementList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

public class FieldInitializer {

    private WebDriver driver;
    private By parentBy;
    private Field field;
    private HashMap<String, String> interfaceAndImplMap = new HashMap<>();

    public FieldInitializer(WebDriver driver, Field field) {
        this(driver, field, null);
    }

    public FieldInitializer(WebDriver driver, Field field, CustomLocator locator) {
        this.driver = driver;
        this.field = field;
        this.parentBy = locator != null ? locator.getBy() : null;
        initCompareInterfaceAndImplMap();
    }

    private void initCompareInterfaceAndImplMap() {
        //interfaceAndImplMap.put("IBaseElement", "Element");
    }

    /**
     * The method initializes the field
     */
    public Object initField() {
        boolean isApp = DriverFactory.isAppProfile();
        if (field.getAnnotation(WebFindBy.class) != null ||
                field.getAnnotation(AppFindBy.class) != null) {
            Class<?> decoratableClass = decoratableClass(field);
            if (decoratableClass != null) {
                CustomFieldElementLocator fieldLocator = new CustomFieldElementLocator(field, parentBy);
                Class<?> useClass = field.getAnnotation(AppFindBy.class) != null ?
                        field.getAnnotation(AppFindBy.class).clazz() :
                        field.getAnnotation(WebFindBy.class).clazz();
                return createInstance(decoratableClass, fieldLocator, useClass);
            }
        }
        // Init forms
        if (field.getAnnotation(Form.class) != null) {
            Class<?> decoratableClass = findImplementingClass(field.getType());
            return createInstance(decoratableClass);
        }
        return null;
    }

    /**
     * @return decorable class if it possible, otherwise it return null
     */
    private Class<?> decoratableClass(Field field) {
        Class<?> clazz = findImplementingClass(field.getType());
        try {
            clazz.getConstructor(WebDriver.class, CustomLocator.class);
        } catch (Exception e) {
            return null;
        }

        return clazz;
    }

    /**
     * Find implementations for the class or interface
     */
    private <T> Class<? extends T> findImplementingClass(final Class<T> elementClass) {
        if (elementClass.getConstructors().length > 0)
            return elementClass;
        String packageName = elementClass.getPackage().getName();
        String interfaceName = elementClass.getSimpleName();
        String implClassName = packageName.replaceAll("interfaces", "");
        String implClassFromMap = interfaceAndImplMap.get(interfaceName);
        if (implClassFromMap != null)
            implClassName += implClassFromMap;
        else
            implClassName += interfaceName.replaceFirst("I", "");
        try {
            return (Class<? extends T>) Class.forName(implClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load class for " + implClassName, e);
        }
    }

    /**
     * Create an instance of the class
     */
    private <T> T createInstance(Class<T> clazz, CustomFieldElementLocator fieldLocator, Class<?> listType) {
        try {
            if (fieldLocator == null)
                return clazz.getConstructor().newInstance();
            if (ElementList.class.isAssignableFrom(clazz))
                return clazz.getConstructor(WebDriver.class, CustomLocator.class, listType.getClass())
                        .newInstance(driver, fieldLocator.getLocator(), listType);
            else {
                Constructor<T> constructor = clazz.getConstructor(WebDriver.class, CustomLocator.class);
                constructor.setAccessible(true);
                return constructor
                        .newInstance(driver, fieldLocator.getLocator());
            }
        } catch (Exception e) {
            throw new AssertionError(
                    "WebElement can't be represented as " + clazz
            );
        }
    }

    private <T> T createInstance(Class<T> clazz) {
        return createInstance(clazz, null, null);
    }
}
