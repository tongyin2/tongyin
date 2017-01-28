public class Planet {
  public double xxPos; // current x position
  public double yyPos; //current y position
  public double xxVel; //current velocity in the x direction
  public double yyVel; //current velocity in the y direction
  public double mass; //its mass
  public String imgFileName; // the name of an image in the images directory that depicts the Planet

  public Planet(double xP, double yP, double xV,
                double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
  }

  public Planet(Planet p){
    xxPos = p.xxPos;
    yyPos = p.yyPos;
    xxVel = p.xxVel;
    yyVel = p.yyVel;
    mass = p.mass;
    imgFileName = p.imgFileName;
  }

  /** calculate and return the distance between this planet and input planet*/
  public double calcDistance(Planet p){
    double dx = p.xxPos - this.xxPos;
    double dy = p.yyPos - this.yyPos;
    return Math.sqrt(dx*dx+dy*dy);
  }
  /** method return force exerted by another planet in x direction
  public double calcForceExertedByX(Planet p){
    return 0.0;
  }

  /** method returns force exerted by another planet in y direction
  public double calcForceExertedByY(Planet p){
    return 0.0;
  }*/

}
