//package jm.bot.steamActivityBot.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.util.HashSet;
//import java.util.Set;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity(name = "Achievements")
//public class Achievement {
//
//    @Id
//    @Column(name = "achId")
//    private Long id;
//
//    @Column(name = "receivingTime")
//    private LocalDate timeRec;
//
//    @ManyToOne
//    @JoinColumn(name = "game_id", nullable = false)
//    private SteamApp games;
//
//    @ManyToMany(mappedBy = "userAchievements")
//    private Set<SteamUser> steamUsers = new HashSet<>();
//
//}
