# Bluetooth-Assistant
做了一个仿小米运动的蓝牙助手app,长期增加功能进去


6个类,其中AcceptThread,ConnectedThread,为线程工具类.只有两个界面活动类,分别是MainActivity,DataActivity;然后ViewHandler 是为了实现异步改变UI , CircleTextView是我自定义的TextView.

大概思路是 通过 AcceptThread 连接 蓝牙设备 ,ConnectedThread 建立流通信.然后收到信息后 用本地广播 LocalBroadcast 进行本地全局 , 再触发 handler 来更改ui .

主界面那个转圈的小球用的是动画 animation 为了保证以某一圆心 旋转,所以其实 旋转的是一个 framlayout.而圆点 是一个位于framlayout左中间的button哈哈.
关于曲线坐标图,我用了一个挺不错的框架.https://github.com/QQ951127336/SmallChart

