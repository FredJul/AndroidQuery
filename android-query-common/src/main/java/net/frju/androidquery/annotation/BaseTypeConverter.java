package net.frju.androidquery.annotation;

/**
 * Author: andrewgrosner
 * Description: This class is responsible for converting the stored database value into the field value in
 * a Model.
 */
public abstract class BaseTypeConverter<DbClass, ModelClass> {

    /**
     * Converts the ModelClass into a DbClass
     *
     * @param model this will be called upon syncing
     * @return The DbClass value that converts into a SQLite type
     */
    public abstract DbClass convertToDb(ModelClass model);

    /**
     * Converts a DbClass from the DB into a ModelClass
     *
     * @param data This will be called when the model is loaded from the DB
     * @return The ModelClass value that gets set in a Model that holds the data class.
     */
    public abstract ModelClass convertFromDb(DbClass data);
}
