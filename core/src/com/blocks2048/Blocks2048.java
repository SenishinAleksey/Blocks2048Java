package com.blocks2048;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.*;

public class Blocks2048 extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    List<Block> blocks;
    TextureAtlas atlas;
    Map<Integer, List<Block>> lines;
    static final int SCALE = 80;
    int score;
    int speed;
    BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Pacifico.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.RED;
        font = generator.generateFont(parameter);
        generator.dispose();
        background = new Texture(Gdx.files.internal("background.png"));
        atlas = new TextureAtlas(Gdx.files.internal("blocks/blocks.atlas"));
        newGame();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        font.draw(batch, String.valueOf(score), 5, 550);
        checkLines();
        if (!isGameOver()) {
            batch.draw(background, 0, 0);
            checkSpeed();
            game();
        } else {
            font.draw(batch, "Game over", 120, 300);
            font.draw(batch, "press Space button", 40, 80);
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                newGame();
            }
        }
        batch.end();
    }

    private void newGame() {
        lines = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            lines.put(1 + SCALE * i, new ArrayList<Block>());
        }
        blocks = new ArrayList<>();
        blocks.add(new Block());
        speed = 1;
        score = 0;
    }

    private void checkSpeed() {
        if (score / 5000 > speed - 1) {
            speed++;
        }
    }

    private void game() {
        boolean newBlock = false;
        for (Block block : blocks) {
            batch.draw(atlas.findRegion(block.getRegionName()), block.posX, block.posY);
            if (!block.finalPosition) {
                block.posY -= speed;
                List<Block> line = lines.get(block.posX);
                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                    if (line.size() == 0) {
                        block.posY = 1;
                    } else {
                        block.posY = line.get(line.size() - 1).posY + SCALE;
                    }
                }
                if ((line.size() == 0 && block.posY <= 1) ||
                    (line.size() > 0 && block.posY <= line.get(line.size() - 1).posY + SCALE)
                ) {
                    line.add(block);
                    block.finalPosition = true;
                    score += block.num;
                    newBlock = true;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                    if (block.posX > 1 && (lines.get(block.posX - SCALE).size() == 0 || lines.get(block.posX - SCALE).get(lines.get(block.posX - SCALE).size() - 1).posY + SCALE < block.posY)) {
                        block.posX -= SCALE;
                    }
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    if (block.posX < 1 + SCALE * 4 && (lines.get(block.posX + SCALE).size() == 0 || lines.get(block.posX + SCALE).get(lines.get(block.posX + SCALE).size() - 1).posY + SCALE < block.posY)) {
                        block.posX += SCALE;
                    }
                }
            }
        }
        if (newBlock) {
            blocks.add(new Block());
        }
    }

    private void checkLines() {
        for (Map.Entry<Integer, List<Block>> entry : lines.entrySet()) {
            List<Block> line = entry.getValue();
            if (line.size() > 0) {
                for (int i = line.size() - 1; i >= 0; i--) {
                    Block block = line.get(i);
                    if (block.forRemove) {
                        continue;
                    }
                    int factor = 0;
                    if (i - 1 >= 0) {
                        if (block.num == line.get(i - 1).num) {
                            factor++;
                            score += block.num;
                            line.get(i - 1).forRemove = true;
                            line.remove(i - 1);
                        }
                    }
                    if (block.posX > 1 && lines.get(block.posX - SCALE).size() > i && lines.get(block.posX - SCALE).get(i).num == block.num) {
                        factor++;
                        score += block.num;
                        lines.get(block.posX - SCALE).get(i).forRemove = true;
                        lines.get(block.posX - SCALE).remove(i);

                    }
                    if (block.posX < 1 + SCALE * 4 && lines.get(block.posX + SCALE).size() > i && lines.get(block.posX + SCALE).get(i).num == block.num) {
                        factor++;
                        score += block.num;
                        lines.get(block.posX + SCALE).get(i).forRemove = true;
                        lines.get(block.posX + SCALE).remove(i);

                    }
                    if (factor > 0) {
                        block.add(factor);
                        break;
                    }
                }
            }

        }
        boolean recheck = false;
        Stack<Integer> indexesForRemove = new Stack<>();
        for (int index = 0; index < blocks.size(); index++) {
            if (blocks.get(index).forRemove) {
                recheck = true;
                indexesForRemove.push(index);
            }
        }
        while (!indexesForRemove.empty()) {
            blocks.remove((int)indexesForRemove.pop());
        }
        if (recheck) {
            for (List<Block> line: lines.values()) {
                int y = 1;
                for (Block block : line) {
                    block.posY = y;
                    y += SCALE;
                }
            }
            checkLines();
        }
    }

    private boolean isGameOver() {
        for (List<Block> line: lines.values()) {
            if (line.size() == 7) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
    }
}
