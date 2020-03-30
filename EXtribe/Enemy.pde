//敵の予定
abstract class Enemy{
  //近くから目標座標を決めて移動する
  PVector pos=new PVector(0,0),vec=new PVector(0,0),dest;
  PVector tpos=new PVector(0,0);
  float r = 100; //大きさ
  float hp = 1500;  //ヒットポイント
  float maxv = 3;
  float evision1 = 350;  //視界範囲
  float charge = 60;
  int t;
  boolean attacking = false;  //攻撃モーション中
  boolean find = false;  //boid判定
  boolean active = false;  //注目されているかどうか
  boolean flag = false;  //死亡フラグ
  
  void act(){
    reset();
    check();
    move();
    draw();
  }
  
  void check(){
    //死亡時
    if (hp <= 0){
      flag = true;
      for (int i = 0; i < 10; i++){
        explosions.add(new Explosion(new PVector(tpos.x,tpos.y),new PVector(random(-3,3),random(-3,3))));
      }
    }
    //攻撃中
    if (attacking){
      attack();
    //not攻撃中
    }else{
      for (int i = 0; i < boids.size(); i++){
        Boid boid = boids.get(i);
        float distance = dist(tpos.x, tpos.y, boid.tpos.x, boid.tpos.y);
        if (distance < evision1){
          find = true;
        }
      }
      //敵を見つける
      if (find){
        actif();
      //ぶらつく
      }else{
        roam();
      }
    }
  }
  
  //ぶらつく
  void roam(){
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
  
  abstract void actif();
  abstract void attack();
  
  //移動反映
  void move(){
    if (sqrt(vec.x*vec.x+vec.y*vec.y)>maxv && attacking == false){
      vec.normalize();
      vec.mult(maxv);
    }
    tpos.add(vec);
    pos = new PVector (tpos.x+width/2-camepos.x,tpos.y+height/2-camepos.y);
  }
  
  //作画
  void draw(){
    for (int i = 0; i < effects.size(); i++){
      Effect effect = effects.get(i);
      float distance = dist(tpos.x+100, tpos.y+100, effect.tpos.x, effect.tpos.y);
    }
    if (tpos.x - camepos.x < -width/2){
      if (tpos.y - camepos.y < -height/2){
        text("☜",5,25);
      }else if(tpos.y - camepos.y > height/2){
        text("☜",5,height-20);
      }else{
        text("☜",5,pos.y);
      }
    }else if(tpos.x - camepos.x > width/2){
      if (tpos.y - camepos.y < -height/2){
        text("☞",width-30,25);
      }else if(tpos.y - camepos.y > height/2){
        text("☞",width-30,height-20);
      }else{
        text("☞",width-30,pos.y);
      }
    }else if(tpos.y - camepos.y < -height/2){
      text("☝",pos.x,25);
    }else if(tpos.y - camepos.y > height/2){
      text("☟",pos.x,height-20);
    }else{
      image(teki,pos.x-r/2,pos.y-r/2,r,r);
//      ellipse(pos.x,pos.y,r,r);
    }
  }
  
  //アクティブモード
  void active(){
    tint(255,100,100);
  }
  
  //初期削除
  void reset(){
    tint(255,255,255);
    find = false;
    textSize(30);
    if (active)
      active();
//    vec = new PVector (0,0,0);
  }
}

//一番ノーマルな敵
class Bear extends Enemy{
  float hp = 150;
  int attackvar = 0;
  PVector forward;
  Bear(PVector _tpos){
    tpos = _tpos;
    dest = tpos;
  }
  
  void actif(){
    text("‼",pos.x+50,pos.y-50);
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
  
  void attack(){
    if (attackvar == 0)
      attack_S();
    if (attackvar == 1)
      attack_T();
  }
  
  void attack_S(){
    vec = new PVector(0,0);
    t++;
    if (t==20 || t==25){
      tint(0);
    }else if (t>=60 && t<100){
      strokeWeight(30);
      line(pos.x,pos.y,pos.x+150*cos(t*0.2),pos.y+150*sin(t*0.2));
      line(pos.x,pos.y,pos.x-150*cos(t*0.2),pos.y-150*sin(t*0.2));
      for (int i=0; i<boids.size(); i++){
        Boid boid = boids.get(i);
        float linetoboid  = abs(sin(t*0.2)*(pos.x-boid.pos.x)-cos(t*0.2)*(pos.y-boid.pos.y));
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
  
  void attack_T(){
    vec = new PVector(0,0);
    t++;
    if (t==20 || t==25){
      tint(0);
    }else if (t==30){
      forward = new PVector(tpos.x-perdist.x,tpos.y-perdist.y);
      forward.normalize();
      forward.mult(-30);
    }else if (t>=60 && t<120){
      forward.mult(0.95);
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
  
  void dash(){
    PVector sent = new PVector (0,0);
    sent.add(perdist);
    sent.sub(tpos);
    sent.normalize();
    vec.add(sent);
  }
  
}
