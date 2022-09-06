package hotelReviewBackend.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    private String username;
    private String name;
    private String surname;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        if (!role.equals("worker") && !role.equals("admin"))
            role = "worker";
        System.out.println("role2="+role);

        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public JSONObject toJson(){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("username",username);
            jsonObject.put("password",password);
            jsonObject.put("nome",name);
            jsonObject.put("surname",surname);
            jsonObject.put("address",address);
            jsonObject.put("email",email);
            jsonObject.put("phone",phone);
            jsonObject.put("role",role);

        }catch(JSONException e){
            e.printStackTrace();
        }
        return jsonObject;


    }
}