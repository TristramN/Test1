package com.simprints.test1.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
@Table(name = "People")
public class Person extends Model {
    @Column(name = "username")
    public String username;
    @Column(name = "email")
    public String email;

    public Person() {

    }

    public Person(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
