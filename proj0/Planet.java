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

  /** returns a force exerted on this planet by the given planet*/
  public double calcForceExertedBy(Planet p){
    double G = 6.67*Math.pow(10, -11);
    double r = this.calcDistance(p);
    return G*this.mass*p.mass/(r*r);
  }

  /** method return force exerted by another planet in x direction*/
  public double calcForceExertedByX(Planet p){
    double F = this.calcForceExertedBy(p);
    double dx = p.xxPos - this.xxPos;
    double r = this.calcDistance(p);
    return F*dx/r;
  }

  /** method returns force exerted by another planet in y direction*/
  public double calcForceExertedByY(Planet p){
    double F = this.calcForceExertedBy(p);
    double dy = p.yyPos - this.yyPos;
    double r = this.calcDistance(p);
    return F*dy/r;
  }

  /** return net X force exerted by all planets in the array input*/
  public double calcNetForceExertedByX(Planet[] ps){
    int i = 0;
    double netX = 0.0;
    while(i < ps.length){
      if(this.equals(ps[i]) == false){
        netX = netX + this.calcForceExertedByX(ps[i]);
      }
      i=i+1;
    }
    return netX;
  }

  /** return net Y force exerted by all planets in the array input*/
  public double calcNetForceExertedByY(Planet[] ps){
    int i = 0;
    double netY = 0.0;
    while(i < ps.length){
      if(this.equals(ps[i]) == false){
        netY = netY + this.calcForceExertedByY(ps[i]);
      }
      i = i+1;
    }
    return netY;
  }

  /** update the planet position and velocitys*/
  public void update(double dt, double netX, double netY){
    double ax = netX/this.mass;
    double ay = netY/this.mass;
    xxVel = xxVel + dt*ax;
    yyVel = yyVel + dt*ay;
    xxPos = xxPos + dt*xxVel;
    yyPos = yyPos + dt*yyVel;
  }

}
