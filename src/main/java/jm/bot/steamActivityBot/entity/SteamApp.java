package jm.bot.steamActivityBot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "steamApps")
public class SteamApp {

    @Id
    @Column(name = "appId")
    private Long id;

    @Column(name = "appName")
    private String name;

    @Column(name = "has_achievements")
    private boolean hasAsh;

    @ManyToMany(mappedBy = "steamAppNames")
    private Set<SteamUser> steamUsers = new HashSet<>();

    @OneToMany(mappedBy = "steamApp")
    private List<Achievement> achievements = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SteamApp steamApp)) return false;
        return Objects.equals(getId(), steamApp.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
