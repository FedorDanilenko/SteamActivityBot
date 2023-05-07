package jm.bot.steamActivityBot.dto.steamUserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SteamAppShortInfo {

    private Long id;
    private String name;

    @Override
    public String toString() {
        return "Steam App: " + name + " - " + id;
    }
}
