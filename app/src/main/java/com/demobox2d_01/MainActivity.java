package com.demobox2d_01;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.broadphase.DynamicTree;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.pooling.normal.DefaultWorldPool;

public class MainActivity extends AppCompatActivity {

    private final static int RATE = 10;//屏幕到现实世界的比例 10px:1m;这里要注意,当我们根据android当中的坐标去定义刚体的位置时,我们需要将坐标除以这个比例获得世界当中的长度,用这个长度来进行 定义。
    private AABB worldAABB;      //创建一个坐标系统
    private World world;         //创建一个世界
    private float timeStep;      //模拟的的频率
    private int iterations;      //迭代越大,模拟约精确,但性能越低
    private Body body, body1;     //创建刚体
    private MyView myView;       //我们自己定义的view,用来绘制出这个世界
    private Handler mHandler;

    private float density;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  //去title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density;


        //end


        // gravity
        Vec2 gravity = new Vec2(0.0f, 10.0f); //向量,用来标示当前世界的重力方向,第一个参数为水平方向,负数为左,正数为右。第二个参数表示垂直方向
        //IWorldPool
        DefaultWorldPool worldPool = new DefaultWorldPool(10, 20);
        //BroadPhaseStrategy
        DynamicTree dynamicTree = new DynamicTree();
        //创建这个世界的坐标范围,并且设定上下限,这里应该是按世界的长度来算的,也就是说这个范围是足够大的,我们只能在这个范围内创建刚体
        worldAABB = new AABB();
        worldAABB.lowerBound.set(-100.0f, -100.0f);
        worldAABB.upperBound.set(100.0f, 100.0f);
        dynamicTree.createProxy(worldAABB, new Object());
        //wrold
        world = new World(gravity, worldPool, dynamicTree);
        world.setSleepingAllowed(true);


        createBorder(0, 410 * density, 320 * density, 10 * density);//创建一个边界
        createBall(150, 50, 20, 1);          //创建两个球
        createBall(155, 100, 20, 2);
        myView = new MyView(this);
        timeStep = 1.0f / 60.0f;            //定义频率
        iterations = 10;                  //定义迭代
        setContentView(myView);
    }


    public void createBall(float x, float y, float radius, int i) {
        CircleShape shape = new CircleShape();
        shape.density = 7f;
        shape.friction = 11f;
        shape.restitution = 0.3f;
        shape.radius = radius / RATE;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / RATE, y / RATE);
        if (i == 1) {
            body = world.createBody(bodyDef);
            body.createShape(shape);
            body.setMassFromShapes();
        } else {
            body1 = world.createBody(bodyDef);
            body1.createShape(shape);
            body1.setMassFromShapes();
        }
    }


    public void createBorder(float x, float y, float half_width,
                             float half_height) {
        PolygonShape shape = new PolygonShape(); //标识刚体的形状
//        shape.density = 0;                       //设置刚体的密度,应为这个是底边界,所以密度设为0,相当于没有质量的物体不受力
//        shape.friction = 11f;                    //摩擦力,学过物理吧….恩,就是这个意思…
//        shape.restitution = 0.3f;                //弹力
        shape.setAsBox(half_width / RATE, half_height / RATE);   //设置刚体刚体的宽和高,要根据android坐标转换成世界当中的单位

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x / RATE, y / RATE);     //定义刚体的位置

        Body body1 = world.createBody(bodyDef);       //在世界中创建这个刚体
//        body1.createShape(shape);                     //刚体形状
//        body1.setMassFromShapes();                    //计算质量
        body1.createFixture(shape, 0);
    }
}
