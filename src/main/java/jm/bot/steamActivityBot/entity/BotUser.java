package jm.bot.steamActivityBot.entity;


import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity(name="botUserData")
public class BotUser {

    @Id
    @Column(name= "id")
    private Long chatId;

    @Column(name="firstName")
    private String firstName;

    @Column(name="lastName")
    private String lastName;

    @Column(name="userName")
    private String userName;

    @Column(name = "registerTime")
    private Timestamp startTime;

}
