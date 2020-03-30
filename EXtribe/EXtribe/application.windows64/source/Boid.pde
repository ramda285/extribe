//ボイドのオブジェクト
abstract class Boid{
  PVector pos = new PVector(0,0),vec;  //位置、速度
  PVector tpos;  //本来の位置
  PImage jiki = war;
  float r,dr;  //半径
  float maxv;  //最高速度
  float vision1,vision2,vision3=100,vision4=500,vision5=1000;  //作的範囲
  int baround,daround;
  //life
  int id,dt,dct,bt;  //deathtime,borntime
  float hp=1,hpmax;  //HPの値
  boolean flag,flag2;  //死亡フラグ
  boolean exing;  //加速度
  boolean finding;
  String shin;  //死因
  Boid[] others ;
  //move
  PVector sacc, aacc, cacc, macc, eacc; //separate,alingment.cohesion,mouse,duplication
  int save = 0,aave = 0,cave = 0;
  boolean colf = false;
  PVector pow;  //x:分散、y:集合、z:整列の力
  
  void act(){
    reset();
    check();
    move();
    draw();
    //余裕を持たせる
  }
  
  //周回初めの初期化
  void reset(){
    tint(255,255,255);
    sacc = new PVector(0,0);aacc = new PVector(0,0);cacc = new PVector(0,0);
    macc = new PVector(0,0);eacc = new PVector(0,0);
    save=0;aave=0;cave=0;
    maxv = 2;  //速度は2
    vision2 = 25+boids.size()/2;
    born_radius = 50+boids.size()/2;
    baround = 0;
    daround = 0;
  } 
  
  //ボイドの修正もろもろ
  //だけだったのに、フラグ管理もやることになった不憫な子
  void check(){
    //HPによる死
    if (hp <= 0){
      shin = "戦闘";
      flag = true;
      for (int i = 0; i < 3; i++){
        explosions.add(new Explosion(new PVector(tpos.x,tpos.y),new PVector(random(-3,3),random(-3,3))));
      }
    }
    if (hp <= hpmax){
      hp += 0.05;
    }
    hpmax = r+1;
    
    //ボイド同士の判定
    for (int i = 0; i < boids.size(); i++) {
      Boid otherboid = boids.get(i);
      float distance = dist(tpos.x, tpos.y, otherboid.tpos.x, otherboid.tpos.y);
      vision1 = (r + otherboid.r)/2;
      if (this != otherboid) {
        
        //ここらへんフラグ管理
        if (distance <= born_radius && otherboid.r<40 && otherboid.r >= 10){
          baround++;
        }else if (distance <= death_radius){
          daround++;
        }
        
        //ここらへんボイド管理  距離に応じて
        if (distance < vision1){
          //衝突すると繁栄できない
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
    
    //敵の管理
    for (int i = 0; i < enemys.size(); i++){
      Enemy enemy = enemys.get(i);
      float distance = dist(tpos.x, tpos.y, enemy.tpos.x, enemy.tpos.y);
      if (distance < vision4){
        find(enemy,distance);
        text("!",pos.x+20,pos.y-20);
        finding = true;
      }
    }
    
    //effectによる変化
    for (int i = 0; i < effects.size(); i++){
      Effect effect = effects.get(i);
      if  (effect.click == true && dist(tpos.x, tpos.y, effect.tpos.x, effect.tpos.y) < vision5
      && dist(tpos.x, tpos.y, effect.tpos.x, effect.tpos.y) > vision1)
        mousemove(effects.size()-1);
    }
  }
  
  //衝突
  void duplication(float distance, Boid other){
    tpos.x -= (((r + other.r)/2 - distance) / distance) * (other.tpos.x - tpos.x);
    tpos.y -= (((r + other.r)/2 - distance) / distance) * (other.tpos.y - tpos.y);
  }
  
  //分散
  void separate(Boid boid){  
    PVector sent = new PVector (0,0);
    sent.add(boid.tpos);
    sent.sub(tpos);
    sent.normalize();
    sent.mult(pow.x);
    sacc.sub(sent);
    save++;
  }
  
  //集合
  void alingment(Boid boid){
    PVector sent = new PVector (0,0);
    sent.add(boid.tpos);
    sent.sub(tpos);
    sent.normalize();
    sent.mult(pow.y);
    aacc.add(sent);
    aave++;
  }
  
  //整列
  void cohesion(Boid boid){
    PVector sent = boid.vec;
    sent.normalize();
    sent.mult(pow.z);
    cacc.add(sent);
    cave++;
  }
  
  abstract void find(Enemy enemy, float distance);
  void suffer(){
    exing = true;
  }
  
  //マウス
  void mousemove(int i){
    Effect effect = effects.get(i);
    PVector sent = new PVector (0,0);
    sent.add(effect.tpos);
    sent.sub(tpos);
    sent.normalize();
    sent.mult((255-effect.clit)*0.008);
    maxv = 2 + 4 * (255-effect.clit)/255;
    if (finding && maxv < 4)
      maxv = 4;
    if (effect.anticlick == false)
      sent.mult(-1);
    macc.add(sent);
  }
  
  void born(int i, int j){
    //drで死ぬ
    if (r >= dr){
      flag2 = true;
      shin = "老衰";
    }
    // 近くに三つ以上存在しない場合、死亡カウント開始
    if (i < 3){
      dt++;
      if (dt > 150+r*3)
        flag2 = true;     
        shin = "孤独";
    }else{
      dt = 0;
    } 
    //近くに三つ以上存在、最高存在個体数以下、十分成長で、生産カウント開始
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
  
  //成長する
  void grow(){
    r += 0.03;
  }
  
  //移動
  void move(){
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
    
    //何がノーマライズだクソが！
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
    
    //壁反射
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
  
  void reflectX(){
    vec.x *= -1;
    sacc.x *= -1;
    aacc.x *= -1;
    cacc.x *= -1;
  }
  void reflectY(){
    vec.y *= -1;
    sacc.y *= -1;
    aacc.y *= -1;
    cacc.y *= -1;
  }
  
  //作画
  void draw(){
    death(); 
    //text(tpos.x+","+tpos.y,pos.x+20,pos.y-20);
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
    }else if (tpos.y - camepos.y < -height/2){
      text("☝",pos.x,25);
    }else if(tpos.y - camepos.y > height/2){
      text("☟",pos.x,height-20);
    }else{
      image(jiki,pos.x-r/2,pos.y-r/2,r,r);
//      ellipse(pos.x,pos.y,r,r);
    }
  }
  
  void death(){
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
  void find(Enemy enemy, float distance){
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
  void collision(Enemy enemy, float distance){
    int damage = (int)random(3,5);
    tpos.x -= (((r + enemy.r)/2 - distance) / distance) * (enemy.tpos.x - tpos.x);
    tpos.y -= (((r + enemy.r)/2 - distance) / distance) * (enemy.tpos.y - tpos.y);
    reflectX();
    reflectY();
    damages.add(new Damage(pos, damage, damages.size()));
    enemy.hp -= damage;
  }
  
  void attack(Enemy enemy){
    PVector sent = new PVector (0,0);
    sent.add(enemy.tpos);
    sent.add(enemy.vec);
    sent.sub(tpos);
    sent.normalize();
    sent.mult(2);
    eacc.add(sent);
  }
  
  //プログラムミスって遠くのほうが力が強い
  void escape(Enemy enemy){
    PVector sent = new PVector (0,0);
    sent.add(enemy.tpos);
    sent.sub(tpos);
    sent.normalize();
    sent.mult(-3);
    eacc.add(sent);
  }
}  
class Knight{
  void attack(Enemy enemy){
    
  }
}
  
class Magician{
    
   void attack(){
  }
}