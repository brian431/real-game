package model;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TrackBullet extends Projectile {

    public String trackBulletImage = "trackbullet.png";

    TrackBullet(String type, double X, double Y, Point2D vector) {
        super(type, X, Y, vector);
        this.projectileImage = new ImageView(new Image(trackBulletImage));
        this.type = type;
        projectileImage.setTranslateX(X);
        projectileImage.setFitHeight(9);
        projectileImage.setFitWidth(30);
        projectileImage.setTranslateY(Y);
        if (vector.getY() < 0)
            projectileImage.setRotate(-vector.angle(vector.distance(0, 0), 0));
        else
            projectileImage.setRotate(vector.angle(vector.distance(0, 0), 0));
        this.vector = vector;

    }
}
