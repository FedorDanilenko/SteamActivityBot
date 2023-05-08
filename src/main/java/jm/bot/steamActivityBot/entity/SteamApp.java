package jm.bot.steamActivityBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Data
@Entity(name = "Games")
public class SteamApp {

    @Id
    @Column(name = "gameId")
    private Long id;

    @Column(name = "gameName")
    private String name;

    @ManyToMany(mappedBy = "steamAppNames")
    private Set<SteamUser> steamUsers;

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
