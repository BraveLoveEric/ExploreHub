package models;

import persistenceComponent.UserConnectionSingleton;

import javax.persistence.*;


@SuppressWarnings("JpaQlInspection")
@NamedQueries({
        @NamedQuery(name= "User.findUserbyEmailPass", query =	"SELECT u FROM User u WHERE u.Email = :email AND u.Password = :password"),
        @NamedQuery(name= "User.findUserbyEmail", query =	"SELECT u FROM User u WHERE u.Email = :email"),
        @NamedQuery(name= "User.findAllUser", query = "SELECT u FROM User u")
})
@NamedNativeQuery(name="checkIfEventInWishList", query ="SELECT Count(*) FROM wishlist WHERE StudentID = ? AND EventID = ?;")

/**
 * Model class which represents the user entity and encapsulates direct access to it
 *
 *@Author: Gheorghe Mironica
 */
@Entity
@DiscriminatorValue(value="0")
public class User extends Account{

    /**
     * Default construtor
     */
    public User(){

    }
    /**
     * Custom constructor
     * @param firstname {@link String} user fist name
     * @param lastname {@link String} user last name
     * @param email {@link String} user email
     * @param password {@link String} user password
     * @param course {@link Courses} user's course
     */
    public User(String firstname, String lastname, String email, String password, Courses course) {
        super(firstname, lastname, email, password, course);
        super.Active = 0;
    }

    /**
     * Custom constructor
     * @param firstname {@link String} user fist name
     * @param lastname {@link String} user last name
     * @param email {@link String} user email
     * @param password {@link String} user password
     * @param course {@link Courses} user's course
     * @param picture {@link String} user's profile picture
     */
    public User(String firstname, String lastname, String email, String password, Courses course, String picture) {
        super(firstname, lastname, email, password, course);
        super.picture = picture;
        super.Active = 0;
    }

    /**
     *  Method to access the access the database interface as User
     * @return Entity Manager
     */
    @Override
    public EntityManager getConnection() {
        UserConnectionSingleton u1 = UserConnectionSingleton.getInstance();
        return u1.getManager();
    }

    /**
     * This method closes connection to db
     */
    @Override
    public void closeConnection(){
        UserConnectionSingleton.getInstance().closeConnection();
    }

}
