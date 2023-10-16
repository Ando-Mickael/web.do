package application.models;

import system.annotations.Browseable;
import system.annotations.Url;
import java.util.HashMap;
import system.utils.ModelView;

@Browseable
public class Hello {

    @Browseable
    String nom;

    @Browseable
    Integer age;

    public Hello() {
    }

    public Hello(String nom, Integer age) {
        this.nom = nom;
        this.age = age;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Url(name = "helloWorld")
    public ModelView hello() {
        ModelView result = new ModelView();
        HashMap data = new HashMap();
        String message = "hello ";
        if (getNom() == null) {
            message += "world";
        } else {
            message += getNom();
        }

        if (getAge() == null) {
            message += "";
        } else {
            message += ", " + getAge();
        }

        data.put("hello", message);
        result.setUrl("hello.jsp");
        result.setData(data);

        return result;
    }

    public void sansRetour() {
        System.out.println("Ando Mickaelllll");
    }

    public String avecRetour() {
        return "Ando Mick";
    }

}
