package com.birthdates.videotominecraft.command.watch;

import com.birthdates.videotominecraft.movie.Movie;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Another watch command that starts a movie instead of a normal map
 */
public class WatchMovieCommand extends WatchCommand {

    public WatchMovieCommand() {
        super(true, "videotominecraft.watchmovie");
    }

    protected void handleVideo(Player player, String framePath) {
        Movie movie;
        try {
            movie = new Movie(player.getLocation(), framePath);
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(ChatColor.RED + "Failed to place the theatre!");
            return;
        }

        movie.start();
        player.sendMessage(ChatColor.GREEN + "Movie started.");
    }
}


