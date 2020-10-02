// 1. Zaimplementuj CRUD w TaskDAO dla klasy Task (przygotowany kod na github.com - link na początku prezentacji)
// 2. Dodatkowo zaimplementuj metodę pobierającą taski danego usera (z parametrem username)
// 3. Wyniki możesz sprawdzić uruchamiając Main10Exercise


package com.example.dao;

import com.example.model.Task;
import com.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.Configuration.*;

public class TaskDAO implements AutoCloseable {

    private Connection connection = null;

    public TaskDAO() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS task(id BIGINT NOT NULL, description VARCHAR(255), user_id BIGINT, PRIMARY KEY (id), CONSTRAINT user_id FOREIGN KEY (user_id) REFERENCES user(id))");
            statement.executeUpdate("DELETE from task");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(Task task) throws SQLException {
        // tworzymy nowy task w bazie danych na podstawie informacji z argumentu

        connection = DriverManager.getConnection(URL, USER, PASSWORD);

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO task(id, description, user_id) VALUES (?,?,?)");
        preparedStatement.setLong(1, task.getId());
        preparedStatement.setString(2, task.getDescription());
        preparedStatement.setLong(3, task.getUserId());
        preparedStatement.executeUpdate();

        preparedStatement.close();

        connection.close();
    }

    //*******************************************************************************************************************************

    public Optional<Task> read(long id) throws SQLException {
        // wyciągamy dane z bazy na podstawie id taska i przypisujemy do obiektu klasy Task
        // jeśli znajdzie wiersz to zwracamy Optional.of(new Task(...))
        // jeśli nie znajdzie to Optional.empty()

        connection = DriverManager.getConnection(URL, USER, PASSWORD);

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM task WHERE id=?");

        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        preparedStatement.close();

        connection.close();

        if (resultSet.wasNull()) {
            return Optional.empty();
        } else {
            Optional<Task> optionalTask = Optional.of(new Task(resultSet.getLong("id"), resultSet.getString("description"), resultSet.getLong("user_id")));
            return optionalTask;
        }
    }



    //*********************************************************************************************************************************

    public List<Task> readAll() throws SQLException {
        // wyciągamy wszystkie wiersze z bazy danych
        // wyniki zapisujemy w liście obiektów klasy Task

        List<Task> tasks = new ArrayList<>();

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        Statement statement = connection.createStatement();
        ResultSet resultSetReadAll = statement.executeQuery("SELECT * FROM task");
        while (resultSetReadAll.next()) {
            long id = resultSetReadAll.getLong("id");
            String description = resultSetReadAll.getString("description");
            long user_id = resultSetReadAll.getLong("user_id");
            tasks.add(new Task(id, description, user_id));
        }

        statement.close();
        connection.close();

        return tasks;
    }

    public void update(Task task) throws SQLException {
        // aktualizujemy description i user_id na podstawie id taska

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        PreparedStatement preparedStatementUpdateDescription = connection.prepareStatement("UPDATE task SET description = ? WHERE id = ? ");

        preparedStatementUpdateDescription.setString(1, task.getDescription());
        preparedStatementUpdateDescription.setLong(2, task.getId());
        preparedStatementUpdateDescription.executeUpdate();

        preparedStatementUpdateDescription.close();

        PreparedStatement preparedStatementUpdateUserId = connection.prepareStatement("UPDATE task SET user_id = ? WHERE id = ? ");

        preparedStatementUpdateUserId.setLong(1, task.getUserId());
        preparedStatementUpdateUserId.setLong(2, task.getId());
        preparedStatementUpdateUserId.executeUpdate();

        preparedStatementUpdateUserId.close();

        connection.close();

    }

    public void delete(long id) throws SQLException {
        // usuwamy wiersz z bazy na podstawie id taska

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM task WHERE id = ? ");

        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();

        preparedStatement.close();

        connection.close();


    }

    public List<Task> readAllForUser(String username) throws SQLException {
        // dla ochotników
        // konstruujemy query z użyciem JOIN i odwołaniem do tabeli user
        return Collections.emptyList();
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}