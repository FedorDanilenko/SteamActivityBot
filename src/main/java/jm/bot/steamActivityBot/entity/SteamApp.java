package jm.bot.steamActivityBot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Games")
public class SteamApp {

    @Id
    @Column(name = "gameId")
    private Long id;

    @Column(name = "gameName")
    private String name;

    @Column(name = "has_achievements")
    private boolean hasAsh;

    @ManyToMany(mappedBy = "steamAppNames")
    private Set<SteamUser> steamUsers = new HashSet<>();

//    @OneToMany(mappedBy = "games")
//    private List<Achievement> achievements;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SteamApp)) return false;
        SteamApp steamApp = (SteamApp) o;
        return Objects.equals(getId(), steamApp.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}
