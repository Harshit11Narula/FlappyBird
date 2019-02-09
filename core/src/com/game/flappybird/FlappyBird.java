package com.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] birds;
	int flapstate = 0;
	float birdy = 0 , birdx =0 , velocity = 0;
	int gamestate =0;
	float gravity = 2;
	float gap = 480;
	Texture toppipe , bottompipe;
	Random random;
	float offset ;
	BitmapFont font;
	Texture gameover1;
	int flaptiming = 0;
    int flagfirst = 0;
	float tubevelocity = 4;
	int numberoftubes = 4;
    float[] tubex = new float[numberoftubes];
    float[] pipeoffset = new float[numberoftubes];
	float distancebetweentubes;
	int score = 0;
	int scoreingtube = 0;

	Circle birdcircle ;
	//ShapeRenderer shapeRenderer ;
    public void startGame() {
        birdy = Gdx.graphics.getHeight()/2 - birds[flapstate].getHeight()/2;
        birdx = Gdx.graphics.getWidth()/2 - birds[flapstate].getWidth()/2;
        for(int iter =0; iter < numberoftubes;iter++){
            pipeoffset[iter] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubex[iter] = Gdx.graphics.getWidth()/2 - toppipe.getWidth()/2 + iter*distancebetweentubes + Gdx.graphics.getWidth();
        }
    }

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameover1 = new Texture("gameover.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        toppipe = new Texture("toptube.png");
        bottompipe = new Texture("bottomtube.png");
        offset = Gdx.graphics.getHeight() / 2- gap/2-100;
        random = new Random();
        distancebetweentubes = (3*Gdx.graphics.getWidth())/4 ;

        birdcircle = new Circle();
     //   shapeRenderer = new ShapeRenderer();


        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        startGame();

	}

    public static boolean intersect(Rectangle r, Circle c)
    {
        float cx = Math.abs(c.x - r.x - r.getWidth()/2);
        float xDist = r.getWidth()/2 + c.radius;
        if (cx > xDist)
            return false;
        float cy = Math.abs(c.y - r.y - r.getHeight()/2);
        float yDist = r.getHeight()/2 + c.radius;
        if (cy > yDist)
            return false;
        if (cx <= r.getWidth()/2 || cy <= r.getHeight()/2)
            return true;
        float xCornerDist = cx - r.getWidth()/2;
        float yCornerDist = cy - r.getHeight()/2;
        float xCornerDistSq = xCornerDist * xCornerDist;
        float yCornerDistSq = yCornerDist * yCornerDist;
        float maxCornerDistSq = c.radius * c.radius;
        return xCornerDistSq + yCornerDistSq <= maxCornerDistSq;
    }




	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(gamestate == 1) {
            if(tubex[scoreingtube] < Gdx.graphics.getWidth()){
                if(flagfirst==0) {
                    flagfirst = 1;

                }else {
                    score++;
                }
                scoreingtube++;
                scoreingtube%=numberoftubes;

            }

            if(Gdx.input.justTouched()) {
                velocity = -30;

            }

            for(int iter =0; iter < numberoftubes;iter++) {
                if(tubex[iter] < -toppipe.getWidth()) {

                    tubex[iter] += numberoftubes * distancebetweentubes;
                    pipeoffset[iter] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {

                    tubex[iter] = tubex[iter] - tubevelocity;


                }
                batch.draw(toppipe, tubex[iter], Gdx.graphics.getHeight() / 2 + gap / 2 + pipeoffset[iter]);
                batch.draw(bottompipe, tubex[iter], Gdx.graphics.getHeight() / 2 - gap / 2 - bottompipe.getHeight() + pipeoffset[iter]);


            }

            for(int iter = 0;iter<numberoftubes;iter++){
                if( intersect(new Rectangle(tubex[iter] , Gdx.graphics.getHeight() / 2 + gap / 2 + pipeoffset[iter] , toppipe.getWidth() , toppipe.getHeight()),birdcircle) || intersect(new Rectangle(tubex[iter] , Gdx.graphics.getHeight() / 2 - gap / 2 - bottompipe.getHeight() + pipeoffset[iter] , toppipe.getWidth() , toppipe.getHeight()) , birdcircle) ) {
                    gamestate =2;
                }
            }




            if(birdy>0 || velocity <0) {
                velocity = velocity + gravity;
                birdy -= velocity;
            }

            if(birdy < 0){
                gamestate = 2;
            }



        } else if (gamestate == 0){
            if(Gdx.input.justTouched()) {
                gamestate = 1;
            }

        } else {
            birdy = Gdx.graphics.getHeight()/2 - birds[flapstate].getHeight()/2;
            birdx = Gdx.graphics.getWidth()/2 - birds[flapstate].getWidth()/2;
            if(Gdx.input.justTouched()) {
                gamestate = 1;
                startGame();
                score = 0;
                scoreingtube = 0;
                velocity = 0;
                flagfirst = 0;
                flaptiming = 0;
            }
        }
        if( flaptiming%20 == 0)
            if (flapstate == 0) flapstate = 1;
            else flapstate = 0;
        flaptiming++;
        flaptiming%=10000;
        batch.draw(birds[flapstate], birdx, birdy);

        font.draw(batch , String.valueOf(score) , 100 , 200);

        birdcircle.set(Gdx.graphics.getWidth()/2 , birdy + birds[flapstate].getHeight()/2, birds[flapstate].getWidth()/2);


        batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
