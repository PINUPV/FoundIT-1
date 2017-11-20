package Usuario;

/**
 * Created by nathan on 10/10/2017.
 */

public class Usuario {

    private String name, lastname,username, passwd, email, population, id;

    public Usuario(String id, String name, String lastname, String username, String passwd, String email, String population) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.passwd = passwd;
        this.email = email;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }
}
