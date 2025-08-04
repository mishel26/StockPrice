package auth.jwt.model;


import lombok.Data;

@Data
public class User {
    private String userName;
    private String password;
    //private String token;

    public User(){}
    public User(String userName,String password){
        this.userName = userName;
        this.password = password;
    }
}
