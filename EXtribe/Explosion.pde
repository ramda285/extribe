class Explosion{
  PVector pos,vec,tpos;
  int t;
  boolean flag;
  Explosion(PVector _tpos, PVector _vec){
    tpos = _tpos;
    vec = _vec;
  }
  
  void act(){
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
