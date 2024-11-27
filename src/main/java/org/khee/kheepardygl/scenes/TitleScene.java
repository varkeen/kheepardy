package org.khee.kheepardygl.scenes;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.particle.ParticleSystem;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import de.quippy.javamod.mixer.Mixer;
import de.quippy.javamod.multimedia.MultimediaContainer;
import de.quippy.javamod.multimedia.MultimediaContainerManager;
import de.quippy.javamod.multimedia.mod.ModContainer;
import de.quippy.javamod.system.Helpers;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class TitleScene extends SubScene implements Runnable {

  private Thread musicThread;
  private Mixer mixer;

  private Texture title;
  private Texture swirl;
  private ArrayList<Animation<?>> animations = new ArrayList<>();
  private ArrayList<ParticleSystem> particleSystems = new ArrayList<>();

  public TitleScene() {

    this.getContentRoot().setStyle("-fx-background-color: black");

//    Rectangle rectangle = new Rectangle();
//    rectangle.setWidth(FXGL.getAppWidth());
//    rectangle.setHeight(FXGL.getAppHeight());
//    rectangle.setFill(Color.BLACK);
//    this.getContentRoot().getChildren().add(rectangle);


//    for (String emojie : new String[] {"cocky", "ohno", "mad", "smirky", "cry"}) {
//
//      ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(50);
//
//      Texture t = FXGL.texture("player-" + emojie + ".png", 32.0, 32.0);
//
//      emitter.setSourceImage(t);
//      emitter.setSize(32, 32);
//      emitter.setEmissionRate(0.02);
//      emitter.setMaxEmissions(Integer.MAX_VALUE);
//      emitter.setNumParticles(3);
//      emitter.setVelocityFunction(i -> new Point2D(FXGL.random(-100, 100), FXGL.random(-100, 100)));
//      emitter.setExpireFunction(i -> Duration.seconds(FXGL.random(1, 2)));
//      emitter.setBlendMode(FXGL.getSettings().isNative() ? BlendMode.SRC_OVER : BlendMode.ADD);
//
//      ParticleSystem particleSystem = new ParticleSystem();
//      particleSystem.addParticleEmitter(
//          emitter, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);
//
//      this.particleSystems.add(particleSystem);
//
//      if (!FXGL.getSettings().isNative()) {
//        getContentRoot().getChildren().addAll(particleSystem.getPane());
//      }
//    }

    this.musicThread = new Thread(this);
    this.musicThread.start();

    Texture background = FXGL.texture("background.png", 1280, 800);
    this.title = FXGL.texture("kheepardy.png", 375.0, 63.0);
    this.title.setX(FXGL.getAppWidth() / 2.0 - this.title.getWidth() / 2.0);
    this.title.setY(FXGL.getAppHeight() / 2.0 - this.title.getHeight() / 2.0);
    this.swirl = FXGL.texture("kheepardy-swirl.png", 1600, 1600);
    this.swirl.setY(-400);
    this.swirl.setX(-160);
    this.swirl.setBlendMode(BlendMode.DIFFERENCE);


    this.getContentRoot().getChildren().add(background);
    this.getContentRoot().getChildren().addAll(this.swirl);
    this.getContentRoot().getChildren().addAll(this.title);

    this.playAnimation();
  }

  private void playAnimation() {

    Animation<?> animation;

    animation =
        FXGL.animationBuilder()
            .duration(Duration.seconds(6))
            .repeatInfinitely()
            .autoReverse(true)
            .rotate(this.title)
            .from(-5)
            .to(5)
            .build();

    animation.start();
    this.addListener(animation);

    animation =
        FXGL.animationBuilder()
            .duration(Duration.seconds(3))
            .repeatInfinitely()
            .autoReverse(true)
            .scale(this.title)
            .from(new Point2D(1.8, 1.8))
            .to(new Point2D(2.8, 2.8))
            .build();

    animation.start();
    this.addListener(animation);

    animation =
            FXGL.animationBuilder()
                    .duration(Duration.seconds(10))
                    .repeatInfinitely()
                    .rotate(this.swirl)
                    .from(0)
                    .to(359)
                    .build();

    animation.start();
    this.addListener(animation);

  }

  @Override
  protected void onUpdate(double tpf) {
//    this.animations.forEach(
//        animation -> {
//          animation.onUpdate(tpf);
//        });
//
//    this.particleSystems.forEach(
//        particleSystem -> {
//          particleSystem.onUpdate(tpf);
//        });
  }

  @Override
  public void onDestroy() {
    if (this.mixer != null) {
      this.mixer.stopPlayback();
    }
    super.onDestroy();
  }

  @Override
  public void run() {

    try {
      Helpers.registerAllClasses();
      Properties props = new Properties();
      props.setProperty(ModContainer.PROPERTY_PLAYER_ISP, "3");
      props.setProperty(ModContainer.PROPERTY_PLAYER_STEREO, "2");
      props.setProperty(ModContainer.PROPERTY_PLAYER_WIDESTEREOMIX, "0");
      props.setProperty(ModContainer.PROPERTY_PLAYER_NOISEREDUCTION, "0");
      props.setProperty(ModContainer.PROPERTY_PLAYER_NOLOOPS, "0");
      props.setProperty(ModContainer.PROPERTY_PLAYER_MEGABASS, "TRUE");
      props.setProperty(ModContainer.PROPERTY_PLAYER_BITSPERSAMPLE, "16");
      props.setProperty(ModContainer.PROPERTY_PLAYER_FREQUENCY, "48000");
      MultimediaContainerManager.configureContainer(props);
      URL modUrl = TitleScene.class.getResource("/assets/music/kheepardy_loop.it").toURI().toURL();
      MultimediaContainer multimediaContainer =
          MultimediaContainerManager.getMultimediaContainer(modUrl);
      this.mixer = multimediaContainer.createNewMixer();
      this.mixer.startPlayback();
    } catch (Exception e) {
      System.err.println("Error while running TitleScene: " + e.getMessage());
    }
  }
}
