/*///////////////////////////////////////////////////////////
                        ボイドライフゲーム
           個体数を増やして敵と戦い、群れを成長させて
           次の階へ進むゲーム
           むしろライフゲーム成分は近くにいると産む
           離れると死ぬ程度しかないため
           くりキン成分やflOw成分が多くなってる希ガス
           最近になって勇なま成分も加えようとしてる
           もうこりゃだめかもしれんね
*////////////////////////////////////////////////////////////
Scene scene;
boolean stop = false;
int mapx,mapy;  //マップ最大値
PVector camepos = new PVector (0,0), camevec = new PVector (0,0);  //カメラ変数
int death_radius = 150 , born_radius = 50;  //死亡範囲と生存範囲
PVector mouse;  //ポジ取得
boolean noticeplus = false;  //あんまりつけたくない
PVector perdist;  //boid達の平均位置
PImage war,kni,gar,sho,mag,teki;  //画像
ArrayList<Boid> boids = new ArrayList<Boid>();
ArrayList<Effect> effects = new ArrayList<Effect>();
ArrayList<Enemy> enemys = new ArrayList<Enemy>();
ArrayList<Notice> notices = new ArrayList<Notice>();
ArrayList<Damage> damages = new ArrayList<Damage>();
ArrayList<Babble> babbles = new ArrayList<Babble>();
ArrayList<BGnumber> bgnumbers = new ArrayList<BGnumber>();
ArrayList<Explosion> explosions = new ArrayList<Explosion>();

void setup() {
  //fullScreen();
  scene = new MainScene();
  size(1400, 800);  //画面サイズ
  frameRate(30);  //フレーム
  background(0);  //まず最初に黒く塗りつぶす
  textSize(30);
  textFont(createFont("HiraKakuPro-W3", 30));
  war = loadImage("xchu.png");
  teki = loadImage("xchu.png");
    //たかがエフェクトに（ｒｙ
}

void draw(){
  //Blackfade();  //フェードアウト
  background(0);
  scene = scene.doScene();
}

//フェードアウト
void Blackfade(){
  noStroke();
  fill(0,150);
  rectMode(CORNER);
  rect(0, 0, width, height);
}

abstract class Scene{
  boolean first = true;
  Scene(){
  }
  Scene doScene(){
    if (scene.first){
      firstScene();
      scene.first = false;
    }
    drawScene();
    return decideScene();
  }
  abstract void firstScene();
  abstract void drawScene();
  abstract Scene decideScene();
  abstract void mouseClicked();
  abstract void keyPressed();
  abstract void keyReleased();
}

class Downstair{
}

//list データ構造　AllayList スーパークラス
