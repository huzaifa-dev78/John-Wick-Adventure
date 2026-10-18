package com.marioexample.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.marioexample.core.MainGame;

public class TeaVMLauncher {
    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0; // 0 means take canvas/window width
        config.height = 0;
        new TeaApplication(new MainGame(), config);
    }
}
