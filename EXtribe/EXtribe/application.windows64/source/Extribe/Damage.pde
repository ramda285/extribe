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
  
  void act(){
    move();
    display();
    other();
    t++;
  }
  
  void move(){
    if(t<30){
      v = (15 - t)/6;
      tpos.y -= v;
    }
    if(t>80)
      flag = true;
}
  
  void display(){
    fill(255,255,0);
    text(damage,tpos.x,tpos.y);
  }
  
  void other(){}
}
