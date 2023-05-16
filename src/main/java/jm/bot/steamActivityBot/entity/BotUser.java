package jm.bot.steamActivityBot.entity;


import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity(name="usersDataTable")
public class BotUser {

    @Id
    @Column(name= "id")
    private Long chatId;

    @Column(name="firstname")
    private String firstName;

    @Column(name="lastname")
    private String lastName;

    @Column(name="username")
    private String userName;

    @Column(name = "registertime")
    private Timestamp startTime;



}
