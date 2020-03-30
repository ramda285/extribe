class BGnumber{
  PVector tpos,pos;
  boolean flag = false;
  int num;
  int t;
  BGnumber(PVector _tpos){
    tpos = _tpos;
  }
  
  void act(){
    check();
    move();
    draw();
    t++;
  }
  
  void check(){
    if(t%5 == 0)
      num = (int)random(0,9);
    if(t >= 100)
      flag = true;
  }
  
  void move(){
    tpos.y -= 2;
    pos = new PVector (tpos.x-camepos.x,tpos.y-camepos.y);
  }
  
  void draw(){
    textSize(20);
    fill(50,255,50);
    text(""+num,pos.x,pos.y);
  }
}