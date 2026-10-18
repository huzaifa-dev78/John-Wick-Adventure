package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * Fix for TeaVM Web builds where package-private reflection fails on GlyphRun.
 * Pre-configures a non-reflective pool so new GlyphRun() is called directly.
 */
public class GlyphRunFix {
    public static void apply() {
        Pools.set(GlyphRun.class, new Pool<GlyphRun>() {
            @Override
            protected GlyphRun newObject() {
                return new GlyphRun();
            }
        });
    }
}
