class MainScene extends Scene{
  int t = 0;
  void firstScene(){
    babbles.add(new Babble(new PVector(0,0)));
  }
  
  void drawScene() {
    if (stop == true)
      return;
    //text("numberは"+boids.size(),width/2,height/2+40);
    //めんどくさいことにマウスとキーの設定
    //ここは後で直したい候補ナンバーワン
    if (mousePressed){
      mouseClicked();
      mousePressed = false;
    }
    if (keyPressed){
      keyPressed();
    }else{
      keyReleased();
    }
    
    //カメラのターン
    camepos.sub(camevec);
    
    //bgnumberのターン
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
    
    //babbleのターン
    for (int i = 0; i < babbles.size(); i++) {
      Babble babble = babbles.get(i);
      if (babble.flag == true){
        babbles.remove(i);
      }else{babble.act();}
    }
    
    //noticeのターン
    /*  はなかったことに
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
    
    //damageのターン
    for (int i = 0; i < damages.size(); i++) {
      Damage damage = damages.get(i);
      if (damage.flag == true){
        damages.remove(i);
      }else{damage.act();}
    }
   
   //effectのターン
    for (int i = 0; i < effects.size(); i++) {
      Effect effect = effects.get(i);
      if (effect.click){
        effect.make();
      }else{
        effects.remove(i);
      }
    }
      
    //boidsのターン
    perdist = new PVector(0,0);
    for (int i = 0; i < boids.size(); i++){
      Boid boid = boids.get(i);
      if (boid.flag == false){  //死亡フラグ
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
    
    //enemyのターン
    for (int i = 0; i < enemys.size(); i++){
      Enemy enemy = enemys.get(i);
      if (enemy.flag == false){
        enemy.act();
      }else{
        enemys.remove(i);
      }
    }
    
    //explosionのターン
    for (int i = 0; i < explosions.size(); i++){
      Explosion explosion = explosions.get(i);
      if (explosion.flag == false){
        explosion.act();
      }else{
        explosions.remove(i);
      }
    }
    
    //カメラによる移動は一気に行う
    for (int i = 0; i < boids.size(); i++){
      Boid boid = boids.get(i);
      boid.pos.add(camevec);
    }
    for (int i = 0; i < enemys.size(); i++){
      Enemy enemy = enemys.get(i);
      enemy.pos.add(camevec);
    }
    
    //ゲームオーバー判定
    textSize(15);
    text(camepos.x+""+camepos.y,width/2-20,height/2);
    if (boids.size()+babbles.size() == 0){
      fill(255,0,0);
      textSize(36);
      text("栄枯盛衰",width/2-50,height/2+50);
    }
    if (enemys.size()+babbles.size() == 0){
      fill(255,0,0);
      textSize(36);
      text("天下統一",width/2-50,height/2+50);
    }
    t++;
  }
  
  Scene decideScene(){
    return this;
  }
  
  //クリック時にEffectのフラグをON
  void mouseClicked(){
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
  
  void keyPressed() {
    if (key == CODED) {      // コード化されているキーが押された
      if (keyCode == RIGHT) {    // キーコードを判定
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
  
  void keyReleased(){
    camevec.x = 0;
    camevec.y = 0;
  }
}
