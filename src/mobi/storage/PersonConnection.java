/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package mobi.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mobi.entity.Person;

/**
 *
 * @author evan.summers
 */
public class PersonConnection {
    static QueryMap sqlMap = new QueryMap(PersonConnection.class);
    Connection connection;

    public PersonConnection(Connection connection) {
        this.connection = connection;
    }
        
    public Person newPerson(ResultSet resultSet) throws SQLException {
        Person person = new Person();
        person.setPersonId(resultSet.getLong("person_id"));
        person.setAccountId(resultSet.getLong("account_id"));
        person.setEmail(resultSet.getString("person_email"));
        person.setPersonName(resultSet.getString("person_name"));
        person.setPasswordHash(resultSet.getString("password_hash"));
        person.setPasswordSalt(resultSet.getString("password_salt"));
        person.setLastLogin(resultSet.getTimestamp("last_login"));
        return person;
    }

    public boolean existsPerson(String email) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("exists"));
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }
    
    public Person find(String email) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("find by email"));
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
            return null;
        }
        return newPerson(resultSet);
    }
    
    public long insertPerson(Person person) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("insert"));
        statement.setLong(1, person.getAccountId());
        statement.setString(2, person.getEmail());
        statement.setString(3, person.getPersonName());
        statement.setString(4, person.getPasswordHash());
        statement.setString(5, person.getPasswordSalt());
        int updateCount = statement.executeUpdate();
        if (updateCount != 1) {
            throw new SQLException();    
        }
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new SQLException();
        }   
        long personId = generatedKeys.getLong(1);
        person.setPersonId(personId);
        return personId;
    }
        
}
