package model;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import scenes.Level;

import static model.Main.projectiles;

public class Player {

    public Image facingRightImage = new Image("/cuphead.png");
    public Image facingLeftImage = new Image("/cupheadFacingLeft.png");

    public int hp = 3;
    public double playerWidth = 95.45;
    public double playerHeight = 150;
    public int horizontalSpeed = 13;
    public int verticalSpeed = 7;
    public int jumpHeight = 38;
    public int dashLength = 330;
    public int dashDuration = 120;
    public int dashLoop = 0;

    public boolean canJump = false;
    public boolean canShoot = true;
    public boolean facingRight = true;
    public boolean canDash = true;
    public boolean invincible = false;

    public Timeline dashCd;
    public Timeline shootCd;
    public ImageView playerImageView;

    public Level myLevel;

    public Player() {

        playerImageView = new ImageView(facingRightImage);
        playerImageView.setFitHeight(playerHeight);
        playerImageView.setFitWidth(playerWidth);
        playerImageView.setPreserveRatio(true);
    }

    public void movePlayer() {

        if (verticalSpeed < 30) verticalSpeed += 2;
        moveY();

        // Key A
        if (Main.KeyCodes.getOrDefault(KeyCode.A, false) && !Main.KeyCodes.getOrDefault(KeyCode.W, false)) {
            facingRight = false;
            playerImageView.setImage(facingLeftImage);
            if (playerImageView.getTranslateX() > horizontalSpeed) {
                playerImageView.setTranslateX(playerImageView.getTranslateX() - horizontalSpeed);
            }
            if (!canJump && playerImageView.getTranslateX() > 3) {
                //playerImageView.setTranslateX(playerImageView.getTranslateX() - 3);
            }
        }

        // Key D
        if (Main.KeyCodes.getOrDefault(KeyCode.D, false) && !Main.KeyCodes.getOrDefault(KeyCode.W, false)) {
            facingRight = true;
            playerImageView.setImage(facingRightImage);
            if (playerImageView.getTranslateX() + playerImageView.getFitWidth() < Level.WIDTH - horizontalSpeed) {
                playerImageView.setTranslateX(playerImageView.getTranslateX() + horizontalSpeed);
            }
            if (!canJump && playerImageView.getTranslateX() + playerImageView.getFitWidth() < Level.WIDTH - 3) {
                //playerImageView.setTranslateX(playerImageView.getTranslateX() + 3);
            }
        }

        // Key Space
        if (Main.KeyCodes.getOrDefault(KeyCode.SPACE, false) && canJump) {
            verticalSpeed = -jumpHeight;
            canJump = false;
        }

        // Key Shift
        if (Main.KeyCodes.getOrDefault(KeyCode.SHIFT, false)) {
            if (canDash && (canJump || (!canJump && verticalSpeed > 0))) dash();
        }

        // Key J
        if (Main.KeyCodes.getOrDefault(KeyCode.J, false)) {
            if (canShoot) shoot();
        }
    }

    public void moveY() {

        for (int i = 0; i < Math.abs(verticalSpeed); ++i) {
            for (Node standable : Main.standables) {
                if (playerImageView.getTranslateY() + playerImageView.getFitHeight() == standable.getTranslateY() && verticalSpeed > 0) {
                    canJump = true;
                    return;
                }
            }
            playerImageView.setTranslateY(playerImageView.getTranslateY() + (verticalSpeed > 0 ? 1 : -1));
        }
    }


    public void startDashCd() {

        dashCd = new Timeline(new KeyFrame(Duration.millis(600), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                canDash = true;
            }
        }));
        dashCd.setCycleCount(1);
        dashCd.play();
    }


    public void dash() {
        if (canDash) {
            canDash = false;
            boolean tempFace = facingRight;
            int toWall = (int) (tempFace ? (Level.WIDTH - playerImageView.getTranslateX() - playerWidth) : playerImageView.getTranslateX());
            toWall = Math.min(toWall, dashLength);

            TranslateTransition tt = new TranslateTransition(Duration.millis(dashDuration * toWall / dashLength), playerImageView);
            tt.setByX(tempFace ? toWall : -toWall);
            tt.setInterpolator(Interpolator.LINEAR);
            tt.play();

            startDashCd();
        }

    }


    public void shoot() {
        /** Shoot one bullet and start a timer */
        canShoot = false;
        projectiles.add(new Projectile("playerBullet", facingRight ? playerImageView.getTranslateX() + playerWidth : playerImageView.getTranslateX(), playerImageView.getTranslateY() + playerHeight / 2, facingRight, Main.KeyCodes.getOrDefault(KeyCode.W, false)));
        shootCd = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                canShoot = true;
            }
        }));
        shootCd.play();

    }

    public void detectBadThings() {
        /**
         * check if player hits projectiles or the boss
         *  if hit the boss, start invincible timer
         */

        for (int i = 0; i < Main.projectiles.size(); ++i) {
            if (Main.projectiles.get(i).projectileImage.getBoundsInParent().intersects(playerImageView.getBoundsInParent()) && Main.projectiles.get(i).type.equals("bossBullet")) {
                myLevel.rootPane.getChildren().remove(Main.projectiles.get(i).projectileImage);
                Main.projectiles.remove(i);
                myLevel.healthes.getChildren().remove(myLevel.healthes.getChildren().size() - 1);
            }
        }

        if (playerImageView.getBoundsInParent().intersects(myLevel.bossHitbox.getBoundsInParent()) && !invincible) {
            myLevel.healthes.getChildren().remove(myLevel.healthes.getChildren().size() - 1);
            invincible = true;
            Timeline invincibleTime = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    invincible = false;
                }
            }));
            invincibleTime.play();
        }
    }

    public void update() {
        detectBadThings();
        movePlayer();
    }
}
