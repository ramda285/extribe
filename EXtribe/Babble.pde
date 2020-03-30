class Babble{
  PVector tpos,pos;
  float sindou;
  boolean flag = false;
  Babble(PVector _tpos){
    tpos = _tpos;
  }
  
  void act(){
    move();
    draw();
  }
  
  void move(){
    sindou += 0.06;
    tpos.y += 2*cos(sindou);
    pos = new PVector (tpos.x+width/2-camepos.x,tpos.y+height/2-camepos.y);
  }
  
  void draw(){
    stroke(0,200,200);
    fill(100,255,255);
    ellipse(pos.x,pos.y,200,200);
    textSize(60);
    text("E X t r i b e",540,110);
  }
  
  void explosion(){
    for (int i = 0; i < 10; i++) {
      boids.add(new Warrior(new PVector(random(-100,100),random(-100,100)),
      new PVector(random(1,5),random(1,5)),
      new PVector(random(6,7),random(1,2),random(4,8)),10,random(35,50)));  
      //初期ボイドインスタンス
    }
    for (int i = 0; i < 5; i++) {
      enemys.add(new Bear(new PVector(random(-500,500)+width/2,random(-500,500)+height/2)));
      //エネミー配置
    }
    for (int i = 0; i < 10; i++){
      explosions.add(new Explosion(new PVector(tpos.x,tpos.y),new PVector(random(-3,3),random(-3,3))));
    }
    flag = true;
  }
}
