class Notice{
  //左上にキャラの画像と死因を書く機能
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
  void act(){
    image(graph,0,60*num);
    text("は"+shin+"により死にました"+num,30,25+60*num);
    t--;
    if (t <= 0)
      flag = true;
  }
  
}