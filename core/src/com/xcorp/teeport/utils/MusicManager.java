package com.xcorp.teeport.utils;

import com.badlogic.gdx.audio.Music;

import com.xcorp.teeport.ui.AssetsScreen;

public class MusicManager {
    Music music;
    String songPlaying;

    public void play(String song) {
        if (song.equals(songPlaying)) {
            return;
        }
        songPlaying = song;
        stop();
        music = AssetsScreen.getMusic(song);
        if (music == null) {
            return;
        }

        music.setLooping(true);
        music.play();
    }

    public void play(String song, float volume) {
        play(song);
        if (music == null) return;
        music.setVolume(volume);
    }

    public void stop() {
        if (music != null)
            music.stop();
    }
}
