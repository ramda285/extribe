//たかがエフェクトごときでクラスを使ってしまう自分が恨めしい
class Effect{
  PVector pos,tpos;
  float clit = 0;  //波紋の半径、透明度、影響をひとまとめ
  boolean click = false;  //flag
  boolean anticlick = false;  //白か赤か
  
  void make(){
    clit += 2;
    if (clit >= 240){
      clit = 0;
      click = false;
    }
    noFill();
    if(anticlick){
      //黒くしてるだけだからいずれテコ入れが必要
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
