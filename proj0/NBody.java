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
}
