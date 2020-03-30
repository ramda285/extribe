import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class EXtribe extends PApplet {

/*///////////////////////////////////////////////////////////
                        \u30dc\u30a4\u30c9\u30e9\u30a4\u30d5\u30b2\u30fc\u30e0
           \u500b\u4f53\u6570\u3092\u5897\u3084\u3057\u3066\u6575\u3068\u6226\u3044\u3001\u7fa4\u308c\u3092\u6210\u9577\u3055\u305b\u3066
           \u6b21\u306e\u968e\u3078\u9032\u3080\u30b2\u30fc\u30e0
           \u3080\u3057\u308d\u30e9\u30a4\u30d5\u30b2\u30fc\u30e0\u6210\u5206\u306f\u8fd1\u304f\u306b\u3044\u308b\u3068\u7523\u3080
           \u96e2\u308c\u308b\u3068\u6b7b\u306c\u7a0b\u5ea6\u3057\u304b\u306a\u3044\u305f\u3081
           \u304f\u308a\u30ad\u30f3\u6210\u5206\u3084flOw\u6210\u5206\u304c\u591a\u304f\u306a\u3063\u3066\u308b\u5e0c\u30ac\u30b9
           \u6700\u8fd1\u306b\u306a\u3063\u3066\u52c7\u306a\u307e\u6210\u5206\u3082\u52a0\u3048\u3088\u3046\u3068\u3057\u3066\u308b
           \u3082\u3046\u3053\u308a\u3083\u3060\u3081\u304b\u3082\u3057\u308c\u3093\u306d
*////////////////////////////////////////////////////////////
Scene scene;
boolean stop = false;
int mapx,mapy;  //\u30de\u30c3\u30d7\u6700\u5927\u5024
PVector camepos = new PVector (0,0), camevec = new PVector (0,0);  //\u30ab\u30e1\u30e9\u5909\u6570
int death_radius = 150 , born_radius = 50;  //\u6b7b\u4ea1\u7bc4\u56f2\u3068\u751f\u5b58\u7bc4\u56f2
PVector mouse;  //\u30dd\u30b8\u53d6\u5f97
boolean noticeplus = false;  //\u3042\u3093\u307e\u308a\u3064\u3051\u305f\u304f\u306a\u3044
PVector perdist;  //boid\u9054\u306e\u5e73\u5747\u4f4d\u7f6e
PImage war,kni,gar,sho,mag,teki;  //\u753b\u50cf
ArrayList<Boid> boids = new ArrayList<Boid>();
ArrayList<Effect> effects = new ArrayList<Effect>();
ArrayList<Enemy> enemys = new ArrayList<Enemy>();
ArrayList<Notice> notices = new ArrayList<Notice>();
ArrayList<Damage> damages = new ArrayList<Damage>();
ArrayList<Babble> babbles = new ArrayList<Babble>();
ArrayList<BGnumber> bgnumbers = new ArrayList<BGnumber>();
ArrayList<Explosion> explosions = new ArrayList<Explosion>();

public void setup() {
  //fullScreen();
  scene = new MainScene();
    //\u753b\u9762\u30b5\u30a4\u30ba
  frameRate(30);  //\u30d5\u30ec\u30fc\u30e0
  background(0);  //\u307e\u305a\u6700\u521d\u306b\u9ed2\u304f\u5857\u308a\u3064\u3076\u3059
  textSize(30);
  textFont(createFont("HiraKakuPro-W3", 30));
  war = loadImage("xchu.png");
  teki = loadImage("xchu.png");
    //\u305f\u304b\u304c\u30a8\u30d5\u30a7\u30af\u30c8\u306b\uff08\uff52\uff59
}

public void draw(){
  //Blackfade();  //\u30d5\u30a7\u30fc\u30c9\u30a2\u30a6\u30c8
  background(0);
  scene = scene.doScene();
}

//\u30d5\u30a7\u30fc\u30c9\u30a2\u30a6\u30c8
public void Blackfade(){
  noStroke();
  fill(0,150);
  rectMode(CORNER);
  rect(0, 0, width, height);
}

abstract class Scene{
  boolean first = true;
  Scene(){
  }
  public Scene doScene(){
    if (scene.first){
      firstScene();
      scene.first = false;
    }
    drawScene();
    return decideScene();
  }
  public abstract void firstScene();
  public abstract void drawScene();
  public abstract Scene decideScene();
  public abstract void mouseClicked();
  public abstract void keyPressed();
  public abstract void keyReleased();
}

class Downstair{
}

//list \u30c7\u30fc\u30bf\u69cb\u9020\u3000AllayList \u30b9\u30fc\u30d1\u30fc\u30af\u30e9\u30b9
class BGnumber{
  PVector tpos,pos;
  boolean flag = false;
  int num;
  int t;
  BGnumber(PVector _tpos){
    tpos = _tpos;
  }
  
  public void act(){
    check();
    move();
    draw();
    t++;
  }
  
  public void check(){
    if(t%5 == 0)
      num = (int)random(0,9);
    if(t >= 100)
      flag = true;
  }
  
  public void move(){
    tpos.y -= 2;
    pos = new PVector (tpos.x-camepos.x,tpos.y-camepos.y);
  }
  
  public void draw(){
    textSize(20);
    fill(50,255,50);
    text(""+num,pos.x,pos.y);
  }
}
class Babble{
  PVector tpos,pos;
  float sindou;
  boolean flag = false;
  Babble(PVector _tpos){
    tpos = _tpos;
  }
  
  public void act(){
    move();
    draw();
  }
  
  public void move(){
    sindou += 0.06f;
    tpos.y += 2*cos(sindou);
    pos = new PVector (tpos.x+width/2-camepos.x,tpos.y+height/2-camepos.y);
  }
  
  public void draw(){
    stroke(0,200,200);
    fill(100,255,255);
    ellipse(pos.x,pos.y,200,200);
    textSize(60);
    text("E X t r i b e",540,110);
  }
  
  public void explosion(){
    for (int i = 0; i < 10; i++) {
      boids.add(new Warrior(new PVector(random(-100,100),random(-100,100)),
      new PVector(random(1,5),random(1,5)),
      new PVector(random(6,7),random(1,2),random(4,8)),10,random(35,50)));  
      //\u521d\u671f\u30dc\u30a4\u30c9\u30a4\u30f3\u30b9\u30bf\u30f3\u30b9
    }
    for (int i = 0; i < 5; i++) {
      enemys.add(new Bear(new PVector(random(-500,500)+width/2,random(-500,500)+height/2)));
      //\u30a8\u30cd\u30df\u30fc\u914d\u7f6e
    }
    for (int i = 0; i < 10; i++){
      explosions.add(new Explosion(new PVector(tpos.x,tpos.y),new PVector(random(-3,3),random(-3,3))));
    }
    flag = true;
  }
}
//\u30dc\u30a4\u30c9\u306e\u30aa\u30d6\u30b8\u30a7\u30af\u30c8
abstract class Boid{
  PVector pos = new PVector(0,0),vec;  //\u4f4d\u7f6e\u3001\u901f\u5ea6
  PVector tpos;  //\u672c\u6765\u306e\u4f4d\u7f6e
  PImage jiki = war;
  float r,dr;  //\u534a\u5f84
  float maxv;  //\u6700\u9ad8\u901f\u5ea6
  float vision1,vision2,vision3=100,vision4=500,vision5=1000;  //\u4f5c\u7684\u7bc4\u56f2
  int baround,daround;
  //life
  int id,dt,dct,bt;  //deathtime,borntime
  float hp=1,hpmax;  //HP\u306e\u5024
  boolean flag,flag2;  //\u6b7b\u4ea1\u30d5\u30e9\u30b0
  boolean exing;  //\u52a0\u901f\u5ea6
  boolean finding;
  String shin;  //\u6b7b\u56e0
  Boid[] others ;
  //move
  PVector sacc, aacc, cacc, macc, eacc; //separate,alingment.cohesion,mouse,duplication
  int save = 0,aave = 0,cave = 0;
  boolean colf = false;
  PVector pow;  //x:\u5206\u6563\u3001y:\u96c6\u5408\u3001z:\u6574\u5217\u306e\u529b
  
  public void act(){
    reset();
    check();
    move();
    draw();
    //\u4f59\u88d5\u3092\u6301\u305f\u305b\u308b
  }
  
  //\u5468\u56de\u521d\u3081\u306e\u521d\u671f\u5316
  public void reset(){
    tint(255,255,255);
    sacc = new PVector(0,0);aacc = new PVector(0,0);cacc = new PVector(0,0);
    macc = new PVector(0,0);eacc = new PVector(0,0);
    save=0;aave=0;cave=0;
    maxv = 2;  //\u901f\u5ea6\u306f2
    vision2 = 25+boids.size()/2;
    born_radius = 50+boids.size()/2;
    baround = 0;
    daround = 0;
  } 
  
  //\u30dc\u30a4\u30c9\u306e\u4fee\u6b63\u3082\u308d\u3082\u308d
  //\u3060\u3051\u3060\u3063\u305f\u306e\u306b\u3001\u30d5\u30e9\u30b0\u7ba1\u7406\u3082\u3084\u308b\u3053\u3068\u306b\u306a\u3063\u305f\u4e0d\u61ab\u306a\u5b50
  public void check(){
    //HP\u306b\u3088\u308b\u6b7b
    if (hp <= 0){
      shin = "\u6226\u95d8";
      flag = true;
      for (int i = 0; i < 3; i++){
        explosions.add(new Explosion(new PVector(tpos.x,tpos.y),new PVector(random(-3,3),random(-3,3))));
      }
    }
    if (hp <= hpmax){
      hp += 0.05f;
    }
    hpmax = r+1;
    
    //\u30dc\u30a4\u30c9\u540c\u58eb\u306e\u5224\u5b9a
    for (int i = 0; i < boids.size(); i++) {
      Boid otherboid = boids.get(i);
      float distance = dist(tpos.x, tpos.y, otherboid.tpos.x, otherboid.tpos.y);
      vision1 = (r + otherboid.r)/2;
      if (this != otherboid) {
        
        //\u3053\u3053\u3089\u3078\u3093\u30d5\u30e9\u30b0\u7ba1\u7406
        if (distance <= born_radius && otherboid.r<40 && otherboid.r >= 10){
          baround++;
        }else if (distance <= death_radius){
          daround++;
        }
        
        //\u3053\u3053\u3089\u3078\u3093\u30dc\u30a4\u30c9\u7ba1\u7406  \u8ddd\u96e2\u306b\u5fdc\u3058\u3066
        if (distance < vision1){
          //\u885d\u7a81\u3059\u308b\u3068\u7e41\u6804\u3067\u304d\u306a\u3044
          duplication(distance, otherboid);
        }else if (distance < vision2) {
          separate(otherboid);
        }else if (distance > vision2 && distance < vision3) {
          cohesion(otherboid);
        }else if (distance > vision3 && distance < vision4) {
          alingment(otherboid);
        }  
      }
    }
    born(daround,baround);
    grow();
    
    //\u6575\u306e\u7ba1\u7406
    for (int i = 0; i < enemys.size(); i++){
      Enemy enemy = enemys.get(i);
      float distance = dist(tpos.x, tpos.y, enemy.tpos.x, enemy.tpos.y);
      if (distance < vision4){
        find(enemy,distance);
        text("!",pos.x+20,pos.y-20);
        finding = true;
      }
    }
    
    //effect\u306b\u3088\u308b\u5909\u5316
    for (int i = 0; i < effects.size(); i++){
      Effect effect = effects.get(i);
      if  (effect.click == true && dist(tpos.x, tpos.y, effect.tpos.x, effect.tpos.y) < vision5
      && dist(tpos.x, tpos.y, effect.tpos.x, effect.tpos.y) > vision1)
        mousemove(effects.size()-1);
    }
  }
  
  //\u885d\u7a81
  public void duplication(float distance, Boid other){
    tpos.x -= (((r + other.r)/2 - distance) / distance) * (other.tpos.x - tpos.x);
    tpos.y -= (((r + other.r)/2 - distance) / distance) * (other.tpos.y - tpos.y);
  }
  
  //\u5206\u6563
  public void separate(Boid boid){  
    PVector sent = new PVector (0,0);
    sent.add(boid.tpos);
    sent.sub(tpos);
    sent.normalize();
    sent.mult(pow.x);
    sacc.sub(sent);
    save++;
  }
  
  //\u96c6\u5408
  public void alingment(Boid boid){
    PVector sent = new PVector (0,0);
    sent.add(boid.tpos);
    sent.sub(tpos);
    sent.normalize();
    sent.mult(pow.y);
    aacc.add(sent);
    aave++;
  }
  
  //\u6574\u5217
  public void cohesion(Boid boid){
    PVector sent = boid.vec;
    sent.normalize();
    sent.mult(pow.z);
    cacc.add(sent);
    cave++;
  }
  
  public abstract void find(Enemy enemy, float distance);
  public void suffer(){
    exing = true;
  }
  
  //\u30de\u30a6\u30b9
  public void mousemove(int i){
    Effect effect = effects.get(i);
    PVector sent = new PVector (0,0);
    sent.add(effect.tpos);
    sent.sub(tpos);
    sent.normalize();
    sent.mult((255-effect.clit)*0.008f);
    maxv = 2 + 4 * (255-effect.clit)/255;
    if (finding && maxv < 4)
      maxv = 4;
    if (effect.anticlick == false)
      sent.mult(-1);
    macc.add(sent);
  }
  
  public void born(int i, int j){
    //dr\u3067\u6b7b\u306c
    if (r >= dr){
      flag2 = true;
      shin = "\u8001\u8870";
    }
    // \u8fd1\u304f\u306b\u4e09\u3064\u4ee5\u4e0a\u5b58\u5728\u3057\u306a\u3044\u5834\u5408\u3001\u6b7b\u4ea1\u30ab\u30a6\u30f3\u30c8\u958b\u59cb
    if (i < 3){
      dt++;
      if (dt > 150+r*3)
        flag2 = true;     
        shin = "\u5b64\u72ec";
    }else{
      dt = 0;
    } 
    //\u8fd1\u304f\u306b\u4e09\u3064\u4ee5\u4e0a\u5b58\u5728\u3001\u6700\u9ad8\u5b58\u5728\u500b\u4f53\u6570\u4ee5\u4e0b\u3001\u5341\u5206\u6210\u9577\u3067\u3001\u751f\u7523\u30ab\u30a6\u30f3\u30c8\u958b\u59cb
    if (j >= 3 && boids.size() <= 50 && r >= 5){
      bt++;
      if (bt > 400-r*3){
        boids.add(new Warrior(new PVector(tpos.x,tpos.y),new PVector(0,0),
        new PVector(random(6,7),random(1,2),random(4,8)),1,random(35,50)));
        bt = 0;
      }
    }else{
      bt = 0;
    }
    i = 0;
    j = 0;
  }
  
  //\u6210\u9577\u3059\u308b
  public void grow(){
    r += 0.03f;
  }
  
  //\u79fb\u52d5
  public void move(){
    if (colf == true){
      vec.add(eacc);
      colf = false;
    }else{
      if (save > 0){
        sacc.div(save);
      }else{sacc = new PVector (0,0);}
      if (aave > 0){
        aacc.div(aave);
      }else{aacc = new PVector (0,0);}
      if (cave > 0){
        cacc.div(cave);
      }else{cacc = new PVector (0,0);}
      vec.x += sacc.x + aacc.x + cacc.x + macc.x + eacc.x;
      vec.y += sacc.y + aacc.y + cacc.y + macc.y + eacc.y;
    }
    
    //\u4f55\u304c\u30ce\u30fc\u30de\u30e9\u30a4\u30ba\u3060\u30af\u30bd\u304c\uff01
    PVector pvec = vec;
    if (sqrt(vec.x*vec.x+vec.y*vec.y)>maxv){
      pvec.normalize();
      pvec.mult(maxv);
    }
    if (sqrt(vec.x*vec.x+vec.y*vec.y)>maxv+1){
      vec.normalize();
      vec.mult(maxv+1);
    }
    tpos.add(pvec);
    pos = new PVector (tpos.x+width/2-camepos.x,tpos.y+height/2-camepos.y);
    
    //\u58c1\u53cd\u5c04
    /*
    if (tpos.x - r <= -300) {
      tpos.x = -300;
      reflectX();
    }
    if (tpos.x + r >= 300) {
      tpos.x = 300;
      reflectX();
    }
    if (tpos.y - r <= -300) {
      tpos.y = -300;
      reflectY();
    }
    if (pos.y + r >= 300) {
      tpos.y = 300;
      reflectY();
    }*/
  }
  
  public void reflectX(){
    vec.x *= -1;
    sacc.x *= -1;
    aacc.x *= -1;
    cacc.x *= -1;
  }
  public void reflectY(){
    vec.y *= -1;
    sacc.y *= -1;
    aacc.y *= -1;
    cacc.y *= -1;
  }
  
  //\u4f5c\u753b
  public void draw(){
    death(); 
    //text(tpos.x+","+tpos.y,pos.x+20,pos.y-20);
    if (tpos.x - camepos.x < -width/2){
      if (tpos.y - camepos.y < -height/2){
        text("\u261c",5,25);
      }else if(tpos.y - camepos.y > height/2){
        text("\u261c",5,height-20);
      }else{
        text("\u261c",5,pos.y);
      }
    }else if(tpos.x - camepos.x > width/2){
      if (tpos.y - camepos.y < -height/2){
        text("\u261e",width-30,25);
      }else if(tpos.y - camepos.y > height/2){
        text("\u261e",width-30,height-20);
      }else{
        text("\u261e",width-30,pos.y);
      }
    }else if (tpos.y - camepos.y < -height/2){
      text("\u261d",pos.x,25);
    }else if(tpos.y - camepos.y > height/2){
      text("\u261f",pos.x,height-20);
    }else{
      image(jiki,pos.x-r/2,pos.y-r/2,r,r);
//      ellipse(pos.x,pos.y,r,r);
    }
  }
  
  public void death(){
    if (flag2 == true){
      dct += 10;
      tint(255,254-dct);
      if (dct >= 254)
        flag = true;
    }else{
      tint(255,254);
    }
  }
}

class Warrior extends Boid{
  PImage jiki = war;
  Warrior (PVector _tpos, PVector _vec, PVector _pow, float _r, float _dr){
      tpos = _tpos;
      vec = _vec;
      pow = _pow;
      r = _r;
      dr = _dr;
  }
  public void find(Enemy enemy, float distance){
    maxv = 4;
    if(enemy.active){
      if(distance < (r + enemy.r)/2){
        collision(enemy, distance);
      }else{
        attack(enemy);}
    }else{
      escape(enemy);
    }
  }
  public void collision(Enemy enemy, float distance){
    int damage = (int)random(3,5);
    tpos.x -= (((r + enemy.r)/2 - distance) / distance) * (enemy.tpos.x - tpos.x);
    tpos.y -= (((r + enemy.r)/2 - distance) / distance) * (enemy.tpos.y - tpos.y);
    reflectX();
    reflectY();
    damages.add(new Damage(pos, damage, damages.size()));
    enemy.hp -= damage;
  }
  
  public void attack(Enemy enemy){
    PVector sent = new PVector (0,0);
    sent.add(enemy.tpos);
    sent.add(enemy.vec);
    sent.sub(tpos);
    sent.normalize();
    sent.mult(2);
    eacc.add(sent);
  }
  
  //\u30d7\u30ed\u30b0\u30e9\u30e0\u30df\u30b9\u3063\u3066\u9060\u304f\u306e\u307b\u3046\u304c\u529b\u304c\u5f37\u3044
  public void escape(Enemy enemy){
    PVector sent = new PVector (0,0);
    sent.add(enemy.tpos);
    sent.sub(tpos);
    sent.normalize();
    sent.mult(-3);
    eacc.add(sent);
  }
}  
class Knight{
  public void attack(Enemy enemy){
    
  }
}
  
class Magician{
    
   public void attack(){
  }
}
class Damage{
  PVector tpos,pos;
  int damage;
  int id;
  boolean flag = false;
  float t=0,v=0;
  Damage(PVector _tpos, int _damage, int _id){
    tpos = new PVector(random(-10,10),random(-10,10)).add(_tpos);
    damage = _damage;
    id = _id;
  }
  
  public void act(){
    move();
    display();
    other();
    t++;
  }
  
  public void move(){
    if(t<30){
      v = (15 - t)/6;
      tpos.y -= v;
    }
    if(t>80)
      flag = true;
}
  
  public void display(){
    fill(255,255,0);
    text(damage,tpos.x,tpos.y);
  }
  
  public void other(){}
}
//\u305f\u304b\u304c\u30a8\u30d5\u30a7\u30af\u30c8\u3054\u3068\u304d\u3067\u30af\u30e9\u30b9\u3092\u4f7f\u3063\u3066\u3057\u307e\u3046\u81ea\u5206\u304c\u6068\u3081\u3057\u3044
class Effect{
  PVector pos,tpos;
  float clit = 0;  //\u6ce2\u7d0b\u306e\u534a\u5f84\u3001\u900f\u660e\u5ea6\u3001\u5f71\u97ff\u3092\u3072\u3068\u307e\u3068\u3081
  boolean click = false;  //flag
  boolean anticlick = false;  //\u767d\u304b\u8d64\u304b
  
  public void make(){
    clit += 2;
    if (clit >= 240){
      clit = 0;
      click = false;
    }
    noFill();
    if(anticlick){
      //\u9ed2\u304f\u3057\u3066\u308b\u3060\u3051\u3060\u304b\u3089\u3044\u305a\u308c\u30c6\u30b3\u5165\u308c\u304c\u5fc5\u8981
      stroke (255-clit,255-clit,255-clit);
    }else{
      stroke (255-clit,0,0);
    }
    pos.add(camevec);
    tpos = new PVector (pos.x-width/2+camepos.x,pos.y-height/2+camepos.y);
    strokeWeight(5);
    ellipse (pos.x,pos.y,clit,clit);
  }
}
class EndScene extends Scene{
  public void firstScene(){
  }
  public void drawScene(){
  }
  public Scene decideScene(){
    return this;
  }
  public void mouseClicked(){};
  public void keyPressed(){}
  public void keyReleased(){}
  
}
//\u6575\u306e\u4e88\u5b9a
abstract class Enemy{
  //\u8fd1\u304f\u304b\u3089\u76ee\u6a19\u5ea7\u6a19\u3092\u6c7a\u3081\u3066\u79fb\u52d5\u3059\u308b
  PVector pos=new PVector(0,0),vec=new PVector(0,0),dest;
  PVector tpos=new PVector(0,0);
  float r = 100; //\u5927\u304d\u3055
  float hp = 1500;  //\u30d2\u30c3\u30c8\u30dd\u30a4\u30f3\u30c8
  float maxv = 3;
  float evision1 = 350;  //\u8996\u754c\u7bc4\u56f2
  float charge = 60;
  int t;
  boolean attacking = false;  //\u653b\u6483\u30e2\u30fc\u30b7\u30e7\u30f3\u4e2d
  boolean find = false;  //boid\u5224\u5b9a
  boolean active = false;  //\u6ce8\u76ee\u3055\u308c\u3066\u3044\u308b\u304b\u3069\u3046\u304b
  boolean flag = false;  //\u6b7b\u4ea1\u30d5\u30e9\u30b0
  
  public void act(){
    reset();
    check();
    move();
    draw();
  }
  
  public void check(){
    //\u6b7b\u4ea1\u6642
    if (hp <= 0){
      flag = true;
      for (int i = 0; i < 10; i++){
        explosions.add(new Explosion(new PVector(tpos.x,tpos.y),new PVector(random(-3,3),random(-3,3))));
      }
    }
    //\u653b\u6483\u4e2d
    if (attacking){
      attack();
    //not\u653b\u6483\u4e2d
    }else{
      for (int i = 0; i < boids.size(); i++){
        Boid boid = boids.get(i);
        float distance = dist(tpos.x, tpos.y, boid.tpos.x, boid.tpos.y);
        if (distance < evision1){
          find = true;
        }
      }
      //\u6575\u3092\u898b\u3064\u3051\u308b
      if (find){
        actif();
      //\u3076\u3089\u3064\u304f
      }else{
        roam();
      }
    }
  }
  
  //\u3076\u3089\u3064\u304f
  public void roam(){
    if (dist(tpos.x, tpos.y, dest.x, dest.y)<100){
      dest = new PVector(random(tpos.x-1000,tpos.x+1000),random(tpos.y-1000,tpos.y+1000));
    }else{
      PVector sent = new PVector (0,0);
      sent.add(dest);
      sent.sub(tpos);
      sent.normalize();
      vec.add(sent);
    }
  }
  
  public abstract void actif();
  public abstract void attack();
  
  //\u79fb\u52d5\u53cd\u6620
  public void move(){
    if (sqrt(vec.x*vec.x+vec.y*vec.y)>maxv && attacking == false){
      vec.normalize();
      vec.mult(maxv);
    }
    tpos.add(vec);
    pos = new PVector (tpos.x+width/2-camepos.x,tpos.y+height/2-camepos.y);
  }
  
  //\u4f5c\u753b
  public void draw(){
    for (int i = 0; i < effects.size(); i++){
      Effect effect = effects.get(i);
      float distance = dist(tpos.x+100, tpos.y+100, effect.tpos.x, effect.tpos.y);
    }
    if (tpos.x - camepos.x < -width/2){
      if (tpos.y - camepos.y < -height/2){
        text("\u261c",5,25);
      }else if(tpos.y - camepos.y > height/2){
        text("\u261c",5,height-20);
      }else{
        text("\u261c",5,pos.y);
      }
    }else if(tpos.x - camepos.x > width/2){
      if (tpos.y - camepos.y < -height/2){
        text("\u261e",width-30,25);
      }else if(tpos.y - camepos.y > height/2){
        text("\u261e",width-30,height-20);
      }else{
        text("\u261e",width-30,pos.y);
      }
    }else if(tpos.y - camepos.y < -height/2){
      text("\u261d",pos.x,25);
    }else if(tpos.y - camepos.y > height/2){
      text("\u261f",pos.x,height-20);
    }else{
      image(teki,pos.x-r/2,pos.y-r/2,r,r);
//      ellipse(pos.x,pos.y,r,r);
    }
  }
  
  //\u30a2\u30af\u30c6\u30a3\u30d6\u30e2\u30fc\u30c9
  public void active(){
    tint(255,100,100);
  }
  
  //\u521d\u671f\u524a\u9664
  public void reset(){
    tint(255,255,255);
    find = false;
    textSize(30);
    if (active)
      active();
//    vec = new PVector (0,0,0);
  }
}

//\u4e00\u756a\u30ce\u30fc\u30de\u30eb\u306a\u6575
class Bear extends Enemy{
  float hp = 150;
  int attackvar = 0;
  PVector forward;
  Bear(PVector _tpos){
    tpos = _tpos;
    dest = tpos;
  }
  
  public void actif(){
    text("\u203c",pos.x+50,pos.y-50);
//    PVector distance = new PVector(tpos.x-perdist.x,tpos.y-perdist.y);
    float distance = abs(dist(tpos.x,tpos.y,perdist.x,perdist.y));
    charge++;
    if (distance<100){
      if(charge >= 100){
        charge = random(0,50);
        attacking = true;
        attackvar = 0;
      }
    }else if(distance<300){
      if(charge >= 150){
        charge = random(30,100);
        attacking = true;
        attackvar = 1;
      }
    }else{
      dash();
    }
  }
  
  public void attack(){
    if (attackvar == 0)
      attack_S();
    if (attackvar == 1)
      attack_T();
  }
  
  public void attack_S(){
    vec = new PVector(0,0);
    t++;
    if (t==20 || t==25){
      tint(0);
    }else if (t>=60 && t<100){
      strokeWeight(30);
      line(pos.x,pos.y,pos.x+150*cos(t*0.2f),pos.y+150*sin(t*0.2f));
      line(pos.x,pos.y,pos.x-150*cos(t*0.2f),pos.y-150*sin(t*0.2f));
      for (int i=0; i<boids.size(); i++){
        Boid boid = boids.get(i);
        float linetoboid  = abs(sin(t*0.2f)*(pos.x-boid.pos.x)-cos(t*0.2f)*(pos.y-boid.pos.y));
        float distance = dist(pos.x,pos.y,boid.pos.x,boid.pos.y);
        if (linetoboid < boid.r && distance < boid.r+150){
          boid.hp -= 20;
          boid.suffer();
        }
      }
    }else if (t>=130){
      attacking = false;
      t=0;
    }
  }
  
  public void attack_T(){
    vec = new PVector(0,0);
    t++;
    if (t==20 || t==25){
      tint(0);
    }else if (t==30){
      forward = new PVector(tpos.x-perdist.x,tpos.y-perdist.y);
      forward.normalize();
      forward.mult(-30);
    }else if (t>=60 && t<120){
      forward.mult(0.95f);
      vec = forward;
      for (int i=0; i<boids.size(); i++){
        Boid boid = boids.get(i);
        float distance = dist(pos.x,pos.y,boid.pos.x,boid.pos.y);
        if (distance < (r + boid.r)/2){
          boid.hp -= 15;
          boid.suffer();
        }
      }
    }else if (t>=120){
      attacking = false;
      t=0;
    }
  }
  
  public void dash(){
    PVector sent = new PVector (0,0);
    sent.add(perdist);
    sent.sub(tpos);
    sent.normalize();
    vec.add(sent);
  }
  
}
class Explosion{
  PVector pos,vec,tpos;
  int t;
  boolean flag;
  Explosion(PVector _tpos, PVector _vec){
    tpos = _tpos;
    vec = _vec;
  }
  
  public void act(){
    t++;
    if (t == 100)
      flag = true;
    tpos.add(vec);
    strokeWeight(3);
    fill(100,255,255);
    stroke(0,200,200);
    pos = new PVector (tpos.x+width/2-camepos.x,tpos.y+height/2-camepos.y);
    ellipse(pos.x,pos.y,30,30);
  }
}
class MainScene extends Scene{
  int t = 0;
  public void firstScene(){
    babbles.add(new Babble(new PVector(0,0)));
  }
  
  public void drawScene() {
    if (stop == true)
      return;
    //text("number\u306f"+boids.size(),width/2,height/2+40);
    //\u3081\u3093\u3069\u304f\u3055\u3044\u3053\u3068\u306b\u30de\u30a6\u30b9\u3068\u30ad\u30fc\u306e\u8a2d\u5b9a
    //\u3053\u3053\u306f\u5f8c\u3067\u76f4\u3057\u305f\u3044\u5019\u88dc\u30ca\u30f3\u30d0\u30fc\u30ef\u30f3
    if (mousePressed){
      mouseClicked();
      mousePressed = false;
    }
    if (keyPressed){
      keyPressed();
    }else{
      keyReleased();
    }
    
    //\u30ab\u30e1\u30e9\u306e\u30bf\u30fc\u30f3
    camepos.sub(camevec);
    
    //bgnumber\u306e\u30bf\u30fc\u30f3
    for (int i = 0; i < bgnumbers.size(); i++){
      BGnumber bgnumber = bgnumbers.get(i);
      if (bgnumber.flag == true){
        bgnumbers.remove(i);
      }else{bgnumber.act();}
    }
    if(t >= 5){
      bgnumbers.add(new BGnumber(new PVector (random(0,width)+camepos.x,random(50,height+50)+camepos.y)));
      t = 0;
    }
    
    //babble\u306e\u30bf\u30fc\u30f3
    for (int i = 0; i < babbles.size(); i++) {
      Babble babble = babbles.get(i);
      if (babble.flag == true){
        babbles.remove(i);
      }else{babble.act();}
    }
    
    //notice\u306e\u30bf\u30fc\u30f3
    /*  \u306f\u306a\u304b\u3063\u305f\u3053\u3068\u306b
    for (int i = 0; i < notices.size(); i++) {
      Notice notice = notices.get(i);
      notice.act();
      if (noticeplus == true){
        notice.num++;        
        notice.t = 300;
      }
      if (notice.flag)
        notices.remove(notices.size()-1);
        notice.flag = false;
    }
    noticeplus = false;
    */
    
    //damage\u306e\u30bf\u30fc\u30f3
    for (int i = 0; i < damages.size(); i++) {
      Damage damage = damages.get(i);
      if (damage.flag == true){
        damages.remove(i);
      }else{damage.act();}
    }
   
   //effect\u306e\u30bf\u30fc\u30f3
    for (int i = 0; i < effects.size(); i++) {
      Effect effect = effects.get(i);
      if (effect.click){
        effect.make();
      }else{
        effects.remove(i);
      }
    }
      
    //boids\u306e\u30bf\u30fc\u30f3
    perdist = new PVector(0,0);
    for (int i = 0; i < boids.size(); i++){
      Boid boid = boids.get(i);
      if (boid.flag == false){  //\u6b7b\u4ea1\u30d5\u30e9\u30b0
        boid.act();
      }else{
        //notices.add(new Notice(boid.r,boid.jiki,boid.shin));
        //noticeplus = true;
        boids.remove(i);
      }
      perdist.add(boid.tpos);
      stroke(255,255,0);
    }
    perdist.div(boids.size());
    
    //enemy\u306e\u30bf\u30fc\u30f3
    for (int i = 0; i < enemys.size(); i++){
      Enemy enemy = enemys.get(i);
      if (enemy.flag == false){
        enemy.act();
      }else{
        enemys.remove(i);
      }
    }
    
    //explosion\u306e\u30bf\u30fc\u30f3
    for (int i = 0; i < explosions.size(); i++){
      Explosion explosion = explosions.get(i);
      if (explosion.flag == false){
        explosion.act();
      }else{
        explosions.remove(i);
      }
    }
    
    //\u30ab\u30e1\u30e9\u306b\u3088\u308b\u79fb\u52d5\u306f\u4e00\u6c17\u306b\u884c\u3046
    for (int i = 0; i < boids.size(); i++){
      Boid boid = boids.get(i);
      boid.pos.add(camevec);
    }
    for (int i = 0; i < enemys.size(); i++){
      Enemy enemy = enemys.get(i);
      enemy.pos.add(camevec);
    }
    
    //\u30b2\u30fc\u30e0\u30aa\u30fc\u30d0\u30fc\u5224\u5b9a
    textSize(15);
    text(camepos.x+""+camepos.y,width/2-20,height/2);
    if (boids.size()+babbles.size() == 0){
      fill(255,0,0);
      textSize(36);
      text("\u6804\u67af\u76db\u8870",width/2-50,height/2+50);
    }
    if (enemys.size()+babbles.size() == 0){
      fill(255,0,0);
      textSize(36);
      text("\u5929\u4e0b\u7d71\u4e00",width/2-50,height/2+50);
    }
    t++;
  }
  
  public Scene decideScene(){
    return this;
  }
  
  //\u30af\u30ea\u30c3\u30af\u6642\u306bEffect\u306e\u30d5\u30e9\u30b0\u3092ON
  public void mouseClicked(){
    boolean effective = false;
    for(int i = 0; i < babbles.size(); i++){
      Babble babble = babbles.get(i);
      if(dist(mouseX,mouseY,babble.pos.x,babble.pos.y)<100)
        babble.explosion();
        effective = true;
    }
    for (int j = 0; j < enemys.size(); j++){
      Enemy enemy = enemys.get(j);
      if (dist(mouseX, mouseY, enemy.pos.x, enemy.pos.y)<enemy.r){
        if (mouseButton == LEFT){
          enemy.active = true;
        }else if (mouseButton == RIGHT){enemy.active = false;}
        effective = true;
      }
    }
    if(effective == false){
      effects.add(new Effect());
      Effect effect = effects.get(effects.size()-1);
      effect.click = true;
      effect.pos = new PVector (mouseX,mouseY);
      if (mouseButton == LEFT){
        effect.anticlick = true;
      }else if (mouseButton == RIGHT){
        effect.anticlick = false;
      }
    }
  }
  
  public void keyPressed() {
    if (key == CODED) {      // \u30b3\u30fc\u30c9\u5316\u3055\u308c\u3066\u3044\u308b\u30ad\u30fc\u304c\u62bc\u3055\u308c\u305f
      if (keyCode == RIGHT) {    // \u30ad\u30fc\u30b3\u30fc\u30c9\u3092\u5224\u5b9a
        camevec.x = -10;
      }else if (keyCode == LEFT) {
        camevec.x = 10;
      }else if (keyCode == UP) {
        camevec.y = 10;
      }else if (keyCode == DOWN) {
        camevec.y = -10;
      }else if (keyCode == BACKSPACE) {
        stop = true;
      }else if (keyCode == SHIFT){
        camepos = perdist;
      }
    }
  }
  
  public void keyReleased(){
    camevec.x = 0;
    camevec.y = 0;
  }
}
class Notice{
  //\u5de6\u4e0a\u306b\u30ad\u30e3\u30e9\u306e\u753b\u50cf\u3068\u6b7b\u56e0\u3092\u66f8\u304f\u6a5f\u80fd
  int num;
  float size;
  PImage graph;
  String shin;
  boolean flag;
  int t;
  Notice(float _size, PImage _graph, String _shin){
    size = _size;
    graph = _graph;
    shin = _shin;
  }
  public void act(){
    image(graph,0,60*num);
    text("\u306f"+shin+"\u306b\u3088\u308a\u6b7b\u306b\u307e\u3057\u305f"+num,30,25+60*num);
    t--;
    if (t <= 0)
      flag = true;
  }
  
}
class TitleScene extends Scene{
  public void firstScene(){
  }
  public void drawScene(){
  }
  public Scene decideScene(){
    return this;
  }
  public void mouseClicked(){};
  public void keyPressed(){}
  public void keyReleased(){}
  
}
  public void settings() {  size(1400, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "EXtribe" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
