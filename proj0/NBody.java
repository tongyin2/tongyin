public class NBody {

  /** return radius of the universe of a given file */
  public static double readRadius(String filename){
    In in = new In(filename);
    int counter = in.readInt();
    double r = in.readDouble();
    in.close();
    return r;
  }

  /** return array of planets given the file*/
  public static Planet[] readPlanets(String filename){
    In in =  new In(filename);
    int counter = in.readInt();
    double rad = in.readDouble();

    Planet[] planets = new Planet[counter];

    int i;
    for(i = 0; i < counter; i++){
      double xPos = in.readDouble();
      double yPos = in.readDouble();
      double xVel = in.readDouble();
      double yVel = in.readDouble();
      double ma = in.readDouble();
      String imag = in.readString();

      planets[i] = new Planet(xPos, yPos, xVel, yVel, ma, imag);
    }
    in.close();
    return planets;
  }

  /** draw the initial universe state*/
  public static void main(String[] args) {
    /** collecting all needed inputs*/
    double T = Double.parseDouble(args[0]);
    double dt = Double.parseDouble(args[1]);

    String filename = args[2];

    double radius = readRadius(filename);
    Planet[] planets = readPlanets(filename);

    /** Drawing the Background*/
    StdDraw.setScale(-radius, radius);
    StdDraw.picture(0,0,"images/starfield.jpg");

    /**Drawing all of the planets*/
    int i;
    for(i = 0; i < planets.length; i++){
      planets[i].draw();
    }

    /**animation*/
    double tv = 0; //time variable
    StdAudio.loop("audio/2001.mid");

    while(tv < T){
      double[] xForces = new double[planets.length];
      double[] yForces = new double[planets.length];

      int n;
      for(n = 0; n < planets.length; n++){
        xForces[n] = planets[n].calcNetForceExertedByX(planets);
        yForces[n] = planets[n].calcNetForceExertedByY(planets);
      }

      for(n = 0; n < planets.length; n++){
        planets[n].update(dt, xForces[n], yForces[n]);
      }

      StdDraw.clear();
      StdDraw.picture(0,0,"images/starfield.jpg");
      for(n = 0; n < planets.length; n++){
        planets[n].draw();
      }
      StdDraw.show(10);
      tv = tv + dt;
    }
    StdAudio.close();
  }
}
